package dev.petuska.gtk.compose.ui

import androidx.compose.runtime.*
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.loggerConfigInit
import co.touchlab.kermit.platformLogWriter
import dev.petuska.gtk.compose.ui.internal.GtkComposeInternalApi
import dev.petuska.gtk.compose.ui.internal.GtkNodeApplier
import dev.petuska.gtk.compose.ui.node.ContainerScope
import dev.petuska.gtk.compose.ui.node.ContentBuilder
import dev.petuska.gtk.compose.ui.node.GtkContainerNode
import dev.petuska.gtk.compose.ui.platform.*
import dev.petuska.gtk.compose.ui.util.AnsiLogFormatter
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKStringFromUtf8
import kotlinx.coroutines.*
import org.gtkkn.bindings.gio.ApplicationFlags
import org.gtkkn.bindings.gtk.Application
import org.gtkkn.bindings.gtk.Widget
import platform.posix.getenv
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
        applier = GtkNodeApplier(root, Logger),
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

@OptIn(ExperimentalForeignApi::class)
private fun buildLogger(): Logger {
    return Logger(
        loggerConfigInit(
            minSeverity = (getenv("LOG_LEVEL")?.toKStringFromUtf8() ?: "INFO").let { env ->
                env.toIntOrNull()?.let { Severity.entries[it] } ?: Severity.valueOf(
                    env.lowercase().replaceFirstChar(Char::uppercase)
                )
            },
            logWriters = arrayOf(platformLogWriter(AnsiLogFormatter)),
        ),
        "gtk-compose"
    )
}

public suspend fun awaitApplication(
    applicationId: String,
    flags: ApplicationFlags = ApplicationFlags.DEFAULT_FLAGS,
    args: List<String> = listOf(),
    content: @Composable ApplicationScope.() -> Unit
): Int = withContext(MainUiThread) {
    val logger = buildLogger()
    loadPlatformSideEffects(logger)

    val application = Application(applicationId, flags)
    logger.d("Bootstrapping GTK Composer")
    logger.v { "Connecting activate signal" }
    application.connectActivate {
        logger.d { "Application(id=${applicationId}) activated, proceeding with compose mounting" }
        CoroutineScope(MainUiDispatcher).launch(start = CoroutineStart.UNDISPATCHED) {
            logger.v { "Starting GlobalSnapshotManager" }
            GlobalSnapshotManager.ensureStarted()

            val context = DefaultMonotonicFrameClock + MainUiDispatcher
            val recomposer = Recomposer(context)

            logger.v { "Launching recomposer" }
            CoroutineScope(context).launch(start = CoroutineStart.UNDISPATCHED) {
                logger.v { "Recomposing" }
                recomposer.runRecomposeAndApplyChanges()
            }

            logger.v { "Creating composition" }
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

            logger.v { "Setting initial composition content" }
            try {
                composition.setContent @Composable {
                    if (isOpen) {
                        logger.v("Mounting compose tree onto Application(id=${applicationId})")
                        CompositionLocalProvider(
                            LocalApplication provides application,
                            LocalLogger provides logger,
                        ) {
                            scope.content()
                        }
                    } else {
                        logger.v("Unounting compose tree from Application(id=${applicationId})")
                    }
                }
            } catch (e: Throwable) {
                logger.e(e) { "Composition failed" }
            }

            logger.v { "Scheduling recomposer cleanup job" }
            CoroutineScope(context).launch(start = CoroutineStart.DEFAULT) {
                try {
                    logger.v { "Closing recomposer" }
                    recomposer.close()
                    logger.v { "Joining recomposer" }
                    recomposer.join()
                } catch (e: Throwable) {
                    logger.e(e) { "Closing recomposer failed" }
                }
                logger.v { "Disposing composition" }
                composition.dispose()
                logger.d { "Recomposer closed, quitting application" }
                application.quit()
            }
            logger.v { "Finished activating Application($applicationId)" }
        }
    }

    logger.v { "Connecting shutdown signal" }
    application.connectShutdown {
        logger.d { "Processing GTK shutdown request" }
        logger.v { "Cancelling MainUiDispatcher" }
        MainUiDispatcher.cancel()
        MainUiThread.cancel()
    }

    logger.d { "Starting GTK Application(id=${applicationId})" }
    return@withContext application.run(args.size, args.toList()).also {
        logger.d { "GTK Application(id=${applicationId}) exited with $it" }
        logger.v { "VERBOSE" }
        logger.d { "DEBUG" }
        logger.i { "INFO" }
        logger.w { "WARNING" }
        logger.e { "ERROR" }
        logger.a { "ASSERT" }
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
