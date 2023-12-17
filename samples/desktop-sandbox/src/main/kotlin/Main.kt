import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Dispatchers.Main

        val labels = remember { mutableStateListOf("0", "1", "2", "3", "4", "5", "6") }

        Column {
            labels.forEach {
                Button(onClick = {
                    println("Clicked $it")
                    labels.remove(it)
                }) { Text(it) }
            }
        }
    }
}

fun main() = application {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        repeat(5) {
            println("Launching window in ${5 - it}s")
            delay(1000)
        }
        visible = true
        println("Window launched $visible")
    }

    Window(visible = visible, onCloseRequest = ::exitApplication) {
        App()
    }
}
