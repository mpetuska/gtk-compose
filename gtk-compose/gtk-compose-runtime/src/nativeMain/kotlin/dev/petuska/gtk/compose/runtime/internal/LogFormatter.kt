package dev.petuska.gtk.compose.runtime.internal

import co.touchlab.kermit.MessageStringFormatter
import co.touchlab.kermit.Tag

internal object LogFormatter: MessageStringFormatter {
    override fun formatTag(tag: Tag) = "[${tag.tag}]"
}
