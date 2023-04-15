package dev.petuska.gtk.compose.widgets

import androidx.compose.runtime.Composable
import dev.petuska.gtk.compose.runtime.content.ContentBuilder
import dev.petuska.gtk.compose.runtime.content.Widget
import dev.petuska.gtk.compose.runtime.content.WidgetBuilder
import org.gtkkn.bindings.gtk.Button

private val builder = WidgetBuilder { Button() }

@Composable
public fun Button(
//    attrs: AttrBuilderContext<HTMLDivElement>? = null,
    content: ContentBuilder<Button>? = null
) {
    Widget(
        widgetBuilder = builder,
//        applyAttrs = attrs,
        content = content
    )
}