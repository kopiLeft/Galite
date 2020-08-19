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

package org.kopi.galite

import io.github.bonigarcia.wdm.WebDriverManager
import org.junit.After
import org.junit.Before
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.pagefactory.ByChained

/**
 * Base class for Tests on chrome.
 *
 */
abstract class TestBase {
  var driver: WebDriver? = null

  @Before
  open fun setupTest() {
    WebDriverManager.chromedriver().setup()
    driver = ChromeDriver()
    driver!!.get("http://localhost:8080")
  }

  @After
  open fun teardown() {
    driver?.quit()
  }

  fun findBy(by: By): List<WebElement?>? {
    return driver!!.findElements(by)
  }

  fun buildSearchQuery(tag: String = "*", vararg criteria: Pair<String, String>): By {
    var query = ".//$tag"
    criteria.forEach {
      query += "[@${it.first}='${it.second}']"
    }

    return By.xpath(query)
  }

  fun buildSelector(tag: String = "*", vararg criteria: Pair<String, String>): String {
    var query = tag
    criteria.forEach {
      query += "[${it.first}='${it.second}']"
    }

    return query
  }

  fun findByMultipleCriteria(vararg bys: By): List<WebElement?>? {
    return driver!!.findElements(ByChained(*bys))
  }

  fun WebElement.findBy(by: By): List<WebElement?>? {
    return driver!!.findElements(by) + this.findShadowElements(by).orEmpty()
  }

  fun WebElement.findShadowElements(by: By): List<WebElement?>? {
    val shadow = (driver as JavascriptExecutor)
        .executeScript("return arguments[0].shadowRoot", this) as WebElement

    return shadow.findElements(by)
  }
}