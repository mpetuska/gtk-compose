package dev.petuska.gtk.compose.runtime.node

import androidx.compose.runtime.*
import dev.petuska.gtk.compose.runtime.internal.GtkComposeInternalApi
import dev.petuska.gtk.compose.runtime.internal.GtkNodeApplier
import org.gtkkn.bindings.gtk.Widget

@GtkComposeInternalApi
public typealias AnyGtkNode = GtkNode<Widget>

@GtkComposeInternalApi
public sealed class GtkNode<out TWidget : Widget> {
    public abstract val widget: TWidget

    override fun toString(): String = widget.name
}

@Composable
@GtkComposeInternalApi
public inline fun <TWidget : Widget, TNode : GtkNode<TWidget>> GtkNode(
    update: @DisallowComposableCalls Updater<TNode>.() -> Unit,
    noinline skippableUpdate: (@Composable SkippableUpdater<TNode>.() -> Unit)? = null,
    crossinline content: ContentBuilder<TWidget> = {},
    noinline factory: () -> TNode,
) {
    var refEffect: (DisposableEffectScope.(TWidget) -> DisposableEffectResult)? = null
    val scope = remember { LazyNodeScope<TWidget>() }

    val aFactory = { factory().also { scope.node = it } }
    val aContent = @Composable { content.invoke(scope) }
    if (skippableUpdate == null) {
        ComposeNode<TNode, GtkNodeApplier>(
            factory = aFactory,
            update = update,
            content = aContent,
        )
    } else {
        ComposeNode<TNode, GtkNodeApplier>(
            factory = aFactory,
            update = update,
            skippableUpdate = {
                skippableUpdate()
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
            content = aContent,
        )
    }

    refEffect?.let { effect ->
        DisposableEffect(null) {
            effect.invoke(this, scope.node.widget)
        }
    }
}
