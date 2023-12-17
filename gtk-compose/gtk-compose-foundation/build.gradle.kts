import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    kotlin("multiplatform")
    id("dev.petuska.gtk.compose")
}

repositories {
    mavenCentral()
}

kotlin {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    targetHierarchy.default()
    explicitApi()
    linuxX64()
    sourceSets {
        named("nativeMain") {
            dependencies {
                api(project(":gtk-compose:gtk-compose-ui"))
            }
        }

        configureEach {
            languageSettings.optIn("dev.petuska.gtk.compose.ui.internal.GtkComposeInternalApi")
        }
    }
}