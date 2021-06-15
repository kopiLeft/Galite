/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
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
  kotlin("jvm") apply true
  id("io.spring.dependency-management") version "1.0.10.RELEASE"
}

val exposedVersion = "0.29.1"
val vaadinVersion = "18.0.3"
val itextVersion = "2.1.5"
val jdomVersion = "2.0.5"
val apachePoi = "4.1.2"
val graphbuilder = "1.02"
val hylafaxVersion = "1.0.0"
val jFreeChartVersion = "1.0.19"
val getoptVersion = "1.0.13"

dependencies {
  // Exposed dependencies
  api("org.jetbrains.exposed", "exposed-core", exposedVersion)
  api("org.jetbrains.exposed", "exposed-jodatime", exposedVersion)
  api("org.jetbrains.exposed", "exposed-java-time", exposedVersion)

  // Vaadin dependencies
  implementation("com.vaadin", "vaadin-core") {
    listOf("com.vaadin.webjar", "org.webjars.bowergithub.insites",
           "org.webjars.bowergithub.polymer", "org.webjars.bowergithub.polymerelements",
           "org.webjars.bowergithub.vaadin", "org.webjars.bowergithub.webcomponents")
            .forEach { group -> exclude(group = group) }
  }

  // Itext dependency
  implementation("com.lowagie", "itext", itextVersion)

  // Jdom dependency
  implementation("org.jdom", "jdom2", jdomVersion)

  //Apache POI
  implementation("org.apache.poi", "poi", apachePoi)

  // Apache OOxml
  implementation("org.apache.poi", "poi-ooxml", apachePoi)

  // Graphbuilder dependency
  implementation("com.github.virtuald", "curvesapi", graphbuilder)

  // Hylafax dependencies
  implementation("net.sf.gnu-hylafax", "gnu-hylafax-core", hylafaxVersion)

  //JFreeChart dependency
  implementation("org.jfree", "jfreechart", jFreeChartVersion)

  //getOpt dependency
  implementation("gnu.getopt", "java-getopt", getoptVersion)

  // EnhancedDialog dependency
  implementation("com.vaadin.componentfactory", "enhanced-dialog", "1.0.4")

  // ApexCharts dependency
  implementation("com.github.appreciated", "apexcharts", "2.0.0.beta10")

  // Iron Icons dependency
  implementation("com.flowingcode.addons", "iron-icons", "2.0.1")

  //Wysiwyg-e Rich Text Editor component for Java dependency
  implementation("org.vaadin.pekka", "wysiwyg_e-java", "2.0.1")
}

dependencyManagement {
  imports {
    mavenBom("com.vaadin:vaadin-bom:${vaadinVersion}")
  }
}
