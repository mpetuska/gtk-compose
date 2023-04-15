package dev.petuska.gtk.compose.runtime.content

import androidx.compose.runtime.*
import dev.petuska.gtk.compose.runtime.internal.GtkComposeInternalApi
import dev.petuska.gtk.compose.runtime.internal.WidgetWrapper
import org.gtkkn.bindings.gtk.Widget

public typealias ScopeBuilder<TScope> = @Composable TScope.() -> Unit
public typealias ContentBuilder<TWidget> = ScopeBuilder<WidgetScope<TWidget>>

@Composable
@ExplicitGroupsComposable
private inline fun <TScope, TWidget> ComposeWidget(
    crossinline factory: () -> TWidget,
    widgetScope: TScope,
    attrsSkippableUpdate: @Composable SkippableUpdater<TWidget>.() -> Unit,
    content: ScopeBuilder<TScope>
) {
    currentComposer.startNode()
    if (currentComposer.inserting) {
        currentComposer.createNode {
            factory()
        }
    } else {
        currentComposer.useNode()
    }

    attrsSkippableUpdate.invoke(SkippableUpdater(currentComposer))

    currentComposer.startReplaceableGroup(0x7ab4aae9)
    content.invoke(widgetScope)
    currentComposer.endReplaceableGroup()
    currentComposer.endNode()
}

@OptIn(GtkComposeInternalApi::class)
@Composable
public fun <TWidget : Widget> Widget(
    widgetBuilder: WidgetBuilder<TWidget>,
//    applyAttrs: (AttrsScope<TWidget>.() -> Unit)?,
    content: ContentBuilder<TWidget>?
) {
    val scope = remember { LazyWidgetScope<TWidget>() }
    var refEffect: (DisposableEffectScope.(TWidget) -> DisposableEffectResult)? = null

    ComposeWidget<WidgetScope<TWidget>, WidgetWrapper>(
        factory = {
            val widget = widgetBuilder.create()
            scope.element = widget
            WidgetWrapper(widget)
        },
        attrsSkippableUpdate = {
//            val attrsScope = AttrsScopeBuilder<TWidget>()
//            applyAttrs?.invoke(attrsScope)
//
//            refEffect = attrsScope.refEffect
//
//            update {
//                set(attrsScope.collect(), WidgetWrapper::updateAttrs)
//                set(
//                    attrsScope.eventsListenerScopeBuilder.collectListeners(),
//                    WidgetWrapper::updateEventListeners
//                )
//                set(attrsScope.propertyUpdates, WidgetWrapper::updateProperties)
//            }
        },
        widgetScope = scope,
        content = {
            content?.invoke(this)
        }
    )

    refEffect?.let { effect ->
        DisposableEffect(null) {
            effect.invoke(this, scope.element)
        }
    }
}
