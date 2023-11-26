import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable

@Composable
fun App() {
    val labels = remember { mutableStateListOf("0", "1", "2", "3", "4", "5", "6") }

    labels.forEach { label ->
        Button(attrs = {
            onClick {
                println("Clicked $label")
                labels.remove(label)
            }
        }) { Text(label) }
    }
}

fun main() = renderComposable("root") {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        repeat(5) {
            println("Launching window in ${5 - it}s")
            delay(1000)
        }
        visible = true
        println("Window launched $visible")
    }

    Div(
        attrs = {
            style {
                display(if (visible) DisplayStyle.Block else DisplayStyle.None)
            }
        }
    ) {
        App()
    }
}
