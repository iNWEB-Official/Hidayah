package com.hidayah.app.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.time.LocalDate

/** One document per device-local date, observed live while Home is subscribed. */
class FirestoreDailyValueRepository : DailyValueRepository {
    override fun observe(day: LocalDate): Flow<DailyValueStatus> = callbackFlow {
        trySend(DailyValueStatus.Loading)
        val registration = try {
            FirebaseFirestore.getInstance().collection("daily_values").document(day.toString())
                .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                    if (error != null) {
                        trySend(DailyValueStatus.Error("Unable to load today's value. Check your connection, Firestore API/database and read rules."))
                    } else {
                        trySend(dailyValueFromSnapshot(snapshot?.exists(), snapshot?.metadata?.isFromCache == true, snapshot?.data.orEmpty()))
                    }
                }
        } catch (_: Exception) {
            trySend(DailyValueStatus.Error("Firebase is not ready. Check the project configuration and Firestore setup."))
            close()
            null
        }
        awaitClose { registration?.remove() }
    }
}
