package dev.petuska.gtk.compose.ui.node

import androidx.compose.runtime.*
import dev.petuska.gtk.compose.ui.internal.GtkComposeInternalApi
import dev.petuska.gtk.compose.ui.internal.GtkNodeApplier
import dev.petuska.gtk.compose.ui.platform.rememberLogger
import dev.petuska.gtk.compose.ui.props.PropsBuilder
import dev.petuska.gtk.compose.ui.props.PropsScope
import org.gtkkn.bindings.gobject.Gobject
import org.gtkkn.bindings.gtk.Widget

@GtkComposeInternalApi
public typealias AnyGtkNode = GtkNode<Widget>

@OptIn(ExperimentalStdlibApi::class)
@GtkComposeInternalApi
public sealed class GtkNode<out TWidget : Widget> : AutoCloseable {
    internal var signals: List<ULong> = emptyList()
        set(value) {
            field.forEach {
                Gobject.signalHandlerDisconnect(widget, it)
            }
            field = value
        }

    public abstract val widget: TWidget

    override fun close() {
        signals = emptyList()
    }

    override fun toString(): String = widget.name
}

@Composable
@GtkComposeInternalApi
public inline fun <TWidget : Widget, TNode : GtkNode<TWidget>> GtkNode(
    update: @DisallowComposableCalls Updater<TNode>.() -> Unit,
    noinline factory: () -> TNode,
) {
    val scope = remember { LazyNodeScope<TWidget>() }

    val aFactory = { factory().also { scope.node = it } }

    ComposeNode<TNode, GtkNodeApplier>(
        factory = aFactory,
        update = update,
    )
}

@Composable
@GtkComposeInternalApi
public inline fun <TWidget : Widget, TNode : GtkNode<TWidget>> GtkNode(
    update: @DisallowComposableCalls Updater<TNode>.() -> Unit,
    crossinline content: ContentBuilder<TWidget> = {},
    noinline factory: () -> TNode,
) {
    val scope = remember { LazyNodeScope<TWidget>() }

    val aFactory = { factory().also { scope.node = it } }
    val aContent = @Composable { content.invoke(scope) }

    ComposeNode<TNode, GtkNodeApplier>(
        factory = aFactory,
        update = update,
        content = aContent,
    )
}

@Composable
@GtkComposeInternalApi
public inline fun <TWidget : Widget, TNode : GtkNode<TWidget>> GtkNode(
    update: @DisallowComposableCalls Updater<TNode>.() -> Unit,
    noinline skippableUpdate: ComposableBuilder<SkippableUpdater<TNode>>,
    crossinline content: ContentBuilder<TWidget> = {},
    noinline factory: () -> TNode,
) {
    val scope = remember { LazyNodeScope<TWidget>() }

    val aFactory = { factory().also { scope.node = it } }
    val aContent = @Composable { content.invoke(scope) }

    ComposeNode<TNode, GtkNodeApplier>(
        factory = aFactory,
        update = update,
        skippableUpdate = skippableUpdate,
        content = aContent,
    )
}

@Composable
@GtkComposeInternalApi
public fun <TWidget : Widget, TNode : GtkNode<TWidget>> GtkNode(
    props: PropsBuilder<TWidget>? = null,
    content: ContentBuilder<TWidget> = {},
    factory: () -> TNode,
) {
    val node = remember(factory)
    val scope = remember(node) { StaticNodeScope(node) }
    val logger = rememberLogger(node) { "GtkNode(${node})" }


    val aFactory = { node }
    var refEffect: (DisposableEffectScope.(GtkNode<TWidget>) -> DisposableEffectResult)? = null
    val aContent = @Composable {
        refEffect?.let { effect ->
            DisposableEffect(Unit) {
                effect.invoke(this, node)
            }
        }
        content.invoke(scope)
    }

    ComposeNode<TNode, GtkNodeApplier>(
        factory = aFactory,
        update = { },
        // Must be updated together with [Window]
        skippableUpdate = {
            val propsScope = PropsScope(node)
            props?.invoke(propsScope)

            refEffect = propsScope.refEffect

            update {
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
        content = aContent,
    )
}
