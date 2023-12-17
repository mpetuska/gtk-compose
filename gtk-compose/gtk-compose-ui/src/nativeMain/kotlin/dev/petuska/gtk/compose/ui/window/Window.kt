package dev.petuska.gtk.compose.ui.window

import androidx.compose.runtime.*
import dev.petuska.gtk.compose.ui.internal.GtkComposeInternalApi
import dev.petuska.gtk.compose.ui.node.*
import dev.petuska.gtk.compose.ui.platform.LocalApplication
import dev.petuska.gtk.compose.ui.platform.LocalWindow
import dev.petuska.gtk.compose.ui.platform.rememberLogger
import dev.petuska.gtk.compose.ui.props.*
import dev.petuska.gtk.compose.ui.util.ComponentUpdater
import org.gtkkn.bindings.gdk.Display
import org.gtkkn.bindings.gtk.*
import kotlin.Unit

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

// region properties
/**
 * @see Window.application
 */
public var PropsScope<out Window>.application: Application? by Window::application

/**
 * @see Window.child
 */
public var PropsScope<out Window>.child: Widget? by Window::child

/**
 * @see Window.decorated
 */
public var PropsScope<out Window>.decorated: Boolean by Window::decorated

/**
 * @see Window.setDefaultSize
 */
public fun PropsScope<out Window>.defaultSize(width: Int, height: Int) {
    setProperty("defaultSize", width to height) {
        widget.setDefaultSize(it.first, it.second)
    }
}

/**
 * @see Window.defaultWidget
 */
public var PropsScope<out Window>.defaultWidget: Widget? by Window::defaultWidget

/**
 * @see Window.deletable
 */
public var PropsScope<out Window>.deletable: Boolean by Window::deletable

/**
 * @see Window.destroyWithParent
 */
public var PropsScope<out Window>.destroyWithParent: Boolean by Window::destroyWithParent

/**
 * @see Window.getDisplay
 * @see Window.setDisplay
 */
public var PropsScope<out Window>.display: Display by SyntheticProperty(
    get = Window::getDisplay,
    set = Window::setDisplay,
)

/**
 * @see Window.focusVisible
 */
public var PropsScope<out Window>.focusVisible: Boolean by Window::focusVisible

/**
 * @see Window.handleMenubarAccel
 */
public var PropsScope<out Window>.handleMenubarAccel: Boolean by Window::handleMenubarAccel

/**
 * @see Window.hideOnClose
 */
public var PropsScope<out Window>.hideOnClose: Boolean by Window::hideOnClose

/**
 * @see Window.iconName
 */
public var PropsScope<out Window>.iconName: String? by Window::iconName

/**
 * @see Window.mnemonicsVisible
 */
public var PropsScope<out Window>.mnemonicsVisible: Boolean by Window::mnemonicsVisible

/**
 * @see Window.modal
 */
public var PropsScope<out Window>.modal: Boolean by Window::modal

/**
 * @see Window.resizable
 */
public var PropsScope<out Window>.resizable: Boolean by Window::resizable

/**
 * @see Window.title
 */
public var PropsScope<out Window>.title: String? by Window::title

/**
 * @see Window.titlebar
 */
public var PropsScope<out Window>.titlebar: Widget? by Window::titlebar

/**
 * @see Window.transientFor
 */
public var PropsScope<out Window>.transientFor: Window? by Window::transientFor
// endregion

// region signals
/**
 * @see Window.connectActivateDefault
 */
public var PropsScope<out Window>.onActivateDefault: () -> Unit by signal { widget.connectActivateDefault(handler = it) }

/**
 * @see Window.connectActivateFocus
 */
public var PropsScope<out Window>.onActivateFocus: () -> Unit by signal { widget.connectActivateFocus(handler = it) }

/**
 * @see Window.connectCloseRequest
 */
public var PropsScope<out Window>.onCloseRequest: () -> Boolean by signal { widget.connectCloseRequest(handler = it) }

/**
 * @see Window.connectEnableDebugging
 */
public var PropsScope<out Window>.onEnableDebugging: (toggle: Boolean) -> Boolean by signal {
    widget.connectEnableDebugging(handler = it)
}
// endregion

/**
 * @see Window
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
    val node = remember { create(application) }
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
        // Must be updated together with [GtkNode]
        update = {
            val propsScope = PropsScope(it)
            props.invoke(propsScope)

            refEffect = propsScope.refEffect

            updater.update {
                set(propsScope.properties) { properties ->
                    logger.d { "Updating properties" }
                    properties.forEach { (key, property) ->
                        logger.v { "Updating property[$key]" }
                        property.updater(node, property.value)
                    }
                }
                set(propsScope.signals) { signals ->
                    logger.d { "Updating signals" }
                    val newSignals = signals.map { (key, signal) ->
                        logger.v { "Updating signal[$key]" }
                        signal.connector(node, signal.handler)
                    }
                    node.signals = newSignals
                }
            }
        },
    )
}
