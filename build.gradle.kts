import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    kotlin("jvm") version "1.9.21"
    application
}

group = "br.com.passella"

val buildVersion = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
version = buildVersion

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("br.com.passella.payments.ApplicationKt")
}

tasks.processResources {
    filesMatching("version.properties") {
        expand(mapOf("version" to buildVersion))
    }
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "br.com.passella.payments.ApplicationKt"
        attributes["Implementation-Version"] = buildVersion
    }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
