package dev.petuska.gtk.compose.samples.compose

import androidx.compose.runtime.*
import dev.petuska.gtk.compose.foundation.Box
import dev.petuska.gtk.compose.foundation.Button
import dev.petuska.gtk.compose.foundation.Label
import dev.petuska.gtk.compose.ui.application
import dev.petuska.gtk.compose.ui.platform.LocalApplication
import dev.petuska.gtk.compose.ui.window.ApplicationWindow
import kotlinx.coroutines.delay
import org.gtkkn.bindings.gio.Settings
import org.gtkkn.bindings.gtk.Application
import org.gtkkn.bindings.gtk.ApplicationWindow
import org.gtkkn.bindings.gtk.Orientation


private const val APP_ID = "dev.petuska.gtk.compose.samples.compose"
fun main(vararg args: String) {
    application(APP_ID, args = args.toList()) {
        var visible by remember { mutableStateOf(false) }
        var title by remember { mutableStateOf<String?>(null) }
        val application = LocalApplication.current
        SideEffect {
            application.setAccelsForAction("win.close", listOf("<Ctrl>W"))
        }

        LaunchedEffect(Unit) {
            repeat(5) {
                println("Launching window in ${5 - it}s")
                delay(1000)
            }
            visible = true
            println("Window launched $visible")
        }
        LaunchedEffect(visible) {
            repeat(5) {
                println("Changing window title in ${5 - it}s")
                delay(1000)
            }
            title = if (visible) {
                "GTK Compose"
            } else {
                "Hidden"
            }
            println("Window title changed: $title")
        }

        ApplicationWindow(visible = visible, title = title) {
            Box {
                Button(
                    onClick = { visible = false }
                ) {
                    Label("Hide")
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
    Box {
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
