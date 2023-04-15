package dev.petuska.gtk.compose.runtime.internal

import androidx.compose.runtime.AbstractApplier

@GtkComposeInternalApi
internal class WidgetApplier(
    root: WidgetWrapper
) : AbstractApplier<WidgetWrapper>(root) {

    override fun insertTopDown(index: Int, instance: WidgetWrapper) {
        // ignored. Building tree bottom-up
    }

    override fun insertBottomUp(index: Int, instance: WidgetWrapper) {
        current.insert(index, instance)
    }

    override fun remove(index: Int, count: Int) {
        current.remove(index, count)
    }

    override fun move(from: Int, to: Int, count: Int) {
        current.move(from, to, count)
    }

    override fun onClear() {
        // or current.node.clear()?; in all examples it calls 'clear' on the root
        root.clear()
    }
}