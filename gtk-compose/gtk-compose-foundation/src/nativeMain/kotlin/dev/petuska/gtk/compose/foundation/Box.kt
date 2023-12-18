package dev.petuska.gtk.compose.foundation

import androidx.compose.runtime.Composable
import dev.petuska.gtk.compose.ui.internal.GtkComposeInternalApi
import dev.petuska.gtk.compose.ui.node.ContentBuilder
import dev.petuska.gtk.compose.ui.node.GtkContainerNode
import dev.petuska.gtk.compose.ui.node.GtkNode
import dev.petuska.gtk.compose.ui.props.PropsBuilder
import dev.petuska.gtk.compose.ui.props.PropsScope
import dev.petuska.gtk.compose.ui.props.getValue
import dev.petuska.gtk.compose.ui.props.setValue
import org.gtkkn.bindings.gtk.BaselinePosition
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

// region properties
/**
 * @see Box.baselineChild
 */
public var PropsScope<out Box>.baselineChild: Int by Box::baselineChild

/**
 * @see Box.baselinePosition
 */
public var PropsScope<out Box>.baselinePosition: BaselinePosition by Box::baselinePosition

/**
 * @see Box.homogeneous
 */
public var PropsScope<out Box>.homogeneous: Boolean by Box::homogeneous

/**
 * @see Box.spacing
 */
public var PropsScope<out Box>.spacing: Int by Box::spacing
// endregion

@Composable
private fun Box(
    orientation: Orientation,
    props: PropsBuilder<Box>,
    content: ContentBuilder<Box>
) {
    GtkNode(
        props = props,
        content = content
    ) {
        val widget = Box(orientation, 0)
        BoxNode(widget)
    }
}

/**
 * A container that spreads its children vertically
 * @see Box
 * @see Orientation.VERTICAL
 */
@Composable
public fun VBox(
    props: PropsBuilder<Box>,
    content: ContentBuilder<Box>,
) {
    Box(
        orientation = Orientation.VERTICAL,
        props = props,
        content = content
    )
}

/**
 * A container that spreads its children horizontally
 * @see Box
 * @see Orientation.HORIZONTAL
 */
@Composable
public fun HBox(
    props: PropsBuilder<Box>,
    content: ContentBuilder<Box>,
) {
    Box(
        orientation = Orientation.HORIZONTAL,
        props = props,
        content = content
    )
}