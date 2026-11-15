package com.hidayah.app.data

/** Serialize token event publication and reject results from an older event. */
internal class AccountRequestGuard {
    private var generation = 0L
    private val lock = Any()

    fun onEvent(block: (Long) -> Unit) = synchronized(lock) { block(++generation) }
    fun invalidate(block: () -> Unit) = synchronized(lock) { generation++; block() }
    fun publishIfCurrent(request: Long, uid: String, currentUid: () -> String?, publish: () -> Unit): Boolean = synchronized(lock) {
        if (!isCurrentAccountRequest(request, generation, uid, currentUid())) false
        else { publish(); true }
    }
}
