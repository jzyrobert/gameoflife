import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.jvm.tasks.Jar

plugins {
    kotlin("jvm") version "1.3.20"

    application
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    compile(fileTree("lib"))
}

application {
    mainClassName = "GameOfLifeKt"
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "GameOfLifeKt"
    }

    from(configurations.compile.get().map { if (it.isDirectory) it else zipTree(it)})
}

repositories {
    jcenter()
}