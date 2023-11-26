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
        gtk.gschemas.main.preinstall(compilations.named("main"))
    }
    sourceSets {
        all {
            languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
        }
        commonMain {
            dependencies {
                implementation(project(":gtk-compose:gtk-compose-foundation"))
            }
        }
    }
}