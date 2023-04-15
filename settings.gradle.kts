rootProject.name = "GTK-Compose"

includeBuild("./gtk-kn")
includeBuild("./gtk-compose-gradle-plugin")

include(
    ":gtk-compose:gtk-compose-runtime",
    ":gtk-compose:gtk-compose-widgets",
)

include(
    ":samples:plain",
    ":samples:compose"
)
