/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2020 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.visual

import java.util.Hashtable

class Registry(val domain: String, private var parents: Array<Registry>?) {

  var dependencies = Hashtable<String, String>()

  init {
    dependencies.put(VISUAL_KOPI_DOMAIN, "org.kopi.galite.resource" + ".Messages")
  }

  /**
   * Builds the dependencies of this registry.
   */
  fun buildDependencies() {
    buildDependencies(dependencies)
  }

  /**
   * Builds the dependencies of this registry.
   *
   * @param     dependencies            the dependency hashtable.
   */
  private fun buildDependencies(dependencies: Hashtable<String, String>) {
    if (!dependencies.containsKey(domain)) {
      dependencies.put(domain, this.javaClass.getPackage().name + ".Messages")
    }
    if (parents != null) {
      parents!!.forEach {
        it.buildDependencies(dependencies)
      }
    }
  }

  /**
   * Returns the message source for the given key.
   *
   * @param     key             a 3 upper-case letter registry identifier.
   */
  fun getMessageSource(key: String): String? {
    return dependencies[key]
  }

  companion object {
    val VISUAL_KOPI_DOMAIN = "VIS"
  }
}
