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
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kermit)
                implementation(libs.compose.runtime)
                implementation("org.gtkkn:gtk4")
            }
        }

        configureEach {
            languageSettings.optIn("dev.petuska.gtk.compose.runtime.internal.GtkComposeInternalApi")
        }
    }
}