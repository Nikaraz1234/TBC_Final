pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("""com\.android.*""")
                includeGroupByRegex("""com\.google.*""")
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
include(":feature:splash")
include(":feature:splash:domain")
include(":feature:splash:presentation")
include(":feature:splash:data")
include(":feature:welcome")
include(":feature:welcome:domain")
include(":feature:welcome:presentation")
include(":feature:welcome:data")
include(":feature:login")
include(":feature:login:domain")
include(":feature:login:presentation")
include(":feature:login:data")
include(":feature:register")
include(":feature:register:domain")
include(":feature:register:presentation")
include(":feature:register:data")
include(":feature:main")
include(":feature:main:domain")
include(":feature:main:presentation")
include(":feature:main:data")
include(":feature:game")
include(":feature:game:domain")
include(":feature:game:presentation")
include(":feature:game:data")
include(":feature:game:archive")
include(":feature:leaderboard")

//Profile
include(":feature:profile")
include(":feature:profile:profile_page")
include(":feature:profile:profile_page:presentation")
include(":feature:profile:edit_profile")
include(":feature:profile:edit_profile:data")
include(":feature:profile:edit_profile:domain")
include(":feature:profile:edit_profile:presentation")
include(":feature:leaderboard:presentation")

include(":feature:notification")
include(":feature:notification:data")
include(":feature:notification:domain")
include(":feature:notification:presentation")
