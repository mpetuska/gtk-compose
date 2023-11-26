import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    kotlin("multiplatform")
    id("dev.petuska.gtk.compose")
    alias(libs.plugins.kotlinx.atomicfu)
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
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kermit)
                implementation(libs.kotlinx.atomicfu)
                api(libs.compose.runtime)
                api("org.gtkkn:gtk4")
            }
        }

        configureEach {
            languageSettings.optIn("dev.petuska.gtk.compose.ui.internal.GtkComposeInternalApi")
        }
    }
}