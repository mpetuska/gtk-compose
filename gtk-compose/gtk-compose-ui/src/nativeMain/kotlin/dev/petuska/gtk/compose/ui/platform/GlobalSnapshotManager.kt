package dev.petuska.gtk.compose.ui.platform

import androidx.compose.runtime.snapshots.Snapshot
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlin.concurrent.AtomicInt

internal object GlobalSnapshotManager {
    private val started = AtomicInt(0)
    private val sent = AtomicInt(0)

    fun ensureStarted() {
        if (started.compareAndSet(0, 1)) {
            val channel = Channel<Unit>(1)
            CoroutineScope(GlobalSnapshotManagerDispatcher).launch {
                channel.consumeEach {
                    sent.compareAndSet(1, 0)
                    Snapshot.sendApplyNotifications()
                }
            }
            Snapshot.registerGlobalWriteObserver {
                if (sent.compareAndSet(0, 1)) {
                    channel.trySend(Unit)
                }
            }
        }
    }
}

internal val GlobalSnapshotManagerDispatcher: CoroutineDispatcher = MainUiDispatcher
