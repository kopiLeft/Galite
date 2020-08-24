plugins {
    kotlin("jvm") apply true
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
}

val vaadinVersion = "16.0.0"

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    api("com.vaadin:vaadin-core") {
        listOf("com.vaadin.webjar", "org.webjars.bowergithub.insites",
            "org.webjars.bowergithub.polymer", "org.webjars.bowergithub.polymerelements",
            "org.webjars.bowergithub.vaadin", "org.webjars.bowergithub.webcomponents")
            .forEach { group -> exclude(group = group) }
    }

    compile("org.jetbrains.exposed", "exposed-core", "0.26.1")
}

dependencyManagement {
    imports {
        mavenBom("com.vaadin:vaadin-bom:${vaadinVersion}")
    }
}
