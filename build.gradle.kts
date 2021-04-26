plugins {
    application
    id("com.google.cloud.tools.jib") version "2.8.0"
    kotlin("jvm") version "1.4.32"
    kotlin("kapt") version "1.4.32"
    kotlin("plugin.serialization") version "1.4.32"
}

group = "to.rxs"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots") // kord.x
}

dependencies {
    runtimeOnly(kotlin("scripting-jsr223"))

    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8", "1.4.3")

    implementation("org.jetbrains.kotlinx", "kotlinx-serialization-core", "1.1.0")
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

    implementation("io.ktor", "ktor-server-netty", "1.5.2")
    implementation("io.ktor", "ktor-serialization", "1.5.2")

    implementation("dev.kord", "kord-core", "0.7.0-RC3")

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
            useIR = true
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
