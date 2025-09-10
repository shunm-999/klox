import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
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
        targetExclude("**/TokenType.kt")
        ktlint("1.7.1")
    }
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.compilerOptions {
    freeCompilerArgs.set(listOf("-Xcontext-parameters"))
}

tasks.register<JavaExec>("generateAst") {
    group = "codegen"
    description = "Run GenerateAst to create AST classes"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("org.example.GenerateAstKt")
    args = listOf("${projectDir.parent}/src/main/kotlin")
}