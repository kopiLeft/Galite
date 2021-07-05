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

import org.kopi.galite.gradle.Versions

plugins {
  kotlin("jvm") apply true
  id("io.spring.dependency-management") version "1.0.10.RELEASE"
}

dependencies {
  // Exposed dependencies
  api("org.jetbrains.exposed", "exposed-core", Versions.EXPOSED)
  api("org.jetbrains.exposed", "exposed-jodatime", Versions.EXPOSED)
  api("org.jetbrains.exposed", "exposed-java-time", Versions.EXPOSED)

  // Vaadin dependencies
  implementation("com.vaadin", "vaadin-core") {
    listOf("com.vaadin.webjar", "org.webjars.bowergithub.insites",
            "org.webjars.bowergithub.polymer", "org.webjars.bowergithub.polymerelements",
            "org.webjars.bowergithub.vaadin", "org.webjars.bowergithub.webcomponents")
            .forEach { group -> exclude(group = group) }
  }

  // Itext dependency
  implementation("com.lowagie", "itext", Versions.ITEXT)

  // Jdom dependency
  implementation("org.jdom", "jdom2", Versions.JDOM)

  //Apache POI
  implementation("org.apache.poi", "poi", Versions.APACHE_POI)

  // Apache OOxml
  implementation("org.apache.poi", "poi-ooxml", Versions.APACHE_POI)

  // Graphbuilder dependency
  implementation("com.github.virtuald", "curvesapi", Versions.GRAPH_BUILDER)

  // Hylafax dependencies
  implementation("net.sf.gnu-hylafax", "gnu-hylafax-core", Versions.HYLAFAX)

  //JFreeChart dependency
  implementation("org.jfree", "jfreechart", Versions.JFREE_CHART)

  //getOpt dependency
  implementation("gnu.getopt", "java-getopt", Versions.GETOPT)

  // EnhancedDialog dependency
  implementation("com.vaadin.componentfactory", "enhanced-dialog", Versions.ENHANCED_DIALOG)

  // ApexCharts dependency
  implementation("com.github.appreciated", "apexcharts", Versions.APEX_CHARTS)

  // Iron Icons dependency
  implementation("com.flowingcode.addons", "iron-icons", Versions.IRON_ICONS)

  // Compile only dependency for Vaadin servlet
  compileOnly("javax.servlet", "javax.servlet-api", Versions.JAVAX_SERVLET_API)
}

dependencyManagement {
  imports {
    mavenBom("com.vaadin:vaadin-bom:${Versions.VAADIN}")
  }
}
