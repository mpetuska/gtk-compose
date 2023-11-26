package dev.petuska.gtk.compose.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import co.touchlab.kermit.Logger
import dev.petuska.gtk.compose.ui.internal.GtkComposeInternalApi
import org.gtkkn.bindings.gtk.Application
import org.gtkkn.bindings.gtk.Window

internal fun noLocalProvidedFor(name: String): Nothing {
    error("CompositionLocal $name not present")
}

public val LocalApplication: ProvidableCompositionLocal<Application> = staticCompositionLocalOf {
    noLocalProvidedFor("Application")
}

public val LocalWindow: ProvidableCompositionLocal<Window> = staticCompositionLocalOf {
    noLocalProvidedFor("Window")
}

internal val LocalLogger: ProvidableCompositionLocal<Logger> = staticCompositionLocalOf {
    noLocalProvidedFor("Logger")
}

@Composable
@GtkComposeInternalApi
public fun rememberLogger(key: Any? = Unit, tag: () -> String): Logger {
    val logger = LocalLogger.current
    return remember(key) { logger.withTag(tag()) }
}
