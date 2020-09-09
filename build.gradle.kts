plugins {
    java
    application
    kotlin("jvm") version "1.4.0"
    kotlin("kapt") version "1.4.0"
    kotlin("plugin.serialization") version "1.4.0"
}

group = "to.rxs"
version = "1.0-SNAPSHOT"

application {
    mainClassName = "to.rxs.kommunity.LauncherKt"
}

repositories {
    mavenCentral()
    jcenter()
    maven("https://dl.bintray.com/kordlib/Kord")
    maven("https://kotlin.bintray.com/kotlinx/")
}

dependencies {
    runtimeOnly(kotlin("scripting-jsr223"))

    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8", "1.3.8")

    implementation("org.jetbrains.kotlinx", "kotlinx-serialization-core", "1.0.0-RC")
    implementation("net.devrieze", "xmlutil-jvm", "0.80.0-RC")
    implementation("net.devrieze", "xmlutil-serialization-jvm", "0.80.0-RC")

    implementation("org.slf4j", "slf4j-api", "2.0.0-alpha1")
    implementation("org.apache.logging.log4j", "log4j-slf4j18-impl", "2.13.3")
    implementation("org.apache.logging.log4j", "log4j-core", "2.13.3")

    implementation("org.jetbrains.exposed", "exposed-core", "0.24.1")
    implementation("org.jetbrains.exposed", "exposed-dao", "0.24.1")
    implementation("org.jetbrains.exposed", "exposed-jdbc", "0.24.1")
    implementation("org.postgresql", "postgresql", "42.2.14")
    implementation("com.zaxxer", "HikariCP", "3.4.5")

    implementation("org.xerial", "sqlite-jdbc", "3.32.3.1")

    implementation("io.ktor", "ktor-server-netty", "1.4.0")
    implementation("io.ktor", "ktor-serialization", "1.4.0")

    implementation("com.gitlab.kordlib.kord", "kord-core", "0.6.2")
    implementation("com.gitlab.kordlib.kordx", "kordx-commands-runtime-kord", "0.3.1")
    kapt("com.gitlab.kordlib.kordx", "kordx-commands-processor", "0.3.1")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.1.0")

    testCompileOnly("junit", "junit", "4.12")
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "11"
            freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
        }
    }
}
