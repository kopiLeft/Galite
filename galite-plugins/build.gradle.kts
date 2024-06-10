/*
 * Copyright (c) 2013-2024 kopiLeft Services SARL, Tunis TN
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

plugins {
    `kotlin-dsl`
    id("java-gradle-plugin")
    id("maven-publish")
}

version = "1.1-026H-B"

repositories {
    mavenCentral()
    mavenLocal()  // If you are using local Maven repository
}

dependencies {
    //getOpt dependency
    implementation("gnu.getopt", "java-getopt", "1.0.13")
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable:1.5.21")
    implementation("org.jetbrains.kotlin:kotlin-sam-with-receiver:1.5.21")

}

gradlePlugin {
    plugins {
        register("customPlugin") {
            id = "org.kopi.factoryGenerator"
            implementationClass = "org.kopi.galite.plugin.FactoryGeneratorPlugin"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("pluginGalite") {
            from(components["java"])
            groupId = "org.kopi"
            artifactId = "factoryGenerator"
            version = "1.1-026H-B"
        }
    }
    repositories {
        val publishDir = System.getenv("PUBLISH_DIR")

        if(publishDir != null) {
            mavenLocal {
                name = "KopiMaven"
                url = uri(publishDir)
                isAllowInsecureProtocol = true
            }
        }
    }
}