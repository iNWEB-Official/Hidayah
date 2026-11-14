package com.hidayah.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hidayah.app.data.AccountProfile
import com.hidayah.app.data.AccountRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AccountUiState(
    val profile: AccountProfile? = null,
    val loading: Boolean = true,
    val busy: Boolean = false,
    val message: String? = null,
)

class AccountViewModel(private val repository: AccountRepository) : ViewModel() {
    private val mutableState = MutableStateFlow(AccountUiState())
    val state: StateFlow<AccountUiState> = mutableState.asStateFlow()
    init {
        viewModelScope.launch {
            repository.observe()
                .catch { mutableState.update { it.copy(loading = false, profile = null, message = "অ্যাকাউন্ট তথ্য পাওয়া যায়নি। পরে আবার চেষ্টা করুন।") } }
                .collect { profile -> mutableState.update { it.copy(loading = false, profile = profile) } }
        }
    }
    private fun perform(message: String, action: suspend () -> Unit) {
        if (mutableState.value.busy) return
        mutableState.update { it.copy(busy = true, message = null) }
        viewModelScope.launch {
            try { action(); mutableState.update { it.copy(busy = false, message = message) } }
            catch (e: CancellationException) { throw e }
            catch (_: Exception) { mutableState.update { it.copy(busy = false, message = "অনুরোধ সম্পন্ন হয়নি। তথ্য, ইন্টারনেট ও অ্যাকাউন্ট সেটিংস যাচাই করুন।") } }
        }
    }
    fun signIn(email: String, password: String) = perform("লগইন সম্পন্ন হয়েছে।") { repository.signIn(email.trim(), password) }
    fun register(email: String, password: String) = perform("অ্যাকাউন্ট তৈরি হয়েছে। ইমেইল যাচাই করতে নিচের বাটন ব্যবহার করুন।") { repository.register(email.trim(), password) }
    fun reset(email: String) = perform("অ্যাকাউন্ট থাকলে পাসওয়ার্ড রিসেটের নির্দেশনা ইমেইলে পাঠানো হবে।") { repository.resetPassword(email.trim()) }
    fun verify() = perform("যাচাই ইমেইল পাঠানোর অনুরোধ সম্পন্ন হয়েছে।") { repository.verifyEmail() }
    fun refresh() = perform("অ্যাকাউন্ট তথ্য হালনাগাদ করা হয়েছে।") { repository.refresh() }
    fun signOut() = perform("লগআউট করা হয়েছে।") { repository.signOut() }
}
