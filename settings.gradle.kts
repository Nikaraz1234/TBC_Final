pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
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
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "MyComposeApp"
include(":app")
include(":core:presentation")
include(":core:data")
include(":core:domain")
include(":core-ui")
include(":feature")
include(":feature:splash")
include(":feature:splash:domain")
include(":feature:splash:presentation")
include(":feature:splash:data")
include(":feature:welcome")
include(":feature:login")
include(":feature:register")
