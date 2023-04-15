package dev.petuska.gtk.compose.runtime.content

import androidx.compose.runtime.DisposableEffectScope
import org.gtkkn.bindings.gtk.Widget

public interface WidgetScope<out TWidget : Widget> {
    public val DisposableEffectScope.scopeElement: TWidget
}

internal class LazyWidgetScope<TWidget : Widget> : WidgetScope<TWidget> {
    internal lateinit var element: TWidget
    override val DisposableEffectScope.scopeElement: TWidget
        get() = element
}
