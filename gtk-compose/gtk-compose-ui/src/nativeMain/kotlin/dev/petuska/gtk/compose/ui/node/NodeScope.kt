package dev.petuska.gtk.compose.ui.node

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffectScope
import dev.petuska.gtk.compose.ui.internal.GtkComposeInternalApi
import org.gtkkn.bindings.gtk.Widget

@DslMarker
public annotation class GtkComposeNodeScope

public typealias Builder<TScope> = TScope.() -> Unit
public typealias ComposableBuilder<TScope> = @Composable Builder<TScope>
public typealias ContentBuilder<TWidget> = ComposableBuilder<ContainerScope<TWidget>>

@GtkComposeNodeScope
public sealed interface NodeScope<out TWidget : Widget> {
    /**
     * Reference to a native GTK [Widget] this node is managing
     */
    public val DisposableEffectScope.scopeElement: TWidget
}

public interface ElementScope<out TWidget : Widget> : NodeScope<TWidget>

public interface ContainerScope<out TWidget : Widget> : NodeScope<TWidget>

/**
 * A [NodeScope] that expects [node] property to be set after scope creation,
 * but always before the first potential call to [scopeElement]
 */
@GtkComposeInternalApi
public class LazyNodeScope<TWidget : Widget> : ElementScope<TWidget>, ContainerScope<TWidget> {
    @PublishedApi
    internal lateinit var node: GtkNode<TWidget>

    override val DisposableEffectScope.scopeElement: TWidget
        get() = node.widget
}

/**
 * A [NodeScope] that demands [node] property to be during scope creation
 */
@GtkComposeInternalApi
public class StaticNodeScope<TWidget : Widget>(
    @PublishedApi
    internal val node: GtkNode<TWidget>
) : ElementScope<TWidget>, ContainerScope<TWidget> {
    override val DisposableEffectScope.scopeElement: TWidget
        get() = node.widget
}

/**
 * Wraps a nullable [ContentBuilder] into potentially noop non-nullable [ContentBuilder]
 */
@GtkComposeInternalApi
public inline fun <TWidget : Widget> ContentBuilder<TWidget>?.wrap(): ContentBuilder<TWidget> =
    { this@wrap?.invoke(this) }
