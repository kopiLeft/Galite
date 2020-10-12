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

import java.util.Locale
import javax.swing.tree.TreeNode
import org.kopi.galite.db.DBContext

/**
 * Represents a menu tree model.
 *
 * @param ctxt          the context where to look for application
 * @param isSuperUser   is it a super user ? it sets the accessibility of the module
 * @param menuTreeUser  the user name. represents the user loaded with this menu tree instance.
 * @param groupName     the group name
 * @param loadFavorites should load favorites ?
 */
class VMenuTree(ctxt: DBContext,
                var isSuperUser: Boolean = false,
                val menuTreeUser: String? = null,
                private val groupName: String? = null,
                loadFavorites: Boolean = true) : VWindow(ctxt) {


  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------
  var root: TreeNode? = null
    private set
  private val actors = arrayOfNulls<VActor>(9)
  lateinit var moduleArray: Array<Module> // Sets the accessibility of the module
    private set
  private val items = mutableListOf<Module>()
  private val shortcutsID = mutableListOf<Int>()

  // ----------------------------------------------------------------------
  // IMPLEMENTATIONS
  // ----------------------------------------------------------------------
  fun localizeActors(locale: Locale) {
    TODO()
  }
  override fun setActorEnabled(actor: Int, enabled: Boolean) {
    TODO()
  }
  override fun getActor(number: Int): VActor =
          TODO()

  override fun getUserID(): Int =
          TODO()
  override fun executeVoidTrigger(key: Int) {
    TODO()
  }
  override fun setTitle(s: String) {
    TODO()
  }
  fun getModule(executable: Executable): Module? {
    TODO()
  }
  override fun getType(): Int = Constants.MDL_MENU_TREE
  override fun getDisplay(): UMenuTree = super.getDisplay() as UMenuTree
}
