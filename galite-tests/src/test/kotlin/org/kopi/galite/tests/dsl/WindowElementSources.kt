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
package org.kopi.galite.tests.dsl

import java.io.File

import kotlin.test.assertEquals

import org.junit.Test
import org.kopi.galite.tests.ui.vaadin.VApplicationTestBase
import org.kopi.galite.visual.dsl.common.Actor
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.common.Menu
import org.kopi.galite.visual.dsl.form.Block
import org.kopi.galite.visual.dsl.form.Form
import org.kopi.galite.visual.dsl.form.Key

class WindowElementIdents: VApplicationTestBase() {

  @Test
  fun blockSourceTest() {
    val product = Product().also { it.model }.block
    val productInnerBlock = ProductInnerBlock().also { it.model }.block
    val blockName = Product::class.java.packageName.replace(".", File.separator) + File.separator + "ProductBlock"
    val formName = ProductInnerBlock::class.java.packageName.replace(".", File.separator) + File.separator + "ProductInnerBlock"

    assertEquals(blockName, product.block._source)
    assertEquals(formName, productInnerBlock.block._source)
  }

  @Test
  fun actorSourceTest() {
    val graph = Product().also { it.model }.graph
    val graphInnerBlock = ProductInnerBlock().also { it.model }.graph
    val formName = ProductInnerBlock::class.java.packageName.replace(".", File.separator) + File.separator + "Product"
    val actorName = Product::class.java.packageName.replace(".", File.separator) + File.separator + "Graph"
    val menuName = Product::class.java.packageName.replace(".", File.separator) + File.separator + "Action"

    assertEquals(formName, graph.model!!._actorSource)
    assertEquals(formName, graph.model!!._menuSource)
    assertEquals(actorName, graphInnerBlock.model!!._actorSource)
    assertEquals(menuName, graphInnerBlock.model!!._menuSource)
  }
}

class ProductBlock: Block("Product Block", 1, 1)

class Product: Form(title = "Product") {
  val action = menu("Action")
  val graph = actor(
    menu = action,
    label = "Graph for test",
    help = "show graph values",
  ) {
    key = Key.F9
    icon = Icon.COLUMN_CHART
  }
  val block = insertBlock(ProductBlock())
}

class ProductInnerBlock: Form(title = "Product") {
  val graph = actor(Graph())
  val block = insertBlock(ProductBlock())


  inner class ProductBlock: Block("Product Block", 1, 1)
}

class Action : Menu("Action")

class Graph: Actor(
  menu = Action(),
  label = "Graph for test",
  help = "show graph values",
) {
  init {
    key = Key.F9
    icon = Icon.COLUMN_CHART
  }
}
