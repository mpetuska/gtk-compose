plugins {
    kotlin("multiplatform")
    id("dev.petuska.gtk")
}

repositories {
    mavenCentral()
}

kotlin {
    linuxX64 {
        binaries.executable {
            entryPoint = "dev.petuska.gtk.compose.samples.todo.main"
        }
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation("org.gtkkn:adwaita")
            }
        }
    }
}
