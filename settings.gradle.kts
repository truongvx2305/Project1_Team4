@file:Suppress("UnstableApiUsage", "UnstableApiUsage", "UnstableApiUsage", "UnstableApiUsage",
    "RedundantSuppression", "RedundantSuppression", "RedundantSuppression"
)

pluginManagement {
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

rootProject.name = "Project1_Team4"
include(":app")
 