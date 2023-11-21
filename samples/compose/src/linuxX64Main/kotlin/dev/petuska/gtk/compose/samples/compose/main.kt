package dev.petuska.gtk.compose.samples.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import dev.petuska.gtk.compose.foundation.Box
import dev.petuska.gtk.compose.foundation.Button
import dev.petuska.gtk.compose.foundation.Label
import dev.petuska.gtk.compose.foundation.renderComposable
import org.gtkkn.bindings.gio.ApplicationFlags
import org.gtkkn.bindings.gio.Settings
import org.gtkkn.bindings.gtk.Application
import org.gtkkn.bindings.gtk.ApplicationWindow
import org.gtkkn.bindings.gtk.Orientation


private const val APP_ID = "dev.petuska.gtk.compose.samples.compose"
fun main(vararg args: String) {
    val application = Application(APP_ID, ApplicationFlags.DEFAULT_FLAGS)
    application.connectActivate {
        renderComposable(buildApplicationWindow(application)) { TodoWindow() }
    }
    application.setAccelsForAction("win.close", listOf("<Ctrl>W"))
    application.run(args.size, args.toList())
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
