package dev.petuska.gtk.compose.foundation

import androidx.compose.runtime.Composable
import dev.petuska.gtk.compose.ui.internal.GtkComposeInternalApi
import dev.petuska.gtk.compose.ui.node.GtkElementNode
import dev.petuska.gtk.compose.ui.node.GtkNode
import dev.petuska.gtk.compose.ui.props.*
import org.gtkkn.bindings.gio.MenuModel
import org.gtkkn.bindings.gtk.*
import org.gtkkn.bindings.pango.AttrList
import org.gtkkn.bindings.pango.TabArray
import org.gtkkn.bindings.pango.WrapMode
import kotlin.Unit

@GtkComposeInternalApi
private class LabelNode(override val widget: Label) : GtkElementNode<Label>() {
}

// region properties
/**
 * @see Label.attributes
 */
public var PropsScope<out Label>.attributes: AttrList? by Label::attributes

/**
 * @see Label.extraMenu
 */
public var PropsScope<out Label>.extraMenu: MenuModel? by Label::extraMenu

/**
 * @see Label.justify
 */
public var PropsScope<out Label>.justify: Justification by Label::justify

/**
 * @see Label.label
 */
public var PropsScope<out Label>.label: String by Label::label

/**
 * @see Label.lines
 */
public var PropsScope<out Label>.lines: Int by Label::lines

/**
 * @see Label.maxWidthChars
 */
public var PropsScope<out Label>.maxWidthChars: Int by Label::maxWidthChars

/**
 * @see Label.mnemonicWidget
 */
public var PropsScope<out Label>.mnemonicWidget: Widget? by Label::mnemonicWidget

/**
 * @see Label.naturalWrapMode
 */
public var PropsScope<out Label>.naturalWrapMode: NaturalWrapMode by Label::naturalWrapMode

/**
 * @see Label.selectable
 */
public var PropsScope<out Label>.selectable: Boolean by Label::selectable

/**
 * @see Label.singleLineMode
 */
public var PropsScope<out Label>.singleLineMode: Boolean by Label::singleLineMode

/**
 * @see Label.tabs
 */
public var PropsScope<out Label>.tabs: TabArray? by Label::tabs

/**
 * @see Label.useMarkup
 */
public var PropsScope<out Label>.useMarkup: Boolean by Label::useMarkup

/**
 * @see Label.useUnderline
 */
public var PropsScope<out Label>.useUnderline: Boolean by Label::useUnderline

/**
 * @see Label.widthChars
 */
public var PropsScope<out Label>.widthChars: Int by Label::widthChars

/**
 * @see Label.wrap
 */
public var PropsScope<out Label>.wrap: Boolean by Label::wrap

/**
 * @see Label.wrapMode
 */
public var PropsScope<out Label>.wrapMode: WrapMode by Label::wrapMode

/**
 * @see Label.xalign
 */
public var PropsScope<out Label>.xalign: Float by Label::xalign

/**
 * @see Label.yalign
 */
public var PropsScope<out Label>.yalign: Float by Label::yalign
// endregion

// region signals
/**
 * @see Label.connectActivateCurrentLink
 */
public var PropsScope<out Label>.onActivateCurrentLink: () -> Unit by signal { widget.connectActivateCurrentLink(handler = it) }

/**
 * @see Label.connectActivateLink
 */
public var PropsScope<out Label>.onActivateLink: (uri: String) -> Boolean by signal { widget.connectActivateLink(handler = it) }

/**
 * @see Label.connectCopyClipboard
 */
public var PropsScope<out Label>.onCopyClipboard: () -> Unit by signal { widget.connectCopyClipboard(handler = it) }

/**
 * @see Label.connectMoveCursor
 */
public var PropsScope<out Label>.onMoveCursor: (step: MovementStep, count: Int, extendSelection: Boolean) -> Unit by signal {
    widget.connectMoveCursor(handler = it)
}
//endregion

@Composable
public fun Label(
    props: PropsBuilder<Label>,
) {
    GtkNode(
        props = props
    ) { LabelNode(Label(null)) }
}

@Composable
public fun Label(
    text: String,
) {
    Label(
        props = {
            label = text
        }
    )
}