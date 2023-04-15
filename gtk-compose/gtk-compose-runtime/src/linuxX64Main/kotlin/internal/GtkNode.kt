package dev.petuska.gtk.compose.runtime.internal

@GtkComposeInternalApi
public interface GtkNode {
    public fun insert(index: Int, widgetWrapper: GtkNode)

    public fun remove(index: Int, count: Int)

    public fun move(from: Int, to: Int, count: Int)

    public fun clear()
}