plugins {
    application
    id("com.google.cloud.tools.jib") version "2.8.0"
    kotlin("jvm") version "1.5.0"
    kotlin("kapt") version "1.5.0"
    kotlin("plugin.serialization") version "1.5.0"
}

group = "to.rxs"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://oss.sonatype.org/content/repositories/snapshots") // kord.x
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
        name = "ktor-eap"
    }
    maven {
        url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
        name = "ktor-eap"
    }
}

dependencies {
    implementation("io.ktor:ktor-server-content-negotiation:2.0.0-eap-256")
    runtimeOnly(kotlin("scripting-jsr223"))

    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8", "1.5.0-RC")

    implementation("org.jetbrains.kotlinx", "kotlinx-serialization-core", "1.2.0")

    implementation("io.github.pdvrieze.xmlutil", "core-jvm", "0.81.2")
    implementation("io.github.pdvrieze.xmlutil", "serialization-jvm", "0.81.2")

    implementation("org.slf4j", "slf4j-api", "2.0.0-alpha1")
    implementation("org.apache.logging.log4j", "log4j-slf4j18-impl", "2.14.1")
    implementation("org.apache.logging.log4j", "log4j-core", "2.14.1")

    implementation("org.jetbrains.exposed", "exposed-core", "0.30.1")
    implementation("org.jetbrains.exposed", "exposed-dao", "0.30.1")
    implementation("org.jetbrains.exposed", "exposed-jdbc", "0.30.1")
    implementation("org.postgresql", "postgresql", "42.2.19")
    implementation("com.zaxxer", "HikariCP", "4.0.3")

    implementation("org.xerial", "sqlite-jdbc", "3.34.0")

    implementation("io.ktor", "ktor-server-netty", "1.5.3")
    implementation("io.ktor", "ktor-serialization", "1.5.3")

    implementation("dev.kord", "kord-core", "0.8.0-M8")

    implementation("dev.kord.x", "emoji", "0.5.0-SNAPSHOT")

    implementation("dev.kord.x", "commands-runtime-kord", "0.4.0-SNAPSHOT")
    kapt("dev.kord.x", "commands-processor", "0.4.0-SNAPSHOT")

    implementation("org.jetbrains.kotlinx", "kotlinx-datetime", "0.1.1")

    implementation(kotlin("reflect"))

    testCompileOnly("junit", "junit", "4.13.2")
}

application {
    mainClass.value("to.rxs.kommunity.LauncherKt")
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "15"
            freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
        }
    }
}

jib {
    from {
        image = "openjdk:15"
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
