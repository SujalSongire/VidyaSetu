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
        maven { url = uri("https://chaquo.com/maven")}
    }
}
//dependencyResolutionManagement {
//    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS) // This setting enforces centralized repository management
//    repositories {
//        google()  // Move Google repository here
//        mavenCentral()
//    }
//}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven { url = uri("https://chaquo.com/maven") }// Ensure this is present
    }
}




rootProject.name = "VidyaSetu"
include(":app")
