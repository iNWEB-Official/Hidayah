package com.hidayah.app

import com.hidayah.app.data.*
import com.hidayah.app.ui.AccountViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AccountViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private class FakeAccountRepository : AccountRepository {
        val profile = MutableStateFlow<AccountProfile?>(null)
        var lastEmail: String? = null
        var fail = false
        var failObserve = false
        var failReset = false
        override fun observe(): Flow<AccountProfile?> {
            if (failObserve) error("observe failed")
            return profile
        }
        override suspend fun signIn(email: String, password: String) {
            if (fail) error("invalid credentials")
            lastEmail = email
            profile.value = AccountProfile("id", email, true, AccountRole.USER)
        }
        override suspend fun register(email: String, password: String) {
            if (fail) error("unavailable")
            profile.value = AccountProfile("id", email, false, AccountRole.UNVERIFIED)
        }
        override suspend fun resetPassword(email: String) { if (failReset) error("reset failed"); lastEmail = email }
        override suspend fun verifyEmail() = Unit
        override suspend fun refresh() = Unit
        override fun signOut() { profile.value = null }
    }
    @Before fun setup() { Dispatchers.setMain(dispatcher) }
    @After fun teardown() { Dispatchers.resetMain() }

    @Test fun verifiedAdminRequiresTrustedClaimAndVerifiedToken() {
        assertEquals(AccountRole.ADMIN, accountRole(true, mapOf("admin" to true, "email_verified" to true)))
        assertEquals(AccountRole.USER, accountRole(true, mapOf("admin" to "true", "email_verified" to true)))
        assertEquals(AccountRole.USER, accountRole(true, mapOf("admin" to true, "email_verified" to false)))
        assertEquals(AccountRole.UNVERIFIED, accountRole(false, mapOf("admin" to true, "email_verified" to true)))
        assertEquals(AccountRole.UNVERIFIED, accountRole(true, null))
    }
    @Test fun pendingProfileAndStaleTokenRequestsFailClosed() {
        val pending = pendingAccountProfile("u1", "user@example.com", true)
        assertEquals(AccountRole.UNVERIFIED, pending.role)
        assertFalse(isCurrentAccountRequest(1, 2, "u1", "u1"))
        assertFalse(isCurrentAccountRequest(2, 2, "u1", "u2"))
        assertFalse(isCurrentAccountRequest(2, 2, "u1", null))
        assertTrue(isCurrentAccountRequest(2, 2, "u1", "u1"))
        assertEquals(AccountRole.UNVERIFIED, accountRole(true, null))
    }
    @Test fun signInAndSignOutObserveProfileWithoutChangingCoreAccess() = runTest(dispatcher) {
        val repo = FakeAccountRepository(); val vm = AccountViewModel(repo)
        runCurrent()
        assertFalse(vm.state.value.loading)
        assertNull(vm.state.value.profile)
        vm.signIn("  user@example.com ", "password"); runCurrent()
        assertEquals("user@example.com", repo.lastEmail)
        assertEquals(AccountRole.USER, vm.state.value.profile?.role)
        vm.signOut(); runCurrent()
        assertNull(vm.state.value.profile)
    }
    @Test fun registerDoesNotMakeUserAnAdmin() = runTest(dispatcher) {
        val repo = FakeAccountRepository(); val vm = AccountViewModel(repo)
        runCurrent(); vm.register("user@example.com", "password"); runCurrent()
        assertEquals(AccountRole.UNVERIFIED, vm.state.value.profile?.role)
        assertFalse(vm.state.value.profile!!.emailVerified)
    }
    @Test fun failedLoginUsesGenericErrorAndClearsBusy() = runTest(dispatcher) {
        val repo = FakeAccountRepository().apply { fail = true }; val vm = AccountViewModel(repo)
        runCurrent(); vm.signIn("user@example.com", "secret"); runCurrent()
        assertFalse(vm.state.value.busy)
        assertNotNull(vm.state.value.message)
        assertFalse(vm.state.value.message!!.contains("secret"))
        assertNull(vm.state.value.profile)
    }
    @Test fun observerFailureCanBeRetriedWithoutMisrepresentingGuestState() = runTest(dispatcher) {
        val repo = FakeAccountRepository().apply { failObserve = true }
        val vm = AccountViewModel(repo)
        runCurrent()
        assertFalse(vm.state.value.loading)
        assertTrue(vm.state.value.observeFailed)
        repo.failObserve = false
        repo.profile.value = AccountProfile("id", "user@example.com", true, AccountRole.USER)
        vm.retryObservation(); runCurrent()
        assertFalse(vm.state.value.observeFailed)
        assertEquals("user@example.com", vm.state.value.profile?.email)
        assertNull(vm.state.value.message)
    }
    @Test fun resetSuccessAndFailureUseTheSameNeutralMessage() = runTest(dispatcher) {
        val repo = FakeAccountRepository()
        val vm = AccountViewModel(repo)
        runCurrent(); vm.reset("user@example.com"); runCurrent()
        val successMessage = vm.state.value.message
        repo.failReset = true
        vm.reset("user@example.com"); runCurrent()
        assertEquals(successMessage, vm.state.value.message)
        assertFalse(vm.state.value.busy)
    }
    @Test fun resetRequestTrimsEmail() = runTest(dispatcher) {
        val repo = FakeAccountRepository(); val vm = AccountViewModel(repo)
        runCurrent(); vm.reset("  user@example.com "); runCurrent()
        assertEquals("user@example.com", repo.lastEmail)
        assertFalse(vm.state.value.busy)
    }
}
