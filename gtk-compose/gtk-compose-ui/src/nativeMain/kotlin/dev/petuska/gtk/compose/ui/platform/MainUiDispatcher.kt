package dev.petuska.gtk.compose.ui.platform

import dev.petuska.gtk.compose.ui.internal.Logger
import kotlinx.coroutines.*
import org.gtkkn.bindings.glib.Glib
import kotlin.coroutines.CoroutineContext

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
internal val MainUiThread: CloseableCoroutineDispatcher = newSingleThreadContext("GTK-Compose")

public val MainUiDispatcher: CoroutineDispatcher = GioMainDispatcher(Dispatchers.Default, false)

/**
 * Dispatcher for Gio event dispatching thread
 */
@OptIn(InternalCoroutinesApi::class)
private class GioMainDispatcher(
    val delegate: CoroutineDispatcher,
    private val invokeImmediately: Boolean
) : MainCoroutineDispatcher() {
    private val logger = Logger.withTag("GioMainDispatcher")
    override val immediate: MainCoroutineDispatcher =
        if (invokeImmediately) this else GioMainDispatcher(delegate, true)

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
