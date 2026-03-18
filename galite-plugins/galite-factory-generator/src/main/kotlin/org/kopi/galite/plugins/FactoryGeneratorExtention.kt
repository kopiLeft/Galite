/*
 * Copyright (c) 2013-2026 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2026 kopiRight Managed Solutions GmbH, Wien AT
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

import java.io.File
import javax.inject.Inject

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory

open class FactoryGeneratorExtention @Inject constructor(objectFactory: ObjectFactory) {
  @Input
  val factories: ListProperty<Factory> = objectFactory.listProperty(Factory::class.java)
}

data class Factory(@Input var classPrefix: String,                      // Prefix of factory class
                   @Input var packageName: String,                      // Package of the generated factory class
                   @Input var xsdFiles: List<String> = emptyList(),     // The xsd files included in the generated factory class
                   @Input var xsdConfigFile: String = "",               // The xsd config file included in the generated factory class
                   @Input var getAbstract: Boolean = true,              // Generate methods for abstract types
                   @Input var keepEmptyStrings: Boolean = true)         // Generate methods without checking for empty strings
{
  @OutputDirectory
  var destinationDirectory = packageName.replace(".", File.separator)   // The destination directory for the generated factory class.
                                                                        // Default : package directory.
}
