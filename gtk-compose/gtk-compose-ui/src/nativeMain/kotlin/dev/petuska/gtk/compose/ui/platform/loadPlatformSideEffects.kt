package dev.petuska.gtk.compose.ui.platform

import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers

/**
 * Sets up required side effects on the platform that the compose tree later depends on.
 *
 * **Must be called as early in the program as possible**
 */
internal fun loadPlatformSideEffects(
    logger: Logger,
) {
    logger.d { "Loading platform side effects" }

    logger.v { "Injecting GioMainDispatcher as Dispatchers.Main" }
    @Suppress("INVISIBLE_MEMBER")
    Dispatchers.injectMain(GioMainDispatcher(Dispatchers.Default, false, logger))
}