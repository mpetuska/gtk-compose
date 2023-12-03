package dev.petuska.gtk.compose.ui.node

import androidx.compose.runtime.*
import dev.petuska.gtk.compose.ui.internal.GtkComposeInternalApi
import dev.petuska.gtk.compose.ui.internal.GtkNodeApplier
import org.gtkkn.bindings.gtk.Widget

@GtkComposeInternalApi
public typealias AnyGtkNode = GtkNode<Widget>

@GtkComposeInternalApi
public sealed class GtkNode<out TWidget : Widget> {
    public abstract val widget: TWidget

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
    noinline skippableUpdate: (@Composable SkippableUpdater<TNode>.() -> Unit),
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
