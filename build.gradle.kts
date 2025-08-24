plugins {
    kotlin("jvm") version "2.2.0"
    id("com.diffplug.spotless") version "7.2.1"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(23)
}

spotless {
    kotlin {
        target("**/*.kt")
        ktlint("0.48.2")
    }
}