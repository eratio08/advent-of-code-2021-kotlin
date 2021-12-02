plugins {
    kotlin("jvm") version "1.6.0"
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of("17"))
    }
}

tasks {
    sourceSets {
        main {
            java.srcDirs("src")
        }
    }

    wrapper {
        gradleVersion = "7.3"
    }
}

fileTree("${project.projectDir}")
    .matching { include("**/*.kt") }
    .asSequence()
    .filter { it.name.contains("Day") }
    .forEach {
        tasks.register<JavaExec>(it.name.substringBefore(".kt")) {
            classpath = sourceSets.main.get().runtimeClasspath
            mainClass.set(it.name.replace(".kt", "Kt"))
        }
    }
