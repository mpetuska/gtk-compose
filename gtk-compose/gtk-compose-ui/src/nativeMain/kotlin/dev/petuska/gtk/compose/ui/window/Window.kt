package dev.petuska.gtk.compose.ui.window

import androidx.compose.runtime.*
import dev.petuska.gtk.compose.ui.internal.GtkComposeInternalApi
import dev.petuska.gtk.compose.ui.node.*
import dev.petuska.gtk.compose.ui.platform.LocalApplication
import dev.petuska.gtk.compose.ui.platform.LocalWindow
import dev.petuska.gtk.compose.ui.platform.rememberLogger
import dev.petuska.gtk.compose.ui.props.PropsScope
import dev.petuska.gtk.compose.ui.props.prop
import dev.petuska.gtk.compose.ui.util.ComponentUpdater
import org.gtkkn.bindings.gtk.Application
import org.gtkkn.bindings.gtk.Widget
import org.gtkkn.bindings.gtk.Window

@GtkComposeInternalApi
internal open class WindowNode<TWidget : Window>(override val widget: TWidget) : GtkParentNode<TWidget>() {
    private var isClosed: Boolean = false
    override fun add(child: Widget) {
        widget.child = child
    }

    override fun clear() {
        widget.child = null
    }

    override fun close() {
        if (!isClosed) {
            widget.close()
            isClosed = true
        }
    }
}

public var PropsScope<out Window>.title: String? by prop { widget.title = it }
public var PropsScope<out Window>.decorated: Boolean by prop { widget.decorated = it }
public var PropsScope<out Window>.deletable: Boolean by prop { widget.deletable = it }

/**
 *
 */
@Composable
public fun Window(
    onCloseRequest: () -> Unit,
    visible: Boolean,
    props: PropsScope<Window>.() -> Unit,
    child: ContentBuilder<Window>,
) {
    Window(
        onCloseRequest = onCloseRequest,
        visible = visible,
        create = { application ->
            WindowNode(Window().apply {
                setApplication(application)
            })
        },
        dispose = {},
        props = props,
        child = child,
    )
}

@Composable
internal fun <TWidget : Window, TNode : GtkContainerNode<TWidget>> Window(
    onCloseRequest: () -> Unit,
    visible: Boolean,
    create: (application: Application) -> TNode,
    dispose: (node: TNode) -> Unit,
    props: PropsScope<TWidget>.() -> Unit,
    child: ContentBuilder<Window>,
) {
    val logger = rememberLogger { "Window" }
    val currentOnCloseRequest by rememberUpdatedState(onCloseRequest)

    val application = LocalApplication.current
    val parentComposition = rememberCompositionContext()
    val node = remember {
        create(application)
    }
    val updater = remember(::ComponentUpdater)

    var refEffect: (DisposableEffectScope.(GtkNode<TWidget>) -> DisposableEffectResult)? = null
    GtkWindow(
        visible = visible,
        create = {
            node.apply {
                setContent(parentComposition, logger) {
                    CompositionLocalProvider(LocalWindow provides this@apply.widget) {
                        refEffect?.let { effect ->
                            DisposableEffect(Unit) {
                                effect.invoke(this@DisposableEffect, this@apply)
                            }
                        }
                        child.invoke(this@setContent)
                    }
                }
                widget.connectCloseRequest {
                    currentOnCloseRequest()
                    false
                }
            }
        },
        dispose = { window ->
            dispose(window)
            window.close()
        },
        update = {
            val propsScope = PropsScope<TWidget>()
            props.invoke(propsScope)

            refEffect = propsScope.refEffect
            updater.update {
                propsScope.updates.forEach { (_, update) ->
                    set(update.value) { update.updater(node, it) }
                }
            }
        },
    )
}
