package dev.petuska.gtk.compose.ui.props

import androidx.compose.runtime.DisposableEffectResult
import androidx.compose.runtime.DisposableEffectScope
import dev.petuska.gtk.compose.ui.internal.GtkComposeInternalApi
import dev.petuska.gtk.compose.ui.node.Builder
import dev.petuska.gtk.compose.ui.node.GtkNode
import dev.petuska.gtk.compose.ui.props.PropsScope.Update
import org.gtkkn.bindings.gtk.Widget
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@DslMarker
public annotation class GtkComposePropsScope

/**
 * A composable lambda to define given [PropsScope]
 */
public typealias PropsBuilder<TWidget> = Builder<PropsScope<TWidget>>

/**
 * A scope to compile [TWidget] properties
 */
@GtkComposePropsScope
public class PropsScope<TWidget : Widget> {
    internal var refEffect: (DisposableEffectScope.(GtkNode<TWidget>) -> DisposableEffectResult)? = null

    public fun ref(effect: (DisposableEffectScope.(GtkNode<TWidget>) -> DisposableEffectResult)) {
        refEffect = effect
    }

    @PublishedApi
    internal val updates: MutableMap<String, Update<TWidget, Any?>> = mutableMapOf()

    @PublishedApi
    internal class Update<TWidget : Widget, TValue>(
        val value: TValue,
        val updater: GtkNode<TWidget>.(TValue) -> Unit
    )
}

@GtkComposeInternalApi
public inline fun <TWidget : Widget, reified TValue : Any?> prop(
    noinline updater: GtkNode<TWidget>.(TValue) -> Unit
): ReadWriteProperty<PropsScope<out TWidget>, TValue> = object : ReadWriteProperty<PropsScope<out TWidget>, TValue> {
    override fun getValue(thisRef: PropsScope<out TWidget>, property: KProperty<*>): TValue {
        val key = property.name
        val update = thisRef.updates[key]
        requireNotNull(update) { "Property[$key] accessed before it was set" }
        val value = update.value
        require(value is TValue) { "Property[$key] is not of expected type ${TValue::class.simpleName}" }
        return value
    }

    override fun setValue(thisRef: PropsScope<out TWidget>, property: KProperty<*>, value: TValue) {
        @Suppress("UNCHECKED_CAST")
        val updates = thisRef.updates as MutableMap<String, Any>
        val key = property.name
        updates[key] = Update(value, updater)
    }
}
