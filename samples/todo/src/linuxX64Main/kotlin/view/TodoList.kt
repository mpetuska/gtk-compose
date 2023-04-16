package dev.petuska.gtk.compose.samples.todo.view

import dev.petuska.gtk.compose.samples.todo.view.task.TaskData
import kotlinx.cinterop.reinterpret
import org.gtkkn.bindings.adw.ActionRow
import org.gtkkn.bindings.gio.Settings
import org.gtkkn.bindings.gobject.Object
import org.gtkkn.bindings.gtk.*

fun Application.TodoListBox(tasks: MutableList<TaskData>) = ListBox().apply {
    val settings = Settings(applicationId!!)
    val ids = StringList(tasks.indices.map(Int::toString))
    val filterModel = FilterListModel(ids, settings.filter())
    val selectionModel = NoSelection(filterModel)
    bindModel(selectionModel) {
        tasks.createTaskRow(it)
    }
    cssClasses = listOf("boxed-list")
}

private fun Settings.filter() = when (val f = getString("filter")) {
    "All" -> null
    "Open" -> CustomFilter {
        TODO()
    }

    "Done" -> CustomFilter {
        TODO()
    }

    else -> error("Filter $f is invalid")
}

private fun List<TaskData>.createTaskRow(model: Object) = ActionRow().apply {
    val id = StringObject(model.gPointer.reinterpret()).string
    val data = get(id.toInt())
    CheckButton().apply {
        valign = Align.CENTER
        canFocus = false
        active = data.completed
//        model.bindProperty(
//            sourceProperty = "completed",
//            target = this,
//            targetProperty = "active",
//            flags = BindingFlags.SYNC_CREATE or BindingFlags.BIDIRECTIONAL,
//        )
    }.let(::addPrefix)

    title = data.content
//    model.bindProperty(
//        sourceProperty = "content",
//        target = this,
//        targetProperty = "title",
//        flags = BindingFlags.SYNC_CREATE,
//    )
}