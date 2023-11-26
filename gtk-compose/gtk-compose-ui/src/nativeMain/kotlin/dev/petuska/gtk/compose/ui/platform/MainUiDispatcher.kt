package dev.petuska.gtk.compose.ui.platform

import co.touchlab.kermit.Logger
import kotlinx.coroutines.*
import org.gtkkn.bindings.glib.Glib
import kotlin.coroutines.CoroutineContext

/**
 * Dedicated thread to bootstrap GTK. After bootstrapping,
 * GTK is meant to fully own the thread and as such no work on it should block the thread nor be executed directly.
 *
 * To interract with GTK on the main thread use [MainUiDispatcher]
 *
 * @see MainUiDispatcher
 * @see Dispatchers.Main
 */
@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
internal val MainUiThread: CloseableCoroutineDispatcher = newSingleThreadContext("GTK-Compose")

/**
 * Alias for [Dispatchers.Main].
 * Work dispatched via this dispatcher is guaranteed to be executed on GTK main/UI thread.
 */
public inline val MainUiDispatcher: CoroutineDispatcher get() = Dispatchers.Main

/**
 * Dispatcher for GIO event dispatching thread.
 *
 * It delegates work to the GIO event loop which not only is thread-safe,
 * but also guarantees that scheduled work will be executed on the main GIO thread.
 *
 * @see Glib.idleAdd
 */
@OptIn(InternalCoroutinesApi::class)
internal class GioMainDispatcher(
    private val delegate: CoroutineDispatcher,
    private val invokeImmediately: Boolean,
    logger: Logger,
) : MainCoroutineDispatcher() {
    private val logger = logger.withTag("GioMainDispatcher")
    override val immediate: MainCoroutineDispatcher =
        if (invokeImmediately) this else GioMainDispatcher(delegate, true, logger)

    override fun dispatch(context: CoroutineContext, block: Runnable) = delegate.dispatch(context, Runnable {
        logger.v { "Dispatching to GLib event loop" }
        Glib.idleAdd(
            priority = Glib.PRIORITY_HIGH_IDLE,
            function = {
                block.run()
                false
            },
        )
    })

    override fun isDispatchNeeded(context: CoroutineContext): Boolean = !invokeImmediately

    override fun dispatchYield(context: CoroutineContext, block: Runnable) = delegate.dispatchYield(context, block)
    override fun toString(): String = toStringInternalImpl() ?: delegate.toString()
}
