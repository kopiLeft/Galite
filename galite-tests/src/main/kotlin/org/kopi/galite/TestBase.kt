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
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver

/**
 * Base class for Tests on chrome.
 *
 */
abstract class TestBase {
  var driver: WebDriver? = null

  /**
   * Initialize the web driver
   */
  @Before
  open fun setupTest() {
    WebDriverManager.chromedriver().setup()
    driver = ChromeDriver()
    driver!!.get("http://localhost:8080")
  }

  /**
   * Quit the web driver
   */
  @After
  open fun teardown() {
    driver?.quit()
  }

  /**
   * Select web element with the specific tag and criteria.
   * @param tag the element's tag
   * @param criteria the criteria of the selection
   */
  fun buildSelector(tag: String = "*", vararg criteria: Pair<String, String>): String {
    var query = tag
    criteria.forEach {
      query += "[${it.first}='${it.second}']"
    }

    return query
  }
}