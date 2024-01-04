plugins {
    alias(libs.plugins.kotlin.jvm)
    id("maven-publish")
}

group = "dev.stashy.hoplite.swarm"
version = "1.0.1"

dependencies {
    implementation(libs.hoplite.core)

    testImplementation(kotlin("test"))
    testImplementation(libs.jimfs)
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

val githubUser: String? by project
val githubToken: String? by project

val repoUser: String? by project
val repoToken: String? by project

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }

    repositories {
        maven("https://maven.pkg.github.com/stashymane/hoplite-swarm") {
            credentials {
                username = githubUser ?: ""
                password = githubToken ?: ""
            }
        }
        maven("https://repo.stashy.dev/releases") {
            credentials {
                username = repoUser ?: ""
                password = repoToken ?: ""
            }
        }
    }
}

