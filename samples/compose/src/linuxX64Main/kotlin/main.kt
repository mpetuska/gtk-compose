package dev.petuska.gtk.compose.samples.compose

import dev.petuska.gtk.compose.runtime.renderComposable
import dev.petuska.gtk.compose.widgets.Box
import dev.petuska.gtk.compose.widgets.Button
import org.gtkkn.bindings.gio.ApplicationFlags
import org.gtkkn.bindings.gtk.Application
import org.gtkkn.bindings.gtk.ApplicationWindow
import org.gtkkn.bindings.gtk.Button


private const val APP_ID = "dev.petuska.gtk.compose"
fun main(vararg args: String) {
    val application = Application(APP_ID, ApplicationFlags.DEFAULT_FLAGS)
    application.connectActivate {
        application.buildComposeUI()
    }
    application.run(args.size, args.toList())
}

private fun Application.buildComposeUI() = renderComposable {
    Box {
        Button { }
        Button { }
    }
}

private fun Application.buildUI() {
    val button = Button("Press me!").apply {
        marginTop = 12
        marginBottom = 12
        marginStart = 12
    }
    val window = ApplicationWindow(this)
    window.title = "Hello Kotlin"
    window.present()
}