/*
 * Copyright (c) 2013-2025 kopiLeft Services SARL, Tunis TN
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
}

gradlePlugin {
  plugins {
    create("") {
      id = "org.kopi.common-plugin" // Unique plugin ID
      implementationClass = "org.kopi.galite.plugins.common.GradleExtensionsPlugin" // The main plugin class
    }
  }
}
