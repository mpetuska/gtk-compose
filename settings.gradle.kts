rootProject.name = "GTK-Compose"

includeBuild("./gtk-kn")

include(
    ":gtk-compose:gtk-compose-runtime",
)

include(
    ":samples:plain",
    ":samples:compose"
)