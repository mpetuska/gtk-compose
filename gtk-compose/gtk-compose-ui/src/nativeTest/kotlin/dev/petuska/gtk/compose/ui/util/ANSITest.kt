package dev.petuska.gtk.compose.ui.util

import dev.petuska.gtk.compose.ui.util.ANSI.bright
import dev.petuska.gtk.compose.ui.util.ANSI.println
import kotlin.test.Test

class ANSITest {
    @Test
    fun printAll() {
        printWithModifier(null)
    }

    private fun printWithModifier(modifier: ANSI.Modifier?) {
        if (modifier != null) {
            println("+++++ Base Colours +++++")
            ANSI.Colour.println("Default Colour")
            ANSI.Colour.entries.forEach { colour ->
                colour.modify(modifier).println("${colour.name} Colour")
            }

            println("+++++ Bright Colours +++++")
            ANSI.Colour.println("Bright Default Colour")
            ANSI.Colour.entries.forEach { colour ->
                colour.modify(modifier).bright.println("Bright ${colour.name} Colour")
            }

            println("+++++ Base Backgrounds +++++")
            ANSI.Background.println("Default Background")
            ANSI.Background.entries.forEach { background ->
                background.modify(modifier).println("${background.name} Background")
            }

            println("+++++ Bright Backgrounds +++++")
            ANSI.Background.println("Bright Default Background")
            ANSI.Background.entries.forEach { background ->
                background.modify(modifier).bright.println("Bright ${background.name} Background")
            }
        } else {
            ANSI.Modifier.entries.forEach { mod ->
                println("===== Modifier ${mod.name} =====")
                printWithModifier(mod)
            }
        }
    }
}