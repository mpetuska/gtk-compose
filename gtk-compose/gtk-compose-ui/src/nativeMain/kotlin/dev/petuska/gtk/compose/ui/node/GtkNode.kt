package dev.petuska.gtk.compose.ui.node

import androidx.compose.runtime.*
import dev.petuska.gtk.compose.ui.internal.GtkComposeInternalApi
import dev.petuska.gtk.compose.ui.internal.GtkNodeApplier
import dev.petuska.gtk.compose.ui.platform.rememberLogger
import dev.petuska.gtk.compose.ui.props.PropsBuilder
import dev.petuska.gtk.compose.ui.props.PropsScope
import org.gtkkn.bindings.gtk.Widget

@GtkComposeInternalApi
public typealias AnyGtkNode = GtkNode<Widget>

@OptIn(ExperimentalStdlibApi::class)
@GtkComposeInternalApi
public sealed class GtkNode<out TWidget : Widget> : AutoCloseable {
    public abstract val widget: TWidget

    override fun close() {}

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
    val scope = remember { LazyNodeScope<TWidget>() }
    val logger = rememberLogger(scope._node) { "GtkNode(${scope._node ?: "Unknown"})" }


    val aFactory = { factory().also { scope.node = it } }
    var refEffect: (DisposableEffectScope.(GtkNode<TWidget>) -> DisposableEffectResult)? = null
    val aContent = @Composable {
        refEffect?.let { effect ->
            DisposableEffect(Unit) {
                effect.invoke(this, scope.node)
            }
        }
        content.invoke(scope)
    }

    ComposeNode<TNode, GtkNodeApplier>(
        factory = aFactory,
        update = { },
        skippableUpdate = {
            val propsScope = PropsScope<TWidget>()
            props?.invoke(propsScope)

            refEffect = propsScope.refEffect

            update {
                logger.d { "Updating properties" }
                propsScope.updates.forEach { (key, update) ->
                    logger.v { "Updating property[$key]" }
                    set(update.value, update.updater)
                }
            }
        },
        content = aContent,
    )
}
