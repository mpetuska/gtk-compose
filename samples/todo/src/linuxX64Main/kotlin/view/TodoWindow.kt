package dev.petuska.gtk.compose.samples.todo.view

import dev.petuska.gtk.compose.samples.todo.view.task.TaskData
import org.gtkkn.bindings.adw.Clamp
import org.gtkkn.bindings.adw.HeaderBar
import org.gtkkn.bindings.gio.Menu
import org.gtkkn.bindings.gio.MenuItem
import org.gtkkn.bindings.gio.Settings
import org.gtkkn.bindings.gio.SimpleAction
import org.gtkkn.bindings.glib.Variant
import org.gtkkn.bindings.gtk.*

fun Application.TodoWindow() = ApplicationWindow(this).apply {
    val settings = Settings(applicationId!!)

    title = "K/N GTK Todo"
    run {
        setDefaultSize(600, 300)
        val width = settings.getInt("window-width")
        val height = settings.getInt("window-height")
        val isMaximised = settings.getBoolean("is-maximised")
        setDefaultSize(width, height)
        if (isMaximised) maximize()
    }
    connectCloseRequest {
        val width = this.getSize(Orientation.HORIZONTAL)
        val height = this.getSize(Orientation.VERTICAL)

        settings.setInt("window-width", width)
        settings.setInt("window-height", height)
        settings.setBoolean("is-maximised", isMaximized())
        false
    }

    setupActions()
    titlebar = HeaderBar().apply {
        MenuButton().apply {
            setIconName("open-menu-symbolic")
            menuModel = menuBar()
        }.let(::packEnd)
    }
    child = ScrolledWindow().apply {
        setPolicy(PolicyType.NEVER, PolicyType.AUTOMATIC)
        minContentHeight = 360
        vexpand = true
        child = Clamp().apply {
            child = Box(Orientation.VERTICAL, 18).apply {
                marginTop = 24
                marginBottom = 24
                marginStart = 12
                marginEnd = 12

                Entry().apply {
                    placeholderText = "Enter a Task..."
                    setIconFromIconName(EntryIconPosition.SECONDARY, "list-add-symbolic")
                }.let(::append)
                val tasks = mutableListOf<TaskData>(
                    TaskData(false, "Task Number One"),
                    TaskData(false, "Task Number Two"),
                    TaskData(false, "Task Number Three"),
                )
                TodoListBox(tasks).apply {
                    visible = true
                }.let(::append)
            }
        }
    }
}

fun menuBar() = Menu().apply {
    MenuItem("_Close Window", "window.close").let(::appendItem)
    MenuItem("_Sensitive button", "win.sensitive-button").let(::appendItem)
    Menu().apply {
        MenuItem("_Horizontal", "win.orientation").apply{
            setAttributeValue("target", Variant.newString("Horizontal"))
        }.let(::appendItem)
        MenuItem("_Vertical", "win.orientation").apply{
            setAttributeValue("target", Variant.newString("Vertical"))
        }.let(::appendItem)
    }.let { appendSection("Orientation", it) }
}

private fun ApplicationWindow.setupActions() {
    SimpleAction("close", null).apply {
        connectActivate {
            close()
        }
    }.let(::addAction)
}
