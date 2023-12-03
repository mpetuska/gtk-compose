package dev.petuska.gtk.compose.samples.compose

import androidx.compose.runtime.*
import dev.petuska.gtk.compose.foundation.Button
import dev.petuska.gtk.compose.foundation.HBox
import dev.petuska.gtk.compose.foundation.Label
import dev.petuska.gtk.compose.foundation.VBox
import dev.petuska.gtk.compose.ui.application
import dev.petuska.gtk.compose.ui.platform.LocalApplication
import dev.petuska.gtk.compose.ui.window.ApplicationWindow
import dev.petuska.gtk.compose.ui.window.Window
import org.gtkkn.bindings.gio.Settings
import org.gtkkn.bindings.gtk.Application
import org.gtkkn.bindings.gtk.ApplicationWindow
import org.gtkkn.bindings.gtk.Orientation


private const val APP_ID = "dev.petuska.gtk.compose.samples.compose"
fun main(vararg args: String) {
    application(APP_ID, args = args.toList()) {
        val application = LocalApplication.current
        SideEffect {
            application.setAccelsForAction("win.close", listOf("<Ctrl>W"))
        }

        ApplicationWindow(visible = true, title = "GTK Compose") {
            HBox(spacing = 3) {
                var extraWindow by remember { mutableStateOf(false) }
                Button(
                    onClick = {
                        println("Toggling extra $extraWindow")
                        extraWindow = !extraWindow
                    }
                ) {
                    Label(text = if (extraWindow) "Close Window" else "Open Window")
                }
                if (extraWindow) {
                    Window(visible = true, title = "Extra") {
                        Label("Extra Window")
                    }
                }
                TodoWindow()
            }
        }
    }
}

private fun buildApplicationWindow(application: Application): ApplicationWindow {
    val settings = Settings(APP_ID)
    return ApplicationWindow(application).apply {
        title = "GTK K/N Compose"

        val width = settings.getInt("window-width")
        val height = settings.getInt("window-height")
        val isMaximised = settings.getBoolean("is-maximised")
        setDefaultSize(width, height)
        if (isMaximised) maximize()

        connectCloseRequest {
            settings.setInt("window-width", getSize(Orientation.HORIZONTAL))
            settings.setInt("window-height", getSize(Orientation.VERTICAL))
            settings.setBoolean("is-maximised", isMaximized())
            false
        }
    }
}

@Composable
private fun TodoWindow() {
    VBox(spacing = 2) {
        val labels = remember { mutableStateListOf("GTK", "Compose", "Super", "Awesome!") }

        labels.forEach {
            Button(onClick = {
                println("Clicked $it")
                labels.remove(it)
            }) { Label(it) }
        }
        Button(onClick = {
            println("Clicked More!")
            labels.add("Another one")
        }) { Label("More!") }
    }
}
