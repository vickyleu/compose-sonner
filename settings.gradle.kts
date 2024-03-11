@file:Suppress("UnstableApiUsage")

import java.util.Properties


rootProject.name = "compose-sonner"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    listOf(repositories, dependencyResolutionManagement.repositories).forEach {
        it.apply {
            mavenCentral()
            gradlePluginPortal()
            google {
                content {
                    includeGroupByRegex(".*google.*")
                    includeGroupByRegex(".*android.*")
                }
            }
            maven(url = "https://androidx.dev/storage/compose-compiler/repository")
            maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")

        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}


dependencyResolutionManagement {
    //FAIL_ON_PROJECT_REPOS
//    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        mavenCentral()
        google {
            content {
                includeGroupByRegex(".*google.*")
                includeGroupByRegex(".*android.*")
            }
        }
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven { setUrl("https://dl.bintray.com/kotlin/kotlin-dev") }
        maven { setUrl("https://dl.bintray.com/kotlin/kotlin-eap") }
        // workaround for https://youtrack.jetbrains.com/issue/KT-51379
        exclusiveContent {
            forRepository {
                ivy("https://download-cdn.jetbrains.com/kotlin/native/builds") {
                    name = "Kotlin Native"
                    patternLayout {
                        // example download URLs:
                        // https://download.jetbrains.com/kotlin/native/builds/releases/1.7.20/linux-x86_64/kotlin-native-prebuilt-linux-x86_64-1.7.20.tar.gz
                        // https://download.jetbrains.com/kotlin/native/builds/releases/1.7.20/windows-x86_64/kotlin-native-prebuilt-windows-x86_64-1.7.20.zip
                        // https://download.jetbrains.com/kotlin/native/builds/releases/1.7.20/macos-x86_64/kotlin-native-prebuilt-macos-x86_64-1.7.20.tar.gz
                        listOf(
                            "macos-x86_64",
                            "macos-aarch64",
                            "osx-x86_64",
                            "osx-aarch64",
                            "linux-aarch64",
                            "linux-x86_64",
                            "windows-x86_64",
                        ).forEach { os ->
                            listOf("dev", "releases").forEach { stage ->
                                artifact("$stage/[revision]/$os/[artifact]-$os-[revision].[ext]")
                            }
                        }
                    }
                    metadataSources { artifact() }
                }
            }
            filter { includeModuleByRegex(".*", ".*kotlin-native-prebuilt.*") }
        }
        ivy {
            name = "Node.js"
            setUrl("https://nodejs.org/dist")
            patternLayout {
                artifact("v[revision]/[artifact](-v[revision]-[classifier]).[ext]")
            }
            metadataSources {
                artifact()
            }
            content {
                includeModule("org.nodejs", "node")
            }
            isAllowInsecureProtocol = false
        }
        maven {
            setUrl("https://jitpack.io")
            content {
                includeGroupByRegex("com.github.*")
            }
        }

        val properties = Properties().apply {
            runCatching { rootProject.projectDir.resolve("local.properties") }
                .getOrNull()
                .takeIf { it?.exists() ?: false }
                ?.reader()
                ?.use(::load)
        }
        val environment: Map<String, String?> = System.getenv()
        extra["githubToken"] = properties["github.token"] as? String
            ?: environment["GITHUB_TOKEN"] ?: ""

        maven {
            url = uri("https://maven.pkg.github.com/vickyleu/${rootProject.name}")
            credentials {
                username = "vickyleu"
                password = extra["githubToken"]?.toString()
            }
        }

    }
}

include(":sample")
include(":sonner")
