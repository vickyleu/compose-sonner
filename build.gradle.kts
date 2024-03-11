plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
}

subprojects {
    afterEvaluate {
        task("testClasses") {
            //https://github.com/robolectric/robolectric/issues/1802#issuecomment-137401530
        }
    }
    configurations.all {
        exclude(group = "org.jetbrains.compose.material", module = "material")
        resolutionStrategy {
            eachDependency {
                if (requested.group == "org.jetbrains.kotlin") {
                    useVersion(libs.versions.kotlin.get())
                }else if (requested.group.startsWith("org.jetbrains.compose.")
                    && !requested.group.endsWith(".compiler")) {
                    useVersion(libs.versions.compose.plugin.get())
                }else if (requested.group == "org.jetbrains" && requested.name == "annotations") {
                    useVersion(libs.versions.annotations.get())
                }
            }
        }
    }
}