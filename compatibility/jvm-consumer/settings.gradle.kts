pluginManagement {
    val consumerKotlinVersion = providers.gradleProperty("consumer.kotlin.version").getOrElse("2.3.0")

    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "org.jetbrains.kotlin.jvm") {
                useVersion(consumerKotlinVersion)
            }
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        mavenCentral()
    }
}

rootProject.name = "fiktion-compatibility-jvm-consumer"
