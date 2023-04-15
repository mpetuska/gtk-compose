package dev.petuska.gtk.compose.runtime

import androidx.compose.runtime.*
import dev.petuska.gtk.compose.runtime.content.WidgetScope
import dev.petuska.gtk.compose.runtime.internal.GlobalSnapshotManager
import dev.petuska.gtk.compose.runtime.internal.GtkComposeInternalApi
import dev.petuska.gtk.compose.runtime.internal.WidgetApplier
import dev.petuska.gtk.compose.runtime.internal.WidgetWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.gtkkn.bindings.gtk.Application
import org.gtkkn.bindings.gtk.ApplicationWindow
import org.gtkkn.bindings.gtk.Widget
import org.gtkkn.bindings.gtk.Window

/**
 * Use this method to mount the composition at the certain [root]
 *
 * @param root - the [Widget] that will be the root of the GTK tree managed by Compose
 * @param content - the Composable lambda that defines the composition content
 *
 * @return the instance of the [Composition]
 */
@OptIn(GtkComposeInternalApi::class)
public fun <TWidget : Widget> renderComposable(
    root: TWidget,
    monotonicFrameClock: MonotonicFrameClock = DefaultMonotonicFrameClock,
    content: @Composable WidgetScope<TWidget>.() -> Unit
): Composition {
    GlobalSnapshotManager.ensureStarted()
    val context = monotonicFrameClock + Dispatchers.Default
    val recomposer = Recomposer(context)

    CoroutineScope(context).launch(start = CoroutineStart.UNDISPATCHED) {
        recomposer.runRecomposeAndApplyChanges()
    }

    val composition = ControlledComposition(
        applier = WidgetApplier(WidgetWrapper(root)),
        parent = recomposer
    )
    val scope = object : WidgetScope<TWidget> {
        override val DisposableEffectScope.scopeElement: TWidget
            get() = root
    }
    composition.setContent @Composable {
        content(scope)
    }
    return composition
}


/**
 * Use this method to mount the composition at the [Application].
 *
 * @param content - the Composable lambda that defines the composition content
 *
 * @return the instance of the [Composition]
 */
public fun Application.renderComposable(
    content: @Composable WidgetScope<ApplicationWindow>.() -> Unit
): Composition {
    val window = ApplicationWindow(this@renderComposable)
    window.present()

    return renderComposable(
        root = window,
        content = content
    )
}
