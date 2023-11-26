package dev.petuska.gtk.compose.ui.window

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import dev.petuska.gtk.compose.ui.internal.GtkComposeInternalApi
import dev.petuska.gtk.compose.ui.node.ContentBuilder
import dev.petuska.gtk.compose.ui.node.GtkParentNode
import dev.petuska.gtk.compose.ui.node.setContent
import dev.petuska.gtk.compose.ui.platform.LocalApplication
import dev.petuska.gtk.compose.ui.platform.LocalWindow
import dev.petuska.gtk.compose.ui.platform.rememberLogger
import dev.petuska.gtk.compose.ui.util.ComponentUpdater
import org.gtkkn.bindings.gtk.Application
import org.gtkkn.bindings.gtk.Widget
import org.gtkkn.bindings.gtk.Window

@GtkComposeInternalApi
public open class WindowNode<TWidget : Window>(override val widget: TWidget) : GtkParentNode<TWidget>() {

    override fun add(child: Widget) {
        widget.child = child
    }

    override fun clear() {
        widget.child = null
    }
}

@Composable
public fun Window(
    visible: Boolean,
    title: String? = null,
    child: ContentBuilder<Window>
) {
    Window(
        visible = visible,
        title = title,
        create = { application ->
            WindowNode(Window().apply {
                setApplication(application)
            })
        },
        update = {
        },
        child = child,
    )
}

@Composable
public fun <TNode : WindowNode<*>> Window(
    visible: Boolean,
    title: String?,
    create: (application: Application) -> TNode,
    dispose: (node: TNode) -> Unit = {},
    update: ComponentUpdater.UpdateScope.(node: TNode) -> Unit,
    child: ContentBuilder<Window>
) {
    val logger = rememberLogger { "Window" }
    val application = LocalApplication.current
    val parentComposition = rememberCompositionContext()
    val node = remember {
        create(application)
    }
    val updater = remember(::ComponentUpdater)
    GtkWindow(
        visible = visible,
        create = {
            node.apply {
                setContent(parentComposition, logger) {
                    CompositionLocalProvider(LocalWindow provides node.widget) {
                        child()
                    }
                }
            }
        },
        dispose = { n ->
            dispose(n)
            n.widget.close()
        },
        update = { n ->
            updater.update {
                set(title) { n.widget.title = it }
                update(n)
            }
        },
    )
}
