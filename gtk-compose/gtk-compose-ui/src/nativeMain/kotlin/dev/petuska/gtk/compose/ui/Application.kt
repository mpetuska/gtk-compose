package dev.petuska.gtk.compose.ui

import androidx.compose.runtime.*
import dev.petuska.gtk.compose.ui.internal.GtkComposeInternalApi
import dev.petuska.gtk.compose.ui.internal.GtkNodeApplier
import dev.petuska.gtk.compose.ui.internal.Logger
import dev.petuska.gtk.compose.ui.node.ContainerScope
import dev.petuska.gtk.compose.ui.node.ContentBuilder
import dev.petuska.gtk.compose.ui.node.GtkContainerNode
import dev.petuska.gtk.compose.ui.platform.GlobalSnapshotManager
import dev.petuska.gtk.compose.ui.platform.LocalApplication
import dev.petuska.gtk.compose.ui.platform.MainUiDispatcher
import dev.petuska.gtk.compose.ui.platform.MainUiThread
import kotlinx.coroutines.*
import org.gtkkn.bindings.gio.ApplicationFlags
import org.gtkkn.bindings.gtk.Application
import org.gtkkn.bindings.gtk.Widget
import kotlin.system.exitProcess

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

/**
 * Scope used by [application], [awaitApplication], [launchApplication]
 */
@Stable
public interface ApplicationScope {
    /**
     * Close all windows created inside the application and cancel all launched effects
     * (they launch via [LaunchedEffect] adn [rememberCoroutineScope].
     */
    public fun exitApplication()
}

public fun application(
    applicationId: String,
    flags: ApplicationFlags = ApplicationFlags.DEFAULT_FLAGS,
    args: List<String> = listOf(),
    exitProcessOnExit: Boolean = true,
    content: @Composable ApplicationScope.() -> Unit
) {
    val code = runBlocking {
        awaitApplication(
            applicationId = applicationId,
            flags = flags,
            args = args,
            content = content
        )
    }
    println("DONE")
    if (exitProcessOnExit) {
        exitProcess(code)
    }
}

public fun CoroutineScope.launchApplication(
    applicationId: String,
    flags: ApplicationFlags = ApplicationFlags.DEFAULT_FLAGS,
    args: List<String> = listOf(),
    content: @Composable ApplicationScope.() -> Unit
): Deferred<Int> {
    return async {
        awaitApplication(
            applicationId = applicationId,
            flags = flags,
            args = args,
            content = content
        )
    }
}

public suspend fun awaitApplication(
    applicationId: String,
    flags: ApplicationFlags = ApplicationFlags.DEFAULT_FLAGS,
    args: List<String> = listOf(),
    content: @Composable ApplicationScope.() -> Unit
): Int = withContext(MainUiThread) {
    val application = Application(applicationId, flags)
    Logger.d("Bootstrapping GTK Composer")
    Logger.v { "Connecting activate signal" }
    application.connectActivate {
        Logger.d { "Application(id=${applicationId}) activated, proceeding with compose mounting" }
        CoroutineScope(MainUiDispatcher).launch(start = CoroutineStart.UNDISPATCHED) {
            Logger.v { "Starting GlobalSnapshotManager" }
            GlobalSnapshotManager.ensureStarted()

            val context = DefaultMonotonicFrameClock + MainUiDispatcher
            val recomposer = Recomposer(context)

            Logger.v { "Launching recomposer" }
            CoroutineScope(context).launch(start = CoroutineStart.UNDISPATCHED) {
                Logger.v { "Recomposing" }
                recomposer.runRecomposeAndApplyChanges()
            }

            Logger.v { "Creating composition" }
            val composition = Composition(
                applier = ApplicationApplier,
                parent = recomposer
            )

            var isOpen by mutableStateOf(true)
            val scope = object : ApplicationScope {
                override fun exitApplication() {
                    isOpen = false
                }
            }

            Logger.v { "Setting initial composition content" }
            try {
                composition.setContent @Composable {
                    if (isOpen) {
                        Logger.v("Mounting compose tree onto Application(id=${applicationId})")
                        CompositionLocalProvider(
                            LocalApplication provides application,
                        ) {
                            scope.content()
                        }
                    } else {
                        Logger.v("Unounting compose tree from Application(id=${applicationId})")
                    }
                }
            } catch (e: Throwable) {
                Logger.e(e) { "Composition failed" }
            }

            Logger.v { "Scheduling recomposer cleanup job" }
            CoroutineScope(context).launch(start = CoroutineStart.DEFAULT) {
                try {
                    Logger.v { "Closing recomposer" }
                    recomposer.close()
                    Logger.v { "Joining recomposer" }
                    recomposer.join()
                } catch (e: Throwable) {
                    Logger.e(e) { "Closing recomposer failed" }
                }
                Logger.v { "Disposing composition" }
                composition.dispose()
                Logger.d { "Recomposer closed, quitting application" }
                application.quit()
            }
            Logger.v { "Finished activating Application($applicationId)" }
        }
    }

    Logger.v { "Connecting shutdown signal" }
    application.connectShutdown {
        Logger.d { "Processing GTK shutdown request" }
        Logger.v { "Cancelling MainUiDispatcher" }
        MainUiDispatcher.cancel()
        MainUiThread.cancel()
    }

    Logger.d { "Starting GTK Application(id=${applicationId})" }
    return@withContext application.run(args.size, args.toList()).also {
        Logger.d { "GTK Application(id=${applicationId}) exited with $it" }

    }
}

private object ApplicationApplier : Applier<Unit> {
    override val current: Unit = Unit
    override fun down(node: Unit) = Unit
    override fun up() = Unit
    override fun insertTopDown(index: Int, instance: Unit) = Unit
    override fun insertBottomUp(index: Int, instance: Unit) = Unit
    override fun remove(index: Int, count: Int) = Unit
    override fun move(from: Int, to: Int, count: Int) = Unit
    override fun clear() = Unit
    override fun onEndChanges() = Unit
}
