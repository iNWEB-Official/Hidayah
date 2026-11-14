package com.hidayah.app.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

/** Dates are stored as ISO yyyy-MM-dd in the user's device-local calendar. */
@Entity(tableName = "daily_deeds", primaryKeys = ["day", "deedId"])
data class DeedRecord(val day: String, val deedId: String, val completed: Boolean)

@Dao
interface DeedDao {
    @Query("SELECT * FROM daily_deeds WHERE day >= :first AND day <= :last")
    fun observeBetween(first: String, last: String): Flow<List<DeedRecord>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(record: DeedRecord)
}

@Database(entities = [DeedRecord::class], version = 1, exportSchema = false)
abstract class HidayahDatabase : RoomDatabase() {
    abstract fun deedDao(): DeedDao
    companion object {
        @Volatile private var instance: HidayahDatabase? = null
        fun getInstance(context: Context): HidayahDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context.applicationContext, HidayahDatabase::class.java, "hidayah-deeds.db")
                .build().also { instance = it }
        }
    }
}

class RoomTrackerRepository(private val dao: DeedDao) : TrackerRepository {
    override fun observeBetween(first: LocalDate, last: LocalDate): Flow<List<DeedRecord>> = dao.observeBetween(first.toString(), last.toString())
    override suspend fun setCompleted(day: LocalDate, id: String, completed: Boolean) {
        require(DeedDefinitions.any { it.id == id })
        dao.upsert(DeedRecord(day.toString(), id, completed))
    }
}
