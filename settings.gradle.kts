rootProject.name = "valkey-poc"

// Enable Gradle's version catalog support
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

// Configure build cache
buildCache {
    local {
        isEnabled = true
        directory = File(rootDir, "build-cache")
        removeUnusedEntriesAfterDays = 30
    }
}

// Plugin management for consistent version resolution
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

// Dependency resolution management
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
