/*
 * Copyright (c) 2013-2025 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2025 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.plugins

import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

import org.kopi.galite.plugins.common.GradleExtensionsPlugin

class FactoryGeneratorPlugin : GradleExtensionsPlugin() {
  override fun apply(project: Project) {
    super.apply(project)
    project.extensions.create("factoryGenerator", FactoryGeneratorExtention::class.java)
    project.tasks.apply {
      register<FactoryGeneratorTask>("generateFactory")
    }
  }
}
