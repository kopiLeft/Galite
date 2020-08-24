import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.72" apply false
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        jcenter()
        maven { setUrl("https://dl.bintray.com/kotlin/exposed") }
        maven { setUrl("https://philanthropist.touk.pl/nexus/content/repositories/releases") }
        maven { setUrl("https://jitpack.io") }
        maven {
            url = uri("https://maven.vaadin.com/vaadin-addons")
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}
