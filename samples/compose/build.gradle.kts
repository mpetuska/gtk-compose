plugins {
    kotlin("multiplatform")
    id("dev.petuska.gtk.compose")
}

repositories {
    mavenCentral()
}

kotlin {
    linuxX64 {
        binaries.executable {
            entryPoint = "dev.petuska.gtk.compose.samples.compose.main"
        }
    }
    sourceSets {
        named("linuxX64Main") {
            dependencies {
                implementation("org.jetbrains.compose.runtime:runtime:1.4.0")
                implementation(project(":gtk-compose:gtk-compose-runtime"))
                implementation(project(":gtk-compose:gtk-compose-widgets"))
            }
        }
    }
}