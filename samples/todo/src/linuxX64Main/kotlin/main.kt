package dev.petuska.gtk.compose.samples.todo

import org.gtkkn.bindings.gio.ApplicationFlags
import org.gtkkn.bindings.gio.Settings
import org.gtkkn.bindings.gio.SettingsBindFlags
import org.gtkkn.bindings.gobject.BindingFlags
import org.gtkkn.bindings.gtk.*
import kotlin.Unit


private const val APP_ID = "dev.petuska.gtk.compose.samples.todo"

fun main(vararg args: String) {
    val app = Application(APP_ID, ApplicationFlags.DEFAULT_FLAGS)
    app.connectActivate { buildUI(app) }
    app.run(args.size, args.toList())
}

// UI

private fun DemoBox(title: String, content: Box.() -> Unit) = Box(Orientation.VERTICAL, 20).apply {
    marginTop = 10
    Label(title).apply {
        justify = Justification.CENTER
    }.let(::append)
    Box(Orientation.VERTICAL, 12).apply {
        content()
    }.let(::append)
}

private fun buildUI(app: Application) = MyWindow(app).apply {
    title = "Hello Kotlin"
    setDefaultSize(600, 300)
    child = ScrolledWindow().apply {
        setPolicy(PolicyType.NEVER, PolicyType.AUTOMATIC)
        minContentWidth = 360
        child = Box(Orientation.VERTICAL, 12).apply {
            append(buttons())
            append(switches())
            append(lists())
        }
    }

    present()
}

private fun buttons() = DemoBox("Buttons") {
    var number = 0
    val countLabel = Label("$number")
    val buttonIncrease = Button().apply {
        setLabel("Increase")
        margin(12)
        align(Align.CENTER)
        connectClicked {
            number++
            countLabel.setLabel("$number")
        }
    }
    val buttonDecrease = Button().apply {
        setLabel("Decrease")
        margin(12)
        align(Align.CENTER)
        connectClicked {
            number--
            countLabel.setLabel("$number")
        }
    }

    append(buttonIncrease)
    append(countLabel)
    append(buttonDecrease)
}

private fun switches() = DemoBox("Switches") {
    val settings = Settings(APP_ID)

    val switch1 = Switch().apply {
        margin(48)
        align(Align.CENTER)
        connectStateSet {
            settings.setBoolean("is-switch-enabled", it)
            false
        }
    }
    settings.bind("is-switch-enabled", switch1, "active", SettingsBindFlags.DEFAULT)
    val switch2 = Switch().apply {
        margin(48)
        align(Align.CENTER)
        bindProperty("state", switch1, "state", BindingFlags.BIDIRECTIONAL)
    }

    append(switch1)
    append(switch2)
}

private fun lists() = DemoBox("Lists") {
    repeat(100) {
        append(Label("$it"))
    }
}

// Utils

fun Widget.margin(margin: Int) {
    marginBottom = margin
    marginTop = margin
    marginStart = margin
    marginEnd = margin
}

fun Widget.align(align: Align) {
    valign = align
    halign = align
}

class MyWindow(app: Application) : ApplicationWindow(app) {
    private val settings: Settings

    init {
        settings = Settings(app.applicationId!!)
        loadWindowSize()
        connectCloseRequest {
            saveWindowSize()
            false
        }
    }

    private fun saveWindowSize() {
        val width = this.getSize(Orientation.HORIZONTAL)
        val height = this.getSize(Orientation.VERTICAL)

        settings.setInt("window-width", width)
        settings.setInt("window-height", height)
        settings.setBoolean("is-maximised", isMaximized())
    }

    private fun loadWindowSize() {
        val width = settings.getInt("window-width")
        val height = settings.getInt("window-height")
        val isMaximised = settings.getBoolean("is-maximised")
        setDefaultSize(width, height)
        if (isMaximised) maximize()
    }
}