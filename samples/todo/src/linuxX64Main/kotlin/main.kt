package dev.petuska.gtk.compose.samples.todo

import dev.petuska.gtk.compose.samples.todo.view.TodoWindow
import org.gtkkn.bindings.gio.ApplicationFlags
import org.gtkkn.bindings.adw.Application
import org.gtkkn.bindings.gio.Menu
import org.gtkkn.bindings.gio.MenuItem
import org.gtkkn.bindings.gio.MenuModel
import org.gtkkn.bindings.glib.Variant


private const val APP_ID = "dev.petuska.gtk.compose.samples.todo"

fun main(vararg args: String) {
    val app = Application(APP_ID, ApplicationFlags.DEFAULT_FLAGS)
    app.connectActivate {
        app.TodoWindow().present()
    }
    app.setAccelsForAction("win.close", listOf("<Ctrl>W"))
    app.run(args.size, args.toList())
}
