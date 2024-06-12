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

plugins {
  kotlin("jvm") apply true
}

dependencies {
  //getOpt dependency
  implementation("gnu.getopt", "java-getopt", Versions.GETOPT)

  // Javax dependencies
  implementation("javax.mail", "mail", Versions.JAVAX_MAIL)
  implementation("javax.activation", "activation", Versions.JAVAX_ACTIVATION)
  implementation("org.apache.xmlbeans", "xmlbeans", Versions.XML_BEANS)

  //jdom2
  implementation("org.jdom","jdom2","2.0.6")
}
