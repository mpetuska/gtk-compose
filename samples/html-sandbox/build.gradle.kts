plugins {
    kotlin("multiplatform")
    alias(libs.plugins.compose)
}

repositories {
    mavenCentral()
    google()
}

kotlin {
    js {
        browser()
    }
    sourceSets {
        named("jsMain") {
            dependencies {
                implementation(compose.html.core)
            }
        }
    }
}
