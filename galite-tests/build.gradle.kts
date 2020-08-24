import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") apply true
    id("org.springframework.boot") version "2.2.4.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
}

val vaadinVersion = "16.0.0"

group = "com.example"

repositories {
    mavenCentral()
    maven { setUrl("https://dl.bintray.com/kotlin/kotlin-eap") }
}

dependencies {
    implementation(project(":galite-core"))

    implementation("com.vaadin:vaadin-spring-boot-starter") {
        // Webjars are only needed when running in Vaadin 13 compatibility mode
        listOf("com.vaadin.webjar", "org.webjars.bowergithub.insites",
            "org.webjars.bowergithub.polymer", "org.webjars.bowergithub.polymerelements",
            "org.webjars.bowergithub.vaadin", "org.webjars.bowergithub.webcomponents")
            .forEach { group -> exclude(group = group) }
    }

    testImplementation(kotlin("stdlib-jdk8"))
    testImplementation(kotlin("test-junit"))

    // Automated Web Tests
    testImplementation("org.seleniumhq.selenium:selenium-java:3.141.59")
    testImplementation("io.github.sukgu:automation:0.0.13")
    implementation(kotlin("stdlib-jdk8"))
}

tasks {
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

tasks.test {
    useJUnit()

    maxHeapSize = "1G"
}


dependencyManagement {
    imports {
        mavenBom("com.vaadin:vaadin-bom:${vaadinVersion}")
    }
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}