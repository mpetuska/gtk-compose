package dev.petuska.gtk.compose.runtime

import androidx.compose.runtime.*
import co.touchlab.kermit.*
import dev.petuska.gtk.compose.runtime.internal.*
import dev.petuska.gtk.compose.runtime.node.ContainerScope
import dev.petuska.gtk.compose.runtime.node.ContentBuilder
import dev.petuska.gtk.compose.runtime.node.GtkContainerNode
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKStringFromUtf8
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.gtkkn.bindings.gtk.Widget
import platform.posix.getenv


@GtkComposeInternalApi
@OptIn(ExperimentalForeignApi::class)
internal val Logger = Logger(
    loggerConfigInit(
        minSeverity = (getenv("LOG_LEVEL")?.toKStringFromUtf8() ?: "INFO").let { env ->
            env.toIntOrNull()?.let { Severity.entries[it] } ?: Severity.valueOf(
                env.lowercase().replaceFirstChar(Char::uppercase)
            )
        },
        logWriters = arrayOf(platformLogWriter(LogFormatter)),
    ),
    "gtk-compose-runtime"
)

/**
 * Use this method to mount the composition at the certain [root]
 *
 * @param root - the [Widget] that will be the root of the GTK tree managed by Compose
 * @param content - the Composable lambda that defines the composition content
 *
 * @return the instance of the [Composition]
 */
@GtkComposeInternalApi
public fun <TWidget : Widget> renderComposable(
    root: GtkContainerNode<TWidget>,
    monotonicFrameClock: MonotonicFrameClock = DefaultMonotonicFrameClock,
    content: ContentBuilder<TWidget>,
): Composition {
    Logger.d("Bootstrapping GTK Composer")
    GlobalSnapshotManager.ensureStarted()
    val context = monotonicFrameClock + Dispatchers.Default
    val recomposer = Recomposer(context)

    CoroutineScope(context).launch(start = CoroutineStart.UNDISPATCHED) {
        recomposer.runRecomposeAndApplyChanges()
    }

    val composition = ControlledComposition(
        applier = GtkNodeApplier(root),
        parent = recomposer
    )
    val scope = object : ContainerScope<TWidget> {
        override val DisposableEffectScope.scopeElement: TWidget
            get() = root.widget
    }
    Logger.d("Mounting compose tree onto $root")
    composition.setContent @Composable {
        content(scope)
    }
    Logger.d("Finished bootstrapping GTK Composer")
    return composition
}
