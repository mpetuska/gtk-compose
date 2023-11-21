import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import java.util.Locale

plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.versions)
    alias(libs.plugins.versions.update)
}

repositories {
    mavenCentral()
}

versionCatalogUpdate {
    keep {
        keepUnusedVersions.set(true)
        keepUnusedLibraries.set(true)
        keepUnusedPlugins.set(true)
    }
}

val isNonStable: (String) -> Boolean = { version ->
    val stableKeyword = setOf("RELEASE", "FINAL", "GA").any { version.uppercase(Locale.ROOT).contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    !stableKeyword && !(version matches regex)
}

tasks.withType<DependencyUpdatesTask>() {
    gradleReleaseChannel = "current"
    rejectVersionIf {
        isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
}
