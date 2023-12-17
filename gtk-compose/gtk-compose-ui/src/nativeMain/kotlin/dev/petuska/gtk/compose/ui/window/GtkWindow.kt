package dev.petuska.gtk.compose.ui.window

import androidx.compose.runtime.*
import dev.petuska.gtk.compose.ui.node.GtkContainerNode
import dev.petuska.gtk.compose.ui.platform.MainUiDispatcher
import dev.petuska.gtk.compose.ui.platform.rememberLogger
import dev.petuska.gtk.compose.ui.util.Ref
import dev.petuska.gtk.compose.ui.util.UpdateEffect
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.gtkkn.bindings.gtk.Window


/**
 * Compose [GtkWindow] obtained from [create]. The [create] block will be called
 * exactly once to obtain the [Window] to be composed, and it is also guaranteed to be invoked on
 * the UI thread (Event Dispatch Thread).
 *
 * Once [GtkWindow] leaves the composition, [dispose] will be called to free resources that
 * obtained by the [Window].
 *
 * The [update] block can be run multiple times (on the UI thread as well) due to recomposition,
 * and it is the right place to set [Window] properties depending on state.
 * When state changes, the block will be re-executed to set the new properties.
 * Note the block will also be run once right after the [create] block completes.
 *
 * [GtkWindow] is needed for creating window's / dialog's that still can't be created with
 * the default Compose functions [dev.petuska.gtk.compose.ui.window.Window] or
 * [dev.petuska.gtk.compose.ui.window.DialogWindow].
 *
 * @param visible Is [Window] visible to user.
 * Note that if we set `false` - native resources will not be released. They will be released
 * only when [Window] will leave the composition.
 * @param create The block creating the [Window] to be composed.
 * @param dispose The block to dispose [Window] and free native resources. Usually it is simple
 * `Window::dispose`
 * @param update The callback to be invoked after the layout is inflated.
 */
@OptIn(DelicateCoroutinesApi::class)
@Composable
internal fun <TNode : GtkContainerNode<Window>> GtkWindow(
    visible: Boolean = true,
    create: () -> TNode,
    dispose: (TNode) -> Unit,
    update: (TNode) -> Unit = {}
) {
    val windowRef = remember { Ref<TNode>() }
    fun window() = windowRef.value!!

    val logger = rememberLogger(windowRef.value) { "${windowRef.value ?: "GtkWindow"}" }

    DisposableEffect(Unit) {
        windowRef.value = create()
        onDispose {
            dispose(window())
        }
    }

    UpdateEffect {
        val window = window()
        update(window)
    }

    DisposableEffect(visible) {
        // Why we dispatch showing in the next AWT tick:
        //
        // 1.
        // window.isVisible = true can be a blocking operation.
        // So we have to schedule it outside the Compose render frame.
        //
        // This happens in when we create a modal dialog.
        // When we call `window.isVisible = true`, internally a new AWT event loop will be created,
        // which will handle all the future Swing events while dialog is visible.
        //
        // We can't show the window directly inside LaunchedEffect or rememberCoroutineScope because
        // their dispatcher is controlled by the Compose rendering loop (ComposeScene.dispatcher)
        // and we will block the coroutine.
        //
        // 2.
        // We achieve the correct order when we open nested window at the same time when we open the
        // parent window. If we had shown the window immediately we would have had this sequence in
        // case of nested windows:
        //
        // 1. window1.setContent
        // 2. window2.setContent
        // 3. window2.isVisible = true
        // 4. window1.isVisible = true
        //
        // So we will have the wrong window active (window1).
        logger.d { "Scheduling show job" }
        val showJob = GlobalScope.launch(MainUiDispatcher) {
            val window = window().widget
            if (visible) {
                logger.v { "Presenting" }
                window.present()
            } else {
                println("Hiding")
                window.hide()
            }
        }
        logger.v { "Show job scheduled" }
        onDispose {
            logger.d { "Cancelling show job" }
            showJob.cancel()
        }
    }
}
