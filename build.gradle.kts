plugins {
    kotlin("jvm") version "2.3.0"
    kotlin("plugin.serialization") version "2.3.0"
    id("com.gradleup.shadow") version "9.3.1"
    id("org.graalvm.buildtools.native") version "0.11.3"
    id("co.uzzu.dotenv.gradle") version "4.0.0"
    application
}

group = "gg.aquatic.foodservice"
version = "26.0.1"

repositories {
    mavenCentral()
}

application {
    mainClass.set("gg.aquatic.foodservice.MainKt")
}

val exposedVersion = "0.61.0"

dependencies {
    // http4k
    implementation(platform("org.http4k:http4k-bom:6.26.0.0"))
    implementation("org.http4k:http4k-core")
    implementation("org.http4k:http4k-server-netty")
    implementation("org.http4k:http4k-serverless-lambda")
    implementation("org.http4k:http4k-format-kotlinx-serialization")
    implementation("org.http4k:http4k-api-openapi")
    implementation("org.http4k:http4k-format-jackson")

    // Database
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")
    implementation("org.postgresql:postgresql:42.7.9")
    implementation("com.zaxxer:HikariCP:7.0.2")

    // Util
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")
    implementation("org.slf4j:slf4j-simple:2.0.17")

    // Testing
    testImplementation(kotlin("test"))
    testImplementation("org.http4k:http4k-testing-kotest")
    testImplementation("com.h2database:h2:2.4.240")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    archiveClassifier.set("lambda")
    mergeServiceFiles()
}

// 1. Task for Standard JAR deployment (API Gateway/ALB)
tasks.register<Zip>("buildLambdaZip") {
    group = "build"
    from(tasks.shadowJar.get().archiveFile)
    archiveFileName.set("deployment.zip")
    destinationDirectory.set(layout.buildDirectory.dir("dist"))
}

// 2. Task for GraalVM Native deployment
// ! Needs to be done thru GitHub Action in order to build a Linux binary !
tasks.register<Zip>("buildNativeLambdaZip") {
    group = "build"
    description = "Packages the GraalVM Native executable for AWS Lambda"
    dependsOn("nativeCompile")

    // Find the native binary and rename it to 'bootstrap'
    from(layout.buildDirectory.file("native/nativeCompile/FoodService")) {
        rename { "bootstrap" }
    }

    archiveFileName.set("native-deployment.zip")
    destinationDirectory.set(layout.buildDirectory.dir("dist"))

    doLast {
        println("Native Lambda ZIP is ready at: ${archiveFile.get().asFile.path}")
    }
}

graalvmNative {
    binaries.all {
        buildArgs.add("--no-fallback")
        buildArgs.add("-H:+InstallExitHandlers")
        buildArgs.add("-H:+UnlockExperimentalVMOptions")

        // 1. Force database and IO drivers to runtime (MANDATORY)
        buildArgs.add("--initialize-at-run-time=org.postgresql.util.SharedTimer")
        buildArgs.add("--initialize-at-run-time=java.sql.DriverManager")
        buildArgs.add("--initialize-at-run-time=org.postgresql.Driver")

        // 2. Allow Kotlin/Stdlib classes to initialize at build time (Solves the DeprecationLevel error)
        buildArgs.add("--initialize-at-build-time=kotlin.DeprecationLevel")
        buildArgs.add("--initialize-at-build-time=kotlin.jvm.internal.Intrinsics")
    }

    // Pulling metadata of configurations from Exposed
    metadataRepository {
        enabled.set(true)
    }
}