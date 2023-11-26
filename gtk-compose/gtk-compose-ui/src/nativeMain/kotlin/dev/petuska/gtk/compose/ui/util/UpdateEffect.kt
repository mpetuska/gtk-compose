package dev.petuska.gtk.compose.ui.util

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateObserver
import kotlinx.coroutines.channels.Channel


/**
 * When [UpdateEffect] enters the composition it will call [update] and will capture all state
 * which is used in this function.
 *
 * When any state is changed, [update] will be called again on the next recomposition.
 *
 * [update] always be called in UI thread.
 */
@Composable
internal fun UpdateEffect(update: () -> Unit) {
    val tasks = remember { Channel<() -> Unit>(Channel.RENDEZVOUS) }
    val currentUpdate by rememberUpdatedState(update)

    LaunchedEffect(Unit) {
        for (task in tasks) {
            task()
        }
    }

    DisposableEffect(Unit) {
        val snapshotObserver = SnapshotStateObserver { command ->
            command()
        }
        snapshotObserver.start()

        lateinit var sendUpdate: (Unit) -> Unit
        fun performUpdate() {
            snapshotObserver.observeReads(
                Unit,
                onValueChangedForScope = sendUpdate,
            ) {
                currentUpdate()
            }
        }
        sendUpdate = { tasks.trySend(::performUpdate) }

        performUpdate()

        onDispose {
            snapshotObserver.stop()
            snapshotObserver.clear()
        }
    }
}
