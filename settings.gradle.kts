import java.util.*

rootProject.name = "GTK-Compose"

val properties = (extra.properties + (rootDir.resolve("local.properties").takeIf(File::exists)?.let {
    Properties().apply {
        it.inputStream().use(this::load)
    }.toMap()
} ?: mapOf())).map { (k, v) -> "$k" to "$v" }.toMap()

includeBuild("./gtk-kn")
includeBuild("./gtk-compose-gradle-plugin")

include(
    ":gtk-compose:gtk-compose-ui",
    ":gtk-compose:gtk-compose-foundation",
)

if (properties["dev.petuska.gtk.compose.samples.disable"] != "true") {
    include(
        ":samples:compose",
        ":samples:desktop-sandbox",
        ":samples:html-sandbox",
        ":samples:plain",
        ":samples:todo",
    )
}
