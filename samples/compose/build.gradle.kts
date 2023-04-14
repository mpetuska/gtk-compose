plugins {
    kotlin("multiplatform") version "1.8.20"
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
        commonMain {
            dependencies {
                implementation(project(":gtk-compose:gtk-compose-runtime"))
            }
        }
    }
}