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

package org.kopi.galite.tests.common

import org.kopi.galite.tests.common.ApplicationTestBase.Companion.asDiv
import org.kopi.galite.tests.database.DBSchemaTest
import org.kopi.galite.visual.ui.vaadin.visual.VWindowController

/**
 * TestBase class for all application tests.
 */
open class ApplicationTestBase: DBSchemaTest() {

  companion object {
    // Use the same window controller for all tests to get all window builders available.
    val windowController = VWindowController()

    /**
     * Creates a Div as a string.
     * @param background The background color to set to the div.
     * @param foreground The foreground color to set to the div.
     */
    fun String?.asDiv(background: Array<Int>? = null, foreground: Array<Int>? = null): String {
      // Start with the basic Div string
      val divBuilder = StringBuilder("""Div[""")

      // Add text if `this` is not null
      if (this != null) {
        divBuilder.append("""text='$this'""")
      }

      // Helper function to convert an array of integers to an RGB string
      fun Array<Int>.toRgbString(): String {
        return "rgb(${this.joinToString(", ")})"
      }

      // Add background color if provided
      if (background != null) {
        if (this != null) divBuilder.append(", ")
        divBuilder.append("""@style='background-color:${background.toRgbString()}""")
        if (foreground != null) divBuilder.append(";")
      }

      // Add foreground color if provided
      if (foreground != null) {
        if (background == null && this != null) divBuilder.append(", ")
        if (background != null) divBuilder.append("color:${foreground.toRgbString()}'")
        else divBuilder.append("""@style='color:${foreground.toRgbString()}'""")
      } else if (background != null) {
        divBuilder.append("'")
      }

      // Close the Div string
      divBuilder.append("]")

      // Return the constructed string
      return divBuilder.toString()
    }
  }
}
