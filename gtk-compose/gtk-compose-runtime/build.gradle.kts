plugins {
    kotlin("multiplatform") version "1.8.20"
}

repositories {
    mavenCentral()
}

kotlin {
    linuxX64()
    sourceSets {
        commonMain {
            dependencies {
                implementation("org.gtkkn:adw")
            }
        }
    }
}