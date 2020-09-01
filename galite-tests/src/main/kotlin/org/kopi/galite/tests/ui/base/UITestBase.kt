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

package org.kopi.galite.tests.ui.base

import io.github.sukgu.Shadow

import org.junit.After
import org.junit.Before

import org.kopi.galite.tests.TestBase

import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

/**
 * The high level class for all classes containing UI tests
 */
abstract class UITestBase : TestBase() {
  abstract val driver: WebDriver
  private val url = "http://localhost:8080"

  /**
   * Initializes [driver], opening web page on the address [url]
   */
  @Before
  open fun setupTest() {
    driver.get(url)
  }

  /**
   * Quit [driver], closing all associated windows
   */
  @After
  open fun teardown() {
    driver.quit()
  }

  /**
   * Finds an element that have a specific [tag].
   * Can return at most one element.
   *
   * @param tag web element tag.
   * @return one element with the tag [tag] or null if no element was found.
   */
  fun findElementByTag(tag: String = "*"): WebElement? {
    val shadow = Shadow(driver)

    return shadow.findElement(buildSelector(tag = tag))
  }

  /**
   * Finds all elements that have a specific [tag].
   * Can return one or many elements.
   *
   * @param tag web element tag.
   * @return all elements with the tag [tag] or null if no element was found.
   */
  fun findElementsByTag(tag: String = "*"): WebElement? {
    val shadow = Shadow(driver)

    return shadow.findElement(buildSelector(tag = tag))
  }

  /**
   * Builds the selection query used to select a specific [tag].
   * You can optionally provide one or many criteria to target a specific element
   * withing elements with the same [tag].
   *
   * @param tag the element's tag.
   * @param criteria the criteria of selection.
   */
  private fun buildSelector(tag: String = "*", vararg criteria: Pair<String, String>): String {
    var query = tag
    criteria.forEach {
      query += "[${it.first}='${it.second}']"
    }

    return query
  }
}
