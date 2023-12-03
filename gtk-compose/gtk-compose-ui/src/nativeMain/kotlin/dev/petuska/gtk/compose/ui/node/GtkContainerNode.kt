package dev.petuska.gtk.compose.ui.node

import androidx.compose.runtime.*
import co.touchlab.kermit.Logger
import dev.petuska.gtk.compose.ui.internal.GtkComposeInternalApi
import dev.petuska.gtk.compose.ui.internal.GtkNodeApplier
import org.gtkkn.bindings.gtk.Widget

@GtkComposeInternalApi
public typealias AnyGtkContainerNode = GtkContainerNode<Widget>

/**
 * A [GtkNode] that can have other [GtkNode]s as children
 */
@GtkComposeInternalApi
public abstract class GtkContainerNode<out TWidget : Widget> : GtkNode<TWidget>() {
    private val children = mutableListOf<AnyGtkNode>()

    public abstract fun insert(index: Int, instance: AnyGtkNode)

    public abstract fun remove(index: Int, count: Int)

    public open fun move(from: Int, to: Int, count: Int) {
        if (from == to) {
            return // nothing to do
        }

        for (i in 0 until count) {
            // if "from" is after "to," the from index moves because we're inserting before it
            val fromIndex = if (from > to) from + i else from
            val toIndex = if (from > to) to + i else to + count - 2


            val child = children[fromIndex]
            remove(fromIndex, 1)
            insert(toIndex, child)
        }
    }

    public open fun clear() {
        remove(0, children.size)
    }
}

/**
 * Mounts this [GtkContainerNode] as a sub-composition of the [parentComposition]
 */
@GtkComposeInternalApi
public fun <TWidget : Widget> GtkContainerNode<TWidget>.setContent(
    parentComposition: CompositionContext,
    logger: Logger,
    content: ContentBuilder<TWidget>
): Composition {
    val applier = GtkNodeApplier(this, logger)
    val scope = StaticNodeScope(this)
    val composition = Composition(applier, parentComposition)
    composition.setContent {
        scope.content()
    }
    return composition
}

@Composable
@GtkComposeInternalApi
public inline fun <TWidget : Widget, TNode : GtkContainerNode<TWidget>> GtkContainerNode(
    update: @DisallowComposableCalls Updater<TNode>.() -> Unit,
    crossinline content: ContentBuilder<TWidget>,
    noinline factory: () -> TNode,
) {
    GtkNode(
        factory = factory,
        update = update,
        content = content,
    )
}
