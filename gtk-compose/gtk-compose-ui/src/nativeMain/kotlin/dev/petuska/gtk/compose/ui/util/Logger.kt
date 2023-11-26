package dev.petuska.gtk.compose.ui.util

import co.touchlab.kermit.Message
import co.touchlab.kermit.MessageStringFormatter
import co.touchlab.kermit.Severity
import co.touchlab.kermit.Tag
import dev.petuska.gtk.compose.ui.util.ANSI.asBackground
import dev.petuska.gtk.compose.ui.util.ANSI.bold
import dev.petuska.gtk.compose.ui.util.ANSI.bright

internal object AnsiLogFormatter : MessageStringFormatter {
    private val Severity?.colour: ANSI.SomeColour?
        get() = if (ANSI.isSupported && this != null) {
            when (this) {
                Severity.Verbose -> ANSI.Colour.White.bright
                Severity.Debug -> ANSI.Colour.Blue.bright
                Severity.Info -> ANSI.Colour.Green.bright
                Severity.Warn -> ANSI.Colour.Yellow.bright
                Severity.Error -> ANSI.Colour.Red.bright
                Severity.Assert -> ANSI.Colour.Purple.bright
            }
        } else {
            null
        }

    override fun formatSeverity(severity: Severity): String {
        return severity.name.padEnd(8, ' ')
    }

    override fun formatTag(tag: Tag): String {
        return tag.tag.padEnd(20, ' ')
    }

    override fun formatMessage(severity: Severity?, tag: Tag?, message: Message): String {
        val (s, t, m) = if (ANSI.isSupported) {
            val fgColour = severity.colour
            val fgCode = fgColour ?: ANSI.Reset
            val bgColour = fgColour?.asBackground()?.bright
            val bgCode = bgColour ?: ANSI.Background.White
            val cSeverity = severity?.let(::formatSeverity)?.let {
                bgCode + ANSI.Colour.Black.bold + " $it" + ANSI.Reset
            }
            val cTag = tag?.let(::formatTag)?.let {
                (fgColour?.bold ?: ANSI.Reset) + it + ANSI.Reset
            }
            val cMessage = message.message.let {
                fgCode + it + ANSI.Reset
            }

            Triple(cSeverity, cTag, cMessage)
        } else {
            Triple(severity?.let(::formatSeverity), tag?.let(::formatTag), message.message)
        }

        // Optimize for Android
        if (severity == null && tag == null)
            return message.message

        val sb = StringBuilder()
        if (s != null) sb.append(s).append(" ")
        if (!t.isNullOrEmpty()) sb.append(t).append(" ")
        sb.append(m)

        return sb.toString()
    }
}