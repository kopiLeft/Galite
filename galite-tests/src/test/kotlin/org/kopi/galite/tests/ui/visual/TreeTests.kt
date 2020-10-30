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

package org.kopi.galite.tests.ui.visual

import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.kopi.galite.tests.ui.base.UITestBase
import org.kopi.galite.visual.Tree


class TreeTests : UITestBase() {

  @Test
  fun testTree() {
    //TODO
  }

  @Before
  fun createRoutes() {
    setupRoutes()
  }

  companion object {
    @BeforeClass
    @JvmStatic
    fun setupVaadin() {
      discoverRooterClass(TreeTest::class.java)
    }
  }
}

@Route("Tree")
class TreeTest() : VerticalLayout() {

  init {
    val root: TreeNode<Any> =  getRoot()
    val isNoEdit: Boolean = false
    val localised: Boolean  = false
    val tree  : Tree = Tree(root , isNoEdit , localised)

    add(tree)

  }

  fun getRoot() : TreeNode<String> {
    val firstLeaf = TreeNode<String>( "firstLeaf")
    val secondLeaf = TreeNode<String>( "secondLeaf")
    val firstNode = TreeNode<String>( "firstNode")
    firstNode.addChild(firstLeaf)
    firstNode.addChild(secondLeaf)

    val thirdLeaf = TreeNode<String>( "thirdLeaf")
    val fourthLeaf = TreeNode<String>( "fourthLeaf")
    val fifthLeaf = TreeNode<String>( "fifthLeaf")
    firstLeaf.addChild(thirdLeaf)
    firstLeaf.addChild(fourthLeaf)
    firstLeaf.addChild(fifthLeaf)

    val sixthLeaf = TreeNode<String>( "ginger tea")
    val seventhLeaf = TreeNode<String>( "normal tea")
    secondLeaf.addChild(sixthLeaf)
    secondLeaf.addChild(seventhLeaf)

    return firstLeaf
  }
}

class TreeNode<T>(value:T)  {
  var value:T = value
  var parent:TreeNode<T>? = null
  var children:MutableList<TreeNode<T>> = mutableListOf()

  fun addChild(node:TreeNode<T>){
    children.add(node)
    node.parent = this
  }
  override fun toString(): String {
    var s = "${value}"
    if (!children.isEmpty()) {
      s += " {" + children.map { it.toString() } + " }"
    }
    return s
  }
}

