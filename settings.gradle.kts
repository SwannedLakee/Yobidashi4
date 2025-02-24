
rootProject.name = "Yobidashi4"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libraries") {
            library("koin", "io.insert-koin", "koin-core").version("4.0.2")
            library("koinAnnotations", "io.insert-koin", "koin-annotations").version("1.4.0")
            library("koinKspCompiler", "io.insert-koin", "koin-ksp-compiler").version("1.4.0")
            library("kotlinSerialization", "org.jetbrains.kotlinx", "kotlinx-serialization-json").version("1.3.3")
            library("coroutines", "org.jetbrains.kotlinx", "kotlinx-coroutines-core-jvm").version("1.6.1")
            library("slf4j", "org.slf4j", "slf4j-api").version("2.0.16")
            library("reload4j", "org.slf4j", "slf4j-reload4j").version("2.0.6")
            library("zxing", "com.google.zxing", "core").version("3.4.1")
            library("jcef", "me.friwi", "jcefmaven").version("132.3.1")
            library("jsoup", "org.jsoup", "jsoup").version("1.18.3")
            version("compose", "1.7.3")
        }
    }
}

include(":domain")
include(":infrastructure")
include(":presentation")

