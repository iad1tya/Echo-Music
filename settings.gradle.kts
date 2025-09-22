pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { setUrl("https://jitpack.io") }
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
    }
}

// prepare for git submodules
val mediaServiceCore =
    if (File(rootDir, "../MediaServiceCore").exists()) {
        File(rootDir, "../MediaServiceCore")
    } else {
        File(rootDir, "./MediaServiceCore")
    }

val sharedDir =
    if (File(rootDir, "../MediaServiceCore/SharedModules").exists()) {
        File(rootDir, "../MediaServiceCore/SharedModules")
    } else {
        File(rootDir, "./MediaServiceCore/SharedModules")
    }

rootProject.name = "Echo"
include(
    "app",
    ":kotlinYtmusicScraper",
    ":spotify",
    ":aiService",
    // ":ffmpeg-kit", // Temporarily disabled for development
)
// Removed missing module dependencies