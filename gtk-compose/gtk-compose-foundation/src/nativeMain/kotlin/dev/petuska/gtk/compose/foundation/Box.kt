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
        val length = children.size
        if (index == 0) {
            widget.prepend(child = instance.widget)
        } else if (index < length) {
            val after = children[index - 1]
            widget.insertChildAfter(child = instance.widget, sibling = after.widget)
        } else {
            widget.append(child = instance.widget)
        }
        children.add(index = index, element = instance)
    }

    override fun remove(index: Int, count: Int) {
        repeat(count) {
            widget.remove(child = children.removeAt(index).widget)
        }
    }
}

@Composable
private fun Box(
    orientation: Orientation,
    spacing: Int,
    homogeneous: Boolean,
    content: ContentBuilder<Box>
) {
    @OptIn(GtkComposeInternalApi::class)
    GtkContainerNode(update = {
        set(spacing) { widget.spacing = it }
        set(homogeneous) { widget.homogeneous = it }
    }, content = content) {
        val widget = Box(orientation, spacing)
        BoxNode(widget)
    }
}

/**
 * A container that spreads its children vertically
 * @param spacing [Box.spacing]
 * @param homogeneous [Box.homogeneous]
 *
 * @see Box
 * @see Orientation.VERTICAL
 */
@Composable
public fun VBox(
    spacing: Int,
    homogeneous: Boolean = true,
    content: ContentBuilder<Box>
) {
    Box(
        orientation = Orientation.VERTICAL,
        spacing = spacing, homogeneous = homogeneous, content = content
    )
}

/**
 * A container that spreads its children horizontally
 * @param spacing [Box.spacing]
 * @param homogeneous [Box.homogeneous]
 *
 * @see Box
 * @see Orientation.HORIZONTAL
 */
@Composable
public fun HBox(
    spacing: Int,
    homogeneous: Boolean = true,
    content: ContentBuilder<Box>
) {
    Box(
        orientation = Orientation.HORIZONTAL,
        spacing = spacing, homogeneous = homogeneous, content = content
    )
}