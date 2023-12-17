package dev.petuska.gtk.compose.ui.props

import androidx.compose.runtime.DisposableEffectResult
import androidx.compose.runtime.DisposableEffectScope
import dev.petuska.gtk.compose.ui.internal.GtkComposeInternalApi
import dev.petuska.gtk.compose.ui.node.Builder
import dev.petuska.gtk.compose.ui.node.GtkNode
import org.gtkkn.bindings.gtk.Widget
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty1
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
public class PropsScope<TWidget : Widget>(
    @PublishedApi
    internal val node: GtkNode<TWidget>
) {
    internal var refEffect: (DisposableEffectScope.(GtkNode<TWidget>) -> DisposableEffectResult)? = null

    public fun ref(effect: (DisposableEffectScope.(GtkNode<TWidget>) -> DisposableEffectResult)) {
        refEffect = effect
    }

    @PublishedApi
    internal val properties: MutableMap<String, Property<TWidget, Any?>> = mutableMapOf()


    @PublishedApi
    internal fun <TValue> setProperty(
        key: String,
        value: TValue,
        updater: GtkNode<TWidget>.(TValue) -> Unit
    ) {
        @Suppress("UNCHECKED_CAST")
        (properties as MutableMap<String, Property<*, *>>)[key] = Property(value, updater)
    }

    @PublishedApi
    internal val signals: MutableMap<String, Signal<TWidget, Any?>> = mutableMapOf()

    @PublishedApi
    internal fun <TValue> setSignal(
        key: String,
        handler: TValue,
        connector: GtkNode<TWidget>.(TValue) -> ULong
    ) {
        @Suppress("UNCHECKED_CAST")
        (signals as MutableMap<String, Signal<*, *>>)[key] = Signal(handler, connector)
    }

    @PublishedApi
    internal class Property<TWidget : Widget, TValue>(
        val value: TValue,
        val updater: GtkNode<TWidget>.(TValue) -> Unit
    )

    @PublishedApi
    internal class Signal<TWidget : Widget, TValue>(
        val handler: TValue,
        val connector: GtkNode<TWidget>.(TValue) -> ULong
    )
}

public class SyntheticProperty<TWidget : Widget, TValue>(
    public val get: TWidget.() -> TValue,
    public val set: TWidget.(TValue) -> Unit,
) : PropertyDelegateProvider<Any?, ReadWriteProperty<PropsScope<out TWidget>, TValue>> {
    override fun provideDelegate(
        thisRef: Any?,
        property: KProperty<*>
    ): ReadWriteProperty<PropsScope<out TWidget>, TValue> =
        object : ReadWriteProperty<PropsScope<out TWidget>, TValue> {
            override fun getValue(thisRef: PropsScope<out TWidget>, property: KProperty<*>): TValue {
                return get.invoke(thisRef.node.widget)
            }

            override fun setValue(thisRef: PropsScope<out TWidget>, property: KProperty<*>, value: TValue) {
                val key = property.name
                thisRef.setProperty(key, value) { set.invoke(widget, it) }
            }
        }
}

public inline operator fun <TWidget : Widget, reified TValue : Any?> KMutableProperty1<TWidget, TValue>.getValue(
    thisRef: PropsScope<out TWidget>,
    property: KProperty<*>
): TValue {
    return get(thisRef.node.widget)
}

public inline operator fun <TWidget : Widget, reified TValue : Any?> KMutableProperty1<TWidget, TValue>.setValue(
    thisRef: PropsScope<out TWidget>,
    property: KProperty<*>,
    value: TValue
) {
    val key = property.name
    thisRef.setProperty(key, value) { set(widget, it) }
}

@GtkComposeInternalApi
public inline fun <TWidget : Widget, reified TValue : Any?> signal(
    noinline connector: GtkNode<TWidget>.(TValue) -> ULong
): ReadWriteProperty<PropsScope<out TWidget>, TValue> = object : ReadWriteProperty<PropsScope<out TWidget>, TValue> {
    override fun getValue(thisRef: PropsScope<out TWidget>, property: KProperty<*>): TValue {
        val key = property.name
        val signal = thisRef.signals[key]
        requireNotNull(signal) { "Signal[$key] accessed before it was set" }
        val handler = signal.handler
        require(handler is TValue) { "Signal[$key] is not of expected type ${TValue::class.simpleName}" }
        return handler
    }

    override fun setValue(thisRef: PropsScope<out TWidget>, property: KProperty<*>, value: TValue) {
        val key = property.name
        thisRef.setSignal(key, value, connector)
    }
}
