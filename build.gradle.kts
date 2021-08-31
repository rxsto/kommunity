@file:Suppress("GradlePackageUpdate")

plugins {
    application
    id("com.google.cloud.tools.jib") version "3.1.4"
    kotlin("jvm") version "1.5.30"
    kotlin("kapt") version "1.5.30"
    kotlin("plugin.serialization") version "1.5.30"
}

group = "to.rxs"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://oss.sonatype.org/content/repositories/snapshots") // kord.x
    maven("https://schlaubi.jfrog.io/artifactory/envconf/")
    mavenCentral()
}

dependencies {
    runtimeOnly(kotlin("scripting-jsr223"))

    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8", "1.5.1")
    implementation("org.jetbrains.kotlinx", "kotlinx-serialization-json", "1.2.2")

    implementation("io.github.pdvrieze.xmlutil", "core-jvm", "0.81.2")
    implementation("io.github.pdvrieze.xmlutil", "serialization-jvm", "0.81.2")

    implementation("org.slf4j", "slf4j-api", "2.0.0-alpha1")
    implementation("org.apache.logging.log4j", "log4j-slf4j18-impl", "2.14.1")
    implementation("org.apache.logging.log4j", "log4j-core", "2.14.1")

    implementation("org.jetbrains.exposed", "exposed-core", "0.33.1")
    implementation("org.jetbrains.exposed", "exposed-dao", "0.33.1")
    implementation("org.jetbrains.exposed", "exposed-jdbc", "0.33.1")
    implementation("org.postgresql", "postgresql", "42.2.23")
    implementation("com.zaxxer", "HikariCP", "5.0.0")

    implementation("org.xerial", "sqlite-jdbc", "3.36.0.1")

    implementation(platform("io.ktor:ktor-bom:1.6.3"))
    implementation("io.ktor", "ktor-server-netty")
    implementation("io.ktor", "ktor-serialization")

    implementation("dev.kord", "kord-core", "0.8.0-M5")

    implementation("dev.kord.x", "emoji", "0.5.0-SNAPSHOT")
    implementation("dev.kord.x", "commands-runtime-kord", "0.4.0-SNAPSHOT")
    kapt("dev.kord.x", "commands-processor", "0.4.0-SNAPSHOT")

    implementation("org.jetbrains.kotlinx", "kotlinx-datetime", "0.2.1")
    implementation("dev.schlaubi", "envconf", "1.1")

    implementation(kotlin("reflect"))
}

application {
    mainClass.value("to.rxs.kommunity.LauncherKt")
}

kotlin {
    kotlinDaemonJvmArgs = listOf(
        "-Dfile.encoding=UTF-8",
        "--add-opens=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.jvm=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED"
    )
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(16))
    }
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "16"
            freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
        }
    }

}

kapt {
    includeCompileClasspath = false
}

jib {
    from {
        image = "adoptopenjdk/openjdk16-openj9:debian-jre"
    }

    to {
        image = "gcr.io/rxs-to/" + project.name
        tags = setOf(System.getenv("SEMAPHORE_GIT_SHA"))
    }

    container {
        mainClass = "to.rxs.kommunity.LauncherKt"
        jvmFlags = listOf(
            "-Xmx1G",
            "-Xms500M",
            "-XX:+UseG1GC",
            "-XX:+UseStringDeduplication"
        )
    }
}
