plugins {
    kotlin("multiplatform")
    id("dev.petuska.gtk.compose")
}

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()
    linuxX64()
    sourceSets {
        named("linuxX64Main") {
            dependencies {
                api(project(":gtk-compose:gtk-compose-runtime"))
            }
        }
    }
}