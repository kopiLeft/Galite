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
  implementation("org.jetbrains.exposed", "exposed-core", "0.26.1")
  implementation("org.vaadin.haijian:exporter:3.0.0")
  implementation("com.vaadin:vaadin-server:8.11.1")
  implementation("javax.servlet:javax.servlet-api:3.1.0")

  implementation("com.itextpdf:itextpdf:5.5.10")
  implementation("org.apache.pdfbox:pdfbox:2.0.4")
  implementation("org.apache.avalon.framework:avalon-framework-impl:4.3.1")

}

dependencyManagement {
  imports {
    mavenBom("com.vaadin:vaadin-bom:${vaadinVersion}")
  }
}