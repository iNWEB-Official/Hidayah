package com.hidayah.app.data

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.atomic.AtomicLong

enum class AccountRole { USER, ADMIN, UNVERIFIED }
data class AccountProfile(val uid: String, val email: String, val emailVerified: Boolean, val role: AccountRole)

/** Never retain a previous role while the current token is being checked. */
fun pendingAccountProfile(uid: String, email: String, emailVerified: Boolean): AccountProfile =
    AccountProfile(uid, email, emailVerified, AccountRole.UNVERIFIED)
fun isCurrentAccountRequest(request: Long, latest: Long, uid: String, currentUid: String?): Boolean =
    request == latest && uid == currentUid

/** Display only; privileged operations must independently enforce trusted server-side rules. */
fun accountRole(verified: Boolean, claims: Map<String, Any>?): AccountRole = when {
    !verified || claims == null -> AccountRole.UNVERIFIED
    claims["admin"] == true && claims["email_verified"] == true -> AccountRole.ADMIN
    else -> AccountRole.USER
}

interface AccountRepository {
    fun observe(): Flow<AccountProfile?>
    suspend fun signIn(email: String, password: String)
    suspend fun register(email: String, password: String)
    suspend fun resetPassword(email: String)
    suspend fun verifyEmail()
    suspend fun refresh()
    fun signOut()
}

class FirebaseAccountRepository : AccountRepository {
    private val auth: FirebaseAuth get() = FirebaseAuth.getInstance()
    override fun observe(): Flow<AccountProfile?> = callbackFlow {
        var pending: Job? = null
        val generation = AtomicLong()
        val listener = FirebaseAuth.IdTokenListener { firebase ->
            val request = generation.incrementAndGet()
            pending?.cancel()
            val user = firebase.currentUser
            if (user == null) {
                trySend(null)
            } else {
                trySend(pendingAccountProfile(user.uid, user.email.orEmpty(), user.isEmailVerified))
                pending = launch {
                    val claims = try { user.getIdToken(false).await().claims }
                    catch (e: CancellationException) { throw e }
                    catch (_: Exception) { null }
                    if (isCurrentAccountRequest(request, generation.get(), user.uid, firebase.currentUser?.uid)) {
                        trySend(AccountProfile(user.uid, user.email.orEmpty(), user.isEmailVerified, accountRole(user.isEmailVerified, claims)))
                    }
                }
            }
        }
        auth.addIdTokenListener(listener)
        awaitClose { pending?.cancel(); auth.removeIdTokenListener(listener) }
    }
    override suspend fun signIn(email: String, password: String) { auth.signInWithEmailAndPassword(email, password).await() }
    override suspend fun register(email: String, password: String) { auth.createUserWithEmailAndPassword(email, password).await() }
    override suspend fun resetPassword(email: String) { auth.sendPasswordResetEmail(email).await() }
    override suspend fun verifyEmail() { (auth.currentUser ?: error("No account")).sendEmailVerification().await() }
    override suspend fun refresh() {
        val user = auth.currentUser ?: error("No account")
        user.reload().await()
        user.getIdToken(true).await()
    }
    override fun signOut() { auth.signOut() }
}
