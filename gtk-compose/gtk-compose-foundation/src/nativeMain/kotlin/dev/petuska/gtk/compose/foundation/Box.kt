package dev.petuska.gtk.compose.foundation

import androidx.compose.runtime.Composable
import dev.petuska.gtk.compose.ui.internal.GtkComposeInternalApi
import dev.petuska.gtk.compose.ui.node.ContentBuilder
import dev.petuska.gtk.compose.ui.node.GtkContainerNode
import dev.petuska.gtk.compose.ui.node.GtkNode
import org.gtkkn.bindings.gtk.Box
import org.gtkkn.bindings.gtk.Orientation
import org.gtkkn.bindings.gtk.Widget

@GtkComposeInternalApi
private class BoxNode(override val widget: Box) : GtkContainerNode<Box>() {
    private val children = mutableListOf<GtkNode<Widget>>()

    override fun insert(index: Int, instance: GtkNode<Widget>) {
//        val length = children.size
//        if (index == 0) {
//            println("Prepending")
//            widget.prepend(child = instance.widget)
//        } else if (index < length) {
//            println("Inserting")
//            val after = children[index - 1]
//            widget.insertChildAfter(child = instance.widget, sibling = after.widget)
//        } else {
//            println("Appending")
//            widget.append(child = instance.widget)
//        }

        val after = children.getOrNull(index - 1)
        widget.insertChildAfter(child = instance.widget, sibling = after?.widget)
        children.add(index = index, element = instance)
    }

    override fun remove(index: Int, count: Int) {
        repeat(count) {
            widget.remove(child = children.removeAt(index).widget)
        }
    }

    override fun move(from: Int, to: Int, count: Int) {
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

    override fun clear() {
        children.reversed().forEach {
            widget.remove(child = it.widget)
        }
    }

}

@Composable
public fun Box(
//    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    content: ContentBuilder<Box>
) {
    @OptIn(GtkComposeInternalApi::class)
    GtkContainerNode(update = {}, content = content) {
        val widget = Box(Orientation.VERTICAL, 0)
        BoxNode(widget)
    }
}