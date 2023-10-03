pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "YoutubeAnalyze"
include(":app")
include(":feature:home")
include(":core:auth")
include(":core:extension")
include(":core:design")
include(":core:data")
