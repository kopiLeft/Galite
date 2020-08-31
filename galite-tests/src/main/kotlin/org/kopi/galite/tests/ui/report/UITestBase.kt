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

import org.junit.After
import org.junit.Before

import org.kopi.galite.tests.TestBase

import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor

import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.pagefactory.ByChained

abstract class UITestBase : TestBase() {
  abstract val driver: WebDriver
  private val url = "http://localhost:8080"

  /**
   * Initializes [driver], opening web page on the [url]
   */
  @Before
  open fun setupTest() {
    driver.get(url)
  }

  /**
   * Quit [driver] inorder to close associated windows
   */
  @After
  open fun teardown() {
    driver.quit()
  }

  fun findBy(by: By): List<WebElement?>? {
    return driver.findElements(by)
  }

  fun buildSearchQuery(tag: String= "*", vararg criteria: Pair<String, String>): By {
    var query = ".//$tag"
    criteria.forEach {
      query += "[@${it.first}='${it.second}']"
    }

    return By.xpath(query)
  }

  fun buildSelector(tag: String= "*", vararg criteria: Pair<String, String>): String {
    var query = tag
    criteria.forEach {
      query += "[${it.first}='${it.second}']"
    }

    return query
  }

  fun findByMultipleCriteria(vararg bys: By): List<WebElement?>? {
    return driver.findElements(ByChained(*bys))
  }

  fun WebElement.findBy(by: By): List<WebElement?>? {
    return driver.findElements(by) + this.findShadowElements(by).orEmpty()
  }

  fun WebElement.findShadowElements(by: By): List<WebElement?>? {
    val shadow = (driver as JavascriptExecutor)
            .executeScript("return arguments[0].shadowRoot", this) as WebElement

    return shadow.findElements(by)
  }
}