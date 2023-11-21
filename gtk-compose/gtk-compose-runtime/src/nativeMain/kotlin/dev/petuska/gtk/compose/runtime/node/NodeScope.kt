package dev.petuska.gtk.compose.runtime.node

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffectScope
import dev.petuska.gtk.compose.runtime.internal.GtkComposeInternalApi
import org.gtkkn.bindings.gtk.Widget

public typealias ScopedBuilder<TScope> = @Composable TScope.() -> Unit
public typealias ContentBuilder<TWidget> = ScopedBuilder<ContainerScope<TWidget>>

public sealed interface NodeScope<out TWidget : Widget> {
    public val DisposableEffectScope.scopeElement: TWidget
}

public interface ElementScope<out TWidget : Widget> : NodeScope<TWidget>

public interface ContainerScope<out TWidget : Widget> : NodeScope<TWidget>

@GtkComposeInternalApi
public class LazyNodeScope<TWidget : Widget> : ElementScope<TWidget>, ContainerScope<TWidget> {
    @PublishedApi
    internal lateinit var node: GtkNode<TWidget>

    override val DisposableEffectScope.scopeElement: TWidget
        get() = node.widget
}

@GtkComposeInternalApi
public inline fun <TWidget : Widget> ContentBuilder<TWidget>?.wrap(): ContentBuilder<TWidget> =
    { this@wrap?.invoke(this) }
