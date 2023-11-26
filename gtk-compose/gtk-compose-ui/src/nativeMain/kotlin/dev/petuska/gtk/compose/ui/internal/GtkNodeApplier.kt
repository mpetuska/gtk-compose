package dev.petuska.gtk.compose.ui.internal

import androidx.compose.runtime.AbstractApplier
import dev.petuska.gtk.compose.ui.node.AnyGtkContainerNode
import dev.petuska.gtk.compose.ui.node.AnyGtkNode
import dev.petuska.gtk.compose.ui.node.GtkContainerNode


@GtkComposeInternalApi
@PublishedApi
internal class GtkNodeApplier(
    root: AnyGtkContainerNode
) : AbstractApplier<AnyGtkNode>(root) {
    private val logger = Logger.withTag(this::class.simpleName!!)

    override fun insertTopDown(index: Int, instance: AnyGtkNode) {
        // ignored. Building tree bottom-up
    }

    override fun insertBottomUp(index: Int, instance: AnyGtkNode) {
        val parent = current
        if (parent is GtkContainerNode) {
            logger.d { "[$parent] Inserting child $instance at $index" }
            parent.insert(index, instance)
        } else {
            logger.e { "[$parent] Trying to insert $instance to non-container node at index $index" }
        }
    }

    override fun remove(index: Int, count: Int) {
        // 0 1 2 3 4 5
        // from=1, count=2
        // 0 3 4 5
        val parent = current
        if (parent is GtkContainerNode) {
            logger.d { "[$parent] Removing $count children at $index" }
            parent.remove(index, count)
        } else {
            logger.e { "[$parent] Trying to remove $count children at index $index from non-container node" }
        }
    }

    override fun move(from: Int, to: Int, count: Int) {
        // 0 1 2 3 4 5
        // from=1, to=3, count=2
        // 0 3 4 1 2 5
        val parent = current
        if (parent is GtkContainerNode) {
            logger.d { "[$parent] Moving $count children from $from to $to" }
            parent.move(from, to, count)
        } else {
            logger.e { "[$parent] Trying to move $count children from $from to $to within non-container node" }
        }
    }

    override fun onClear() {
        val parent = root
        if (parent is GtkContainerNode) {
            logger.i { "[$parent] Clearing children" }
            parent.clear()
        } else {
            logger.e { "[$parent] Trying to clear children of non-container node" }
        }
    }
}