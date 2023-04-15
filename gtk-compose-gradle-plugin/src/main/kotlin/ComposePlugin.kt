@file:Suppress("unused")

package dev.petuska.gtk.compose.gradle.plugin

import dev.petuska.gtk.compose.gradle.plugin.ext.compose
import dev.petuska.gtk.compose.gradle.plugin.ext.gtk
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ComponentMetadataContext
import org.gradle.api.artifacts.ComponentMetadataRule
import org.gradle.api.artifacts.dsl.ComponentModuleMetadataHandler
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.create
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal val composeVersion get() = BuildConfig.composeVersion

class ComposePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.apply(GtkPlugin::class.java)
        project.plugins.apply(ComposeCompilerKotlinSupportPlugin::class.java)
//        val desktopExtension = composeExtension.extensions.create("desktop", DesktopExtension::class.java)
//        val androidExtension = composeExtension.extensions.create("android", AndroidExtension::class.java)
//        val experimentalExtension = composeExtension.extensions.create("experimental", ExperimentalExtension::class.java)
//
        project.dependencies.extensions.create<Dependencies>("compose")
//
//        if (!project.buildFile.endsWith(".gradle.kts")) {
//            setUpGroovyDslExtensions(project)
//        }
//
//        project.initializePreview(desktopExtension)
//        composeExtension.extensions.create("web", WebExtension::class.java)


        project.afterEvaluate {
//            configureDesktop(project, desktopExtension)
//            project.configureExperimental(composeExtension, experimentalExtension)
//            project.checkExperimentalTargetsWithSkikoIsEnabled()
//
//            if (androidExtension.useAndroidX) {
//                project.logger.warn("useAndroidX is an experimental feature at the moment!")
//                RedirectAndroidVariants.androidxVersion = androidExtension.androidxVersion
//                listOf(
//                    RedirectAndroidVariants::class.java,
//                ).forEach(project.dependencies.components::all)
//            }

            fun ComponentModuleMetadataHandler.replaceAndroidx(original: String, replacement: String) {
                module(original) {
                    replacedBy(
                        replacement,
                        "org.jetbrains.compose isn't compatible with androidx.compose, because it is the same library published with different maven coordinates"
                    )
                }
            }

            project.tasks.withType(KotlinCompile::class.java).configureEach {
                kotlinOptions.apply {
                    freeCompilerArgs = freeCompilerArgs +
                            gtk.compose.kotlinCompilerPluginArgs.get().flatMap { arg ->
                                listOf("-P", "plugin:androidx.compose.compiler.plugins.kotlin:$arg")
                            }
                }
            }
        }
    }

    class RedirectAndroidVariants : ComponentMetadataRule {
        override fun execute(context: ComponentMetadataContext) = with(context.details) {
            if (id.group.startsWith("org.jetbrains.compose")) {
                val group = id.group.replaceFirst("org.jetbrains.compose", "androidx.compose")
                val newReference = "$group:${id.module.name}:$androidxVersion"
                listOf(
                    "debugApiElements-published",
                    "debugRuntimeElements-published",
                    "releaseApiElements-published",
                    "releaseRuntimeElements-published"
                ).forEach { variantNameToAlter ->
                    withVariant(variantNameToAlter) {
                        withDependencies {
                            removeAll { true } //there are references to org.jetbrains artifacts now
                            add(newReference)
                        }
                    }
                }
            }
        }

        companion object {
            var androidxVersion: String? = null
        }
    }

    abstract class Dependencies() {
        val desktop = DesktopDependencies
        val compiler = CompilerDependencies()
        val animation get() = composeDependency("org.jetbrains.compose.animation:animation")
        val animationGraphics get() = composeDependency("org.jetbrains.compose.animation:animation-graphics")
        val foundation get() = composeDependency("org.jetbrains.compose.foundation:foundation")
        val material get() = composeDependency("org.jetbrains.compose.material:material")
        val material3 get() = composeDependency("org.jetbrains.compose.material3:material3")
        val runtime get() = composeDependency("org.jetbrains.compose.runtime:runtime")
        val ui get() = composeDependency("org.jetbrains.compose.ui:ui")
        val uiTooling get() = composeDependency("org.jetbrains.compose.ui:ui-tooling")
        val preview get() = composeDependency("org.jetbrains.compose.ui:ui-tooling-preview")
        val materialIconsExtended get() = composeDependency("org.jetbrains.compose.material:material-icons-extended")
        val components get() = CommonComponentsDependencies
        val html: HtmlDependencies get() = HtmlDependencies
    }

    object DesktopDependencies {
        val components = DesktopComponentsDependencies

        val common = composeDependency("org.jetbrains.compose.desktop:desktop")
        val linux_x64 = composeDependency("org.jetbrains.compose.desktop:desktop-jvm-linux-x64")
        val linux_arm64 = composeDependency("org.jetbrains.compose.desktop:desktop-jvm-linux-arm64")
        val windows_x64 = composeDependency("org.jetbrains.compose.desktop:desktop-jvm-windows-x64")
        val macos_x64 = composeDependency("org.jetbrains.compose.desktop:desktop-jvm-macos-x64")
        val macos_arm64 = composeDependency("org.jetbrains.compose.desktop:desktop-jvm-macos-arm64")
    }

    class CompilerDependencies() {
        fun forKotlin(version: String) = "org.jetbrains.compose.compiler:compiler:" +
                ComposeCompilerCompatibility.compilerVersionFor(version)

        /**
         * Compose Compiler that is chosen by the version of Kotlin applied to the Gradle project
         */
//        val auto get() = forKotlin(project.getKotlinPluginVersion())
    }

    object CommonComponentsDependencies {
        val resources = composeDependency("org.jetbrains.compose.components:components-resources")
    }

    object DesktopComponentsDependencies {
        val splitPane = composeDependency("org.jetbrains.compose.components:components-splitpane")

        val animatedImage = composeDependency("org.jetbrains.compose.components:components-animatedimage")
    }

    @Deprecated("Use compose.html")
    object WebDependencies {
        val core by lazy {
            composeDependency("org.jetbrains.compose.html:html-core")
        }

        val svg by lazy {
            composeDependency("org.jetbrains.compose.html:html-svg")
        }

        val testUtils by lazy {
            composeDependency("org.jetbrains.compose.html:html-test-utils")
        }
    }

    object HtmlDependencies {
        val core by lazy {
            composeDependency("org.jetbrains.compose.html:html-core")
        }

        val svg by lazy {
            composeDependency("org.jetbrains.compose.html:html-svg")
        }

        val testUtils by lazy {
            composeDependency("org.jetbrains.compose.html:html-test-utils")
        }
    }
}

fun KotlinDependencyHandler.compose(groupWithArtifact: String) = composeDependency(groupWithArtifact)

fun DependencyHandler.compose(groupWithArtifact: String) = composeDependency(groupWithArtifact)

private fun composeDependency(groupWithArtifact: String) = "$groupWithArtifact:$composeVersion"
