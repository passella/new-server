plugins {
    kotlin("jvm") version "1.9.21"
    application
}

group = "br.com.passella"
version = "1.0-SNAPSHOT"

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

tasks.jar {
    manifest {
        attributes["Main-Class"] = "br.com.passella.payments.ApplicationKt"
    }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
