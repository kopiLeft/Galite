plugins {
    kotlin("jvm") apply true
    id("org.springframework.boot") version "2.2.4.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
}

val vaadinVersion = "16.0.0"

repositories {
    mavenCentral()
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
    testImplementation("test-junit:4.13")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.1")


    // Automated Web Tests
    testImplementation("org.seleniumhq.selenium:selenium-java:3.141.59")
    testImplementation("io.github.bonigarcia:webdrivermanager:4.0.0")
    testImplementation("io.github.sukgu:automation:0.0.13")
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