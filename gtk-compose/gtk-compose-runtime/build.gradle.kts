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
                implementation("org.jetbrains.compose.runtime:runtime:1.4.0")
                api("org.gtkkn:gtk4")
            }
        }
    }
}