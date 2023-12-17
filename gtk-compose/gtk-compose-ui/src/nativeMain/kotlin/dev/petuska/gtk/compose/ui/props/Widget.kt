package dev.petuska.gtk.compose.ui.props

import org.gtkkn.bindings.gdk.Cursor
import org.gtkkn.bindings.gtk.*
import kotlin.Unit

// region properties
/**
 * @see Widget.canFocus
 */
public var PropsScope<out Widget>.canFocus: Boolean by Widget::canFocus

/**
 * @see Widget.canTarget
 */
public var PropsScope<out Widget>.canTarget: Boolean by Widget::canTarget

/**
 * @see Widget.cssClasses
 */
public var PropsScope<out Widget>.cssClasses: List<String> by Widget::cssClasses

/**
 * @see Widget.cursor
 */
public var PropsScope<out Widget>.cursor: Cursor? by Widget::cursor

/**
 * @see Widget.focusOnClick
 */
public var PropsScope<out Widget>.focusOnClick: Boolean by Widget::focusOnClick

/**
 * @see Widget.focusable
 */
public var PropsScope<out Widget>.focusable: Boolean by Widget::focusable

/**
 * @see Widget.halign
 */
public var PropsScope<out Widget>.halign: Align by Widget::halign

/**
 * @see Widget.hasTooltip
 */
public var PropsScope<out Widget>.hasTooltip: Boolean by Widget::hasTooltip

/**
 * @see Widget.hexpand
 */
public var PropsScope<out Widget>.hexpand: Boolean by Widget::hexpand

/**
 * @see Widget.hexpandSet
 */
public var PropsScope<out Widget>.hexpandSet: Boolean by Widget::hexpandSet

/**
 * @see Widget.layoutManager
 */
public var PropsScope<out Widget>.layoutManager: LayoutManager? by Widget::layoutManager

/**
 * @see Widget.marginBottom
 */
public var PropsScope<out Widget>.marginBottom: Int by Widget::marginBottom

/**
 * @see Widget.marginEnd
 */
public var PropsScope<out Widget>.marginEnd: Int by Widget::marginEnd

/**
 * @see Widget.marginStart
 */
public var PropsScope<out Widget>.marginStart: Int by Widget::marginStart

/**
 * @see Widget.marginTop
 */
public var PropsScope<out Widget>.marginTop: Int by Widget::marginTop

/**
 * @see Widget.name
 */
public var PropsScope<out Widget>.name: String by Widget::name

/**
 * @see Widget.opacity
 */
public var PropsScope<out Widget>.opacity: Double by Widget::opacity

/**
 * @see Widget.overflow
 */
public var PropsScope<out Widget>.overflow: Overflow by Widget::overflow

/**
 * @see Widget.receivesDefault
 */
public var PropsScope<out Widget>.receivesDefault: Boolean by Widget::receivesDefault

/**
 * @see Widget.sensitive
 */
public var PropsScope<out Widget>.sensitive: Boolean by Widget::sensitive

/**
 * @see Widget.tooltipMarkup
 */
public var PropsScope<out Widget>.tooltipMarkup: String? by Widget::tooltipMarkup

/**
 * @see Widget.tooltipText
 */
public var PropsScope<out Widget>.tooltipText: String? by Widget::tooltipText

/**
 * @see Widget.valign
 */
public var PropsScope<out Widget>.valign: Align by Widget::valign

/**
 * @see Widget.vexpand
 */
public var PropsScope<out Widget>.vexpand: Boolean by Widget::vexpand

/**
 * @see Widget.vexpandSet
 */
public var PropsScope<out Widget>.vexpandSet: Boolean by Widget::vexpandSet

/**
 * @see Widget.visible
 */
public var PropsScope<out Widget>.visible: Boolean by Widget::visible
// endregion

// region signals
/**
 * @see Widget.connectDestroy
 */
public var PropsScope<out Widget>.onClick: () -> Unit by signal { widget.connectDestroy(handler = it) }

/**
 * @see Widget.connectDirectionChanged
 */
public var PropsScope<out Widget>.onDirectionChanged: (previousDirection: TextDirection) -> Unit by signal {
    widget.connectDirectionChanged(handler = it)
}

/**
 * @see Widget.connectHide
 */
public var PropsScope<out Widget>.onHide: () -> Unit by signal { widget.connectHide(handler = it) }

/**
 * @see Widget.connectKeynavFailed
 */
public var PropsScope<out Widget>.onKeynavFailed: (direction: DirectionType) -> Boolean by signal {
    widget.connectKeynavFailed(handler = it)
}

/**
 * @see Widget.connectMap
 */
public var PropsScope<out Widget>.onMap: () -> Unit by signal { widget.connectMap(handler = it) }

/**
 * @see Widget.connectMnemonicActivate
 */
public var PropsScope<out Widget>.onMnemonicActivate: (groupCycling: Boolean) -> Boolean by signal {
    widget.connectMnemonicActivate(handler = it)
}

/**
 * @see Widget.connectMoveFocus
 */
public var PropsScope<out Widget>.onMoveFocus: (direction: DirectionType) -> Unit by signal {
    widget.connectMoveFocus(handler = it)
}

/**
 * @see Widget.connectQueryTooltip
 */
public var PropsScope<out Widget>.onQueryTooltip: (
    x: Int,
    y: Int,
    keyboardMode: Boolean,
    tooltip: Tooltip,
) -> Boolean by signal { widget.connectQueryTooltip(handler = it) }

/**
 * @see Widget.connectRealize
 */
public var PropsScope<out Widget>.onRealize: () -> Unit by signal { widget.connectRealize(handler = it) }

/**
 * @see Widget.connectShow
 */
public var PropsScope<out Widget>.onShow: () -> Unit by signal { widget.connectShow(handler = it) }

/**
 * @see Widget.connectStateFlagsChanged
 */
public var PropsScope<out Widget>.onStateFlagsChanged: (flags: StateFlags) -> Unit by signal {
    widget.connectStateFlagsChanged(handler = it)
}

/**
 * @see Widget.connectUnmap
 */
public var PropsScope<out Widget>.onUnmap: () -> Unit by signal { widget.connectUnmap(handler = it) }

/**
 * @see Widget.connectUnrealize
 */
public var PropsScope<out Widget>.onUnrealize: () -> Unit by signal { widget.connectUnrealize(handler = it) }
// endregion