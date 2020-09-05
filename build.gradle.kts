plugins {
    java
    kotlin("jvm") version "1.3.72"
    kotlin("kapt") version "1.3.72"
}

group = "to.rxs"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven("https://dl.bintray.com/kordlib/Kord")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    runtimeOnly(kotlin("scripting-jsr223-embeddable"))

    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8", "1.3.8")

    implementation("io.github.cdimascio", "java-dotenv", "5.2.1")

    implementation("org.slf4j", "slf4j-api", "2.0.0-alpha1")
    implementation("org.apache.logging.log4j", "log4j-slf4j18-impl", "2.13.3")
    implementation("org.apache.logging.log4j", "log4j-core", "2.13.3")

    implementation("org.jetbrains.exposed", "exposed-core", "0.24.1")
    implementation("org.jetbrains.exposed", "exposed-dao", "0.24.1")
    implementation("org.jetbrains.exposed", "exposed-jdbc", "0.24.1")
    implementation("org.postgresql", "postgresql", "42.2.14")
    implementation("com.zaxxer", "HikariCP", "3.4.5")

    implementation("org.xerial", "sqlite-jdbc", "3.32.3.1")

    implementation("com.gitlab.kordlib.kord", "kord-core", "0.5.11")
    implementation("com.gitlab.kordlib.kordx", "kordx-commands-runtime-kord", "0.2.0")
    kapt("com.gitlab.kordlib.kordx", "kordx-commands-processor", "0.2.0")

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
