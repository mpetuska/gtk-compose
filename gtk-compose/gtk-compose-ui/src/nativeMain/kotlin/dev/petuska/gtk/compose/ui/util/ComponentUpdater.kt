package dev.petuska.gtk.compose.ui.util

import dev.petuska.gtk.compose.ui.internal.GtkComposeInternalApi

/**
 * Stores the previous applied state, and provide ability to update component if the new state is
 * changed.
 */
@GtkComposeInternalApi
public class ComponentUpdater {
    private var updatedValues = mutableListOf<Any?>()

    public fun update(body: UpdateScope.() -> Unit) {
        UpdateScope().body()
    }

    public inner class UpdateScope {
        private var index = 0

        /**
         * Compare [value] with the old one and if it is changed - store a new value and call
         * [update]
         */
        public fun <T : Any?> set(value: T, update: (T) -> Unit) {
            if (index < updatedValues.size) {
                if (updatedValues[index] != value) {
                    update(value)
                    updatedValues[index] = value
                }
            } else {
                check(index == updatedValues.size)
                update(value)
                updatedValues.add(value)
            }

            index++
        }
    }
}