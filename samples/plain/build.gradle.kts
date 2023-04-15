plugins {
    kotlin("multiplatform")
}

repositories {
    mavenCentral()
}

kotlin {
    linuxX64 {
        binaries.executable {
            entryPoint = "dev.petuska.gtk.compose.samples.plain.main"
        }
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation("org.gtkkn:adw")
            }
        }
    }
}

tasks {
    val linuxX64ProcessResources = named<Copy>("linuxX64ProcessResources")
    val schemasDest = "${System.getenv("HOME")}/.local/share/glib-2.0/schemas/"
    val copyGSchemas by registering(Copy::class) {
        dependsOn(linuxX64ProcessResources)
        from(linuxX64ProcessResources) {
            include("**/*.gschema.xml")
        }
        into(schemasDest)
    }
    val compileGSchemas by registering(Exec::class) {
        dependsOn(copyGSchemas)
        executable = "glib-compile-schemas"
        args(schemasDest)
    }
    named("runDebugExecutableLinuxX64") {
        dependsOn(compileGSchemas)
    }
    named("runReleaseExecutableLinuxX64") {
        dependsOn(compileGSchemas)
    }
}