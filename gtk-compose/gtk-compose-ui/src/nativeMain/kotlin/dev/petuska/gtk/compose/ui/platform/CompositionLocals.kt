package dev.petuska.gtk.compose.ui.platform

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import org.gtkkn.bindings.gtk.Application

private fun noLocalProvidedFor(name: String): Nothing {
    error("CompositionLocal $name not present")
}

public val LocalApplication: ProvidableCompositionLocal<Application> = staticCompositionLocalOf {
    noLocalProvidedFor("Application")
}