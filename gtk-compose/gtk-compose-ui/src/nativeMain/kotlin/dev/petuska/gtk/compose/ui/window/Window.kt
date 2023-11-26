package dev.petuska.gtk.compose.ui.window

import androidx.compose.runtime.*
import dev.petuska.gtk.compose.ui.internal.GtkComposeInternalApi
import dev.petuska.gtk.compose.ui.internal.GtkNodeApplier
import dev.petuska.gtk.compose.ui.node.ContentBuilder
import dev.petuska.gtk.compose.ui.node.GtkParentNode
import dev.petuska.gtk.compose.ui.node.LazyNodeScope
import dev.petuska.gtk.compose.ui.platform.LocalApplication
import org.gtkkn.bindings.gtk.ApplicationWindow
import org.gtkkn.bindings.gtk.Widget
import org.gtkkn.bindings.gtk.Window

@GtkComposeInternalApi
public class WindowNode(override val widget: Window) : GtkParentNode<Window>() {

    override fun add(child: Widget) {
        widget.child = child
    }

    override fun clear() {
        widget.child = null
    }
}

private fun WindowNode.setContent(
    parentComposition: CompositionContext,
    content: ContentBuilder<Window>
): Composition {
    val applier = GtkNodeApplier(this)
    val scope = LazyNodeScope<Window>().also { it.node = this }
    val composition = Composition(applier, parentComposition)
    composition.setContent {
        scope.content()
    }
    return composition
}

@Composable
public fun Window(
    visible: Boolean,
    child: ContentBuilder<Window>
) {
    val application = LocalApplication.current
    val parentComposition = rememberCompositionContext()
    val node = remember {
        WindowNode(Window().apply {
            println("Setting Application on Window: ${name}")
            setApplication(application)
        })
    }
    GtkWindow(
        visible = visible,
        create = {
            node.apply {
                setContent(parentComposition, child)
            }
        },
        dispose = {

        },
        update = {},
    )


//    val application = LocalApplication.current
//    val nodeRef = remember { Ref<WindowNode>() }
//    fun node() = nodeRef.value!!
//
//    DisposableEffect(Unit) {
//        nodeRef.value = WindowNode(Window().apply {
//            println("NAME: ${name}")
//            setApplication(application)
//        })
//        onDispose {
//            nodeRef.value?.widget?.close()
//        }
//    }
//    DisposableEffect(visible) {
//        val showJob = GlobalScope.launch(MainUIDispatcher) {
//            println("VISIBLE: $visible")
//            if (visible) node().widget.present() else node().widget.hide()
//        }
//
//        onDispose {
//            showJob.cancel()
//        }
//    }
//
//    DisposableEffect(Unit) {
//        val node = node()
//        val applier = GtkNodeApplier(node)
//        val scope = LazyNodeScope<Window>().also { it.node = node }
//        val composition = Composition(applier, parentComposition)
//        composition.setContent {
//            scope.child()
//        }
//
//        onDispose {
//            println("Disposing window")
//            composition.dispose()
//            node.widget.close()
//        }
//    }

//    ComposeNode<WindowNode, GtkNodeApplier>(
//        factory = { node },
//        update = {
//            set(visible) {
//                if(visible) widget.present() else widget.hide()
//            }
//        }
//    )
}