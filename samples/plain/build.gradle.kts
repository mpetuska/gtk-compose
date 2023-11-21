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
            entryPoint = "dev.petuska.gtk.compose.samples.plain.main"
        }
        gtk.gschemas.main.preinstall(compilations.named("main"))
    }
    sourceSets {
        all {
            languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
        }
        commonMain {
            dependencies {
                implementation("org.gtkkn:adwaita")
            }
        }
    }
}
