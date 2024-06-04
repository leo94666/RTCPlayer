pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
        maven {
            url = uri("https://jitpack.io")
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
        google()
    }
}

rootProject.name = "RTCPlayer"
include(":app")
include(":RTCPlayer")
