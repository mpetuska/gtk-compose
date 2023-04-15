package dev.petuska.gtk.compose.widgets

import androidx.compose.runtime.Composable
import dev.petuska.gtk.compose.runtime.content.ContentBuilder
import dev.petuska.gtk.compose.runtime.content.Widget
import dev.petuska.gtk.compose.runtime.content.WidgetBuilder
import org.gtkkn.bindings.gtk.Box
import org.gtkkn.bindings.gtk.FixedLayout
import org.gtkkn.bindings.gtk.Orientation

private val builder = WidgetBuilder { Box(Orientation.VERTICAL, 0) }

@Composable
public fun Box(
//    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    content: ContentBuilder<Box>? = null
) {
    Widget(
        widgetBuilder = builder,
//        applyAttrs = attrs,
        content = content
    )
}