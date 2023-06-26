/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
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
import org.kopi.galite.gradle.excludeWebJars

plugins {
  kotlin("jvm") apply true
  id("io.spring.dependency-management") version "1.0.10.RELEASE"
}

dependencies {
  api(project(":galite-data"))
  api(project(":galite-util"))

  // Exposed dependencies
  api("org.jetbrains.exposed", "exposed-core", Versions.EXPOSED)
  api("org.jetbrains.exposed", "exposed-jodatime", Versions.EXPOSED)
  api("org.jetbrains.exposed", "exposed-java-time", Versions.EXPOSED)
  api("org.jetbrains.exposed", "exposed-jdbc", Versions.EXPOSED)

  // Vaadin dependencies
  implementation("com.vaadin", "vaadin-core") {
    excludeWebJars()
    exclude("org.slf4j", "slf4j-ext")
  }
  // Vaadin addons
  // Wysiwyg-e Rich Text Editor component for Java
  implementation("org.vaadin.pekka", "wysiwyg_e-java", Versions.WYSIWYG_EJAVA)
  // EnhancedDialog
  implementation("com.vaadin.componentfactory", "enhanced-dialog", Versions.ENHANCED_DIALOG)
  // Apex charts
  implementation("com.github.appreciated", "apexcharts", Versions.APEX_CHARTS)
  // Iron Icons
  implementation("com.flowingcode.addons", "iron-icons", Versions.IRON_ICONS)
  //FullCalendar for Flow dependency
  implementation("org.vaadin.stefan", "fullcalendar2", Versions.FULL_CALENDAR)

  // Itext dependency
  implementation("com.lowagie", "itext", Versions.ITEXT)

  // Jdom dependency
  implementation("org.jdom", "jdom2", Versions.JDOM)

  // Apache POI dependencies
  implementation("org.apache.poi", "poi", Versions.APACHE_POI)
  implementation("org.apache.poi", "poi-ooxml", Versions.APACHE_POI)

  // Graphbuilder dependency
  implementation("com.github.virtuald", "curvesapi", Versions.GRAPH_BUILDER)

  // Hylafax dependencies
  implementation("net.sf.gnu-hylafax", "gnu-hylafax-core", Versions.HYLAFAX) {
    exclude("log4j")
  }

  //JFreeChart dependency
  implementation("org.jfree", "jfreechart", Versions.JFREE_CHART)

  //getOpt dependency
  implementation("gnu.getopt", "java-getopt", Versions.GETOPT)

  // Compile only dependency for Vaadin servlet
  compileOnly("javax.servlet", "javax.servlet-api", Versions.JAVAX_SERVLET_API)

  // Dataframe used in Pivot Table
  implementation("org.jetbrains.kotlinx", "dataframe", Versions.KOTLINX_DATAFRAME)

  // Pivot Table
  implementation("org.vaadin.addons.componentfactory", "pivottable-flow", Versions.PIVOT_TABLE)
}

dependencyManagement {
  imports {
    mavenBom("com.vaadin:vaadin-bom:${Versions.VAADIN}")
  }
}
