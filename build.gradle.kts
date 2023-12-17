plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "dev.stashy"
version = "1.0-SNAPSHOT"

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
