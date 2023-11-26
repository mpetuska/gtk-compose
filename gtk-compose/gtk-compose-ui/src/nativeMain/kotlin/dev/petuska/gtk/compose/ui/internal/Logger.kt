package dev.petuska.gtk.compose.ui.internal

import co.touchlab.kermit.*
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKStringFromUtf8
import platform.posix.getenv


@GtkComposeInternalApi
@OptIn(ExperimentalForeignApi::class)
internal val Logger = Logger(
    loggerConfigInit(
        minSeverity = (getenv("LOG_LEVEL")?.toKStringFromUtf8() ?: "INFO").let { env ->
            env.toIntOrNull()?.let { Severity.entries[it] } ?: Severity.valueOf(
                env.lowercase().replaceFirstChar(Char::uppercase)
            )
        },
        logWriters = arrayOf(platformLogWriter(LogFormatter)),
    ),
    "gtk-compose"
)

internal object LogFormatter : MessageStringFormatter {
    override fun formatTag(tag: Tag) = "[${tag.tag}]"
}