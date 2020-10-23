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

import java.awt.event.KeyEvent
import java.util.Locale

import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeNode

import kotlin.system.exitProcess

import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.kopi.galite.base.Utils
import org.kopi.galite.db.DBContext
import org.kopi.galite.db.DBSchema.Favorites
import org.kopi.galite.db.DBSchema.GroupParties
import org.kopi.galite.db.DBSchema.GroupRights
import org.kopi.galite.db.DBSchema.Groups
import org.kopi.galite.db.DBSchema.Modules
import org.kopi.galite.db.DBSchema.Symbols
import org.kopi.galite.db.DBSchema.UserRights
import org.kopi.galite.db.DBSchema.Users
import org.kopi.galite.l10n.LocalizationManager
import org.kopi.galite.util.base.InconsistencyException

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

  companion object {

    private val SELECT_MODULES = Modules.slice(Modules.id, Modules.father, Modules.shortName,
            Modules.sourceName, Modules.objectName, Modules.priority, Modules.symbol).selectAll().
            orderBy(Modules.priority to SortOrder.ASC)

    const val CMD_QUIT = 0
    const val CMD_OPEN = 1
    const val CMD_SHOW = 2
    const val CMD_ADD = 3
    const val CMD_REMOVE = 4
    const val CMD_FOLD = 5
    const val CMD_UNFOLD = 6
    const val CMD_INFORMATION = 7
    const val CMD_HELP = 8
    const val MAIN_MENU = -1
    const val USER_MENU = -2
    const val ADMIN_MENU = -3
    const val BOOKMARK_MENU = -4
    private val ROOT_MENUS = arrayOf(
            RootMenu(MAIN_MENU, "forms"),
            RootMenu(USER_MENU, "user"),
            RootMenu(ADMIN_MENU, "admin"))
    private const val MENU_LOCALIZATION_RESOURCE = "resource/org/kopi/galite/Menu"

    init {
      WindowController.windowController.registerWindowBuilder(Constants.MDL_MENU_TREE, object : WindowBuilder {
        override fun createWindow(model: VWindow): UWindow {
          return UIFactory.uiFactory.createView(model) as UMenuTree
        }
      })
    }
  }

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
  // CONSTRUCTORS
  // ----------------------------------------------------------------------
  init {
    createActor(CMD_QUIT, "File", "Close", "quit", 0 /*KeyEvent.VK_ESCAPE*/, 0)
    createActor(CMD_OPEN, "Edit", "Open", "open", KeyEvent.VK_ENTER, 0)
    createActor(CMD_SHOW, "Edit", "Show", null, 0, 0)
    createActor(CMD_ADD, "Edit", "Add", null, 0, 0)
    createActor(CMD_REMOVE, "Edit", "Remove", null, 0, 0)
    createActor(CMD_FOLD, "Edit", "Fold", "fold", KeyEvent.VK_ENTER, 0)
    createActor(CMD_UNFOLD, "Edit", "Unfold", "unfold", KeyEvent.VK_ENTER, 0)
    createActor(CMD_INFORMATION, "Help", "Information", null, 0, 0)
    createActor(CMD_HELP, "Help", "Help", "help", KeyEvent.VK_F1, 0)
    setActors(actors.filterIsInstance<VActor>().toTypedArray())
    localizeActors(ApplicationContext!!.getDefaultLocale())
    createTree(isSuperUser || loadFavorites)
    localizeRootMenus(ApplicationContext!!.getDefaultLocale())
  }

  // ----------------------------------------------------------------------
  // IMPLEMENTATIONS
  // ----------------------------------------------------------------------
  /**
   * Localize this menu tree
   *
   * @param     locale  the locale to use
   */
  fun localizeActors(locale: Locale?) {
    var manager: LocalizationManager?

    manager = LocalizationManager(locale, Locale.getDefault())
    try {
      super.localizeActors(manager) // localizes the actors in VWindow
    } catch (e: InconsistencyException) {
      ApplicationContext.reportTrouble("MenuTree Actor localization",
              "MenuTreeModel.localize",
              e.message,
              e)
      exitProcess(1)
    }
    manager = null
  }

  /**
   * Enables or disable the given actor
   */
  override fun setActorEnabled(actor: Int, enabled: Boolean) {
    actors[actor]!!.handler = this
    actors[actor]!!.isEnabled = enabled
  }

  /**
   * Returns the actor having the given number.
   */
  override fun getActor(number: Int): VActor = actors[number]!!

  /**
   * Returns the ID of the current user
   */
  override fun getUserID(): Int = dBContext!!.defaultConnection.getUserID()

  /**
   * Creates a new actor
   */
  private fun createActor(number: Int,
                          menu: String,
                          item: String,
                          icon: String?,
                          key: Int,
                          modifier: Int) {
    actors[number] = VActor(menu,
            MENU_LOCALIZATION_RESOURCE,
            item,
            MENU_LOCALIZATION_RESOURCE,
            icon,
            key,
            modifier)
    actors[number]!!.number = number
  }

  /**
   * Performs the appropriate action.
   *
   * @param   key           the number of the actor.
   * @return  true if an action was found for the specified number
   */
  override fun executeVoidTrigger(key: Int) {
    val currentDisplay = getDisplay()

    when (key) {
      CMD_QUIT -> currentDisplay.closeWindow()
      CMD_OPEN -> currentDisplay.launchSelectedForm()
      CMD_SHOW -> {
        currentDisplay.getBookmark().show()
        currentDisplay.getBookmark().toFront()
      }
      CMD_ADD -> {
        currentDisplay.addSelectedElement()
        currentDisplay.setMenu()
      }
      CMD_REMOVE -> {
        currentDisplay.removeSelectedElement()
        currentDisplay.setMenu()
      }
      CMD_FOLD -> currentDisplay.getTree().collapseRow(currentDisplay.getTree().selectionRow)
      CMD_UNFOLD -> currentDisplay.getTree().expandRow(currentDisplay.getTree().selectionRow)
      CMD_INFORMATION -> {
        val versionArray = Utils.getVersion()
        var version = ""

        versionArray.forEach {
          version += "\n" + it
        }
        val informationText: String

        informationText = try {
          ApplicationContext.getDefaults().getInformationText()
        } catch (e: PropertyException) {
          e.printStackTrace()
          ""
        }
        getDisplay().showApplicationInformation(informationText + version)
      }
      CMD_HELP -> {
      }
      else -> super.executeVoidTrigger(key)
    }
  }

  /**
   * Localizes the root menus with a given locale
   * @param locale The locale to be used for localization
   */
  protected fun localizeRootMenus(locale: Locale?) {
    var manager: LocalizationManager?

    manager = LocalizationManager(locale, Locale.getDefault())
    for (rootMenu in ROOT_MENUS) {
      rootMenu.localize(manager)
    }
    manager = null
  }

  /**
   * Localize this menu tree
   *
   * @param     locale  the locale to use
   */
  protected fun localizeModules(locale: Locale?) {
    var manager: LocalizationManager?

    manager = LocalizationManager(locale, Locale.getDefault())

    // localizes the modules
    items.forEach {
      it.localize(manager!!)
    }
    manager = null
  }

  /**
   * Builds the module tree.
   */
  private fun createTree(loadFavorites: Boolean) {
    var hasModules: Boolean
    val localModules = loadModules(loadFavorites)

    if (localModules.isEmpty()) {
      hasModules = false
    } else {
      hasModules = false
      for (rootMenu in ROOT_MENUS) {
        rootMenu.createTree(localModules, isSuperUser)
        hasModules = hasModules or !rootMenu.isEmpty()
      }
    }
    if (!hasModules) {
      error(MessageCode.getMessage("VIS-00042"))
      throw InconsistencyException() //never accessed
    }
    createTopLevelTree()
  }

  /**
   * Creates the root tree that contains all root menus.
   * This is used to keep compatibility with swing implementation
   */
  private fun createTopLevelTree() {
    root = DefaultMutableTreeNode(Module(0,
            0,
            VlibProperties.getString("PROGRAM"),
            VlibProperties.getString("program"),
            null,
            Module.ACS_PARENT,
            Int.MAX_VALUE,
            null))
    for (menu in ROOT_MENUS) {
      if (!menu.isEmpty()) {
        (root as DefaultMutableTreeNode).add(menu.root as DefaultMutableTreeNode)
      }
    }
  }

  /**
   * Fetches the modules from the database.
   */
  private fun fetchModules(isUnicode: Boolean): MutableList<Module> {
    val localModules: ArrayList<Module> = ArrayList()
    var icon: String? = null

    transaction {
      SELECT_MODULES.forEach {
        if (it[Modules.symbol] != null && it[Modules.symbol] != 0) {
          val symbol = it[Modules.symbol] as Int

          icon = Symbols.select { Symbols.id eq symbol }.single()[Symbols.objectName]
        }

        val module = Module(it[Modules.id],
                it[Modules.father],
                it[Modules.shortName],
                it[Modules.sourceName],
                it[Modules.objectName],
                Module.ACS_PARENT,
                it[Modules.priority],
                icon)

        localModules.add(module)
        items.add(module)
      }
    }
    return localModules
  }

  private fun fetchGroupRightsByUserId(modules: List<Module>) {
    when {
      groupName != null -> {
        fetchRights(modules, (Modules innerJoin GroupRights innerJoin GroupParties)
                .slice(Modules.id, GroupRights.access, Modules.priority)
                .select {
                  (Modules.id eq GroupRights.module) and
                          (GroupRights.group eq GroupParties.group) and (GroupParties.user
                          inSubQuery (Groups.slice(Groups.id).select { Groups.shortName eq groupName }))
                }
                .orderBy(Modules.priority to SortOrder.ASC, Modules.id to SortOrder.ASC))
      }
      menuTreeUser != null -> {
        fetchRights(modules, (Modules innerJoin GroupRights innerJoin GroupParties)
                .slice(Modules.id, GroupRights.access, Modules.priority)
                .select {
                  (Modules.id eq GroupRights.module) and
                          (GroupRights.group eq GroupParties.group) and (GroupParties.user
                          inSubQuery (Users.slice(Users.id).select { Users.shortName eq menuTreeUser }))
                }
                .orderBy(Modules.priority to SortOrder.ASC, Modules.id to SortOrder.ASC))
      }
      else -> {
        fetchRights(modules, (Modules innerJoin GroupRights innerJoin GroupParties)
                .slice(Modules.id, GroupRights.access, Modules.priority)
                .select {
                  (Modules.id eq GroupRights.module) and
                          (GroupRights.group eq GroupParties.group) and (GroupParties.user eq getUserID())
                }
                .orderBy(Modules.priority to SortOrder.ASC, Modules.id to SortOrder.ASC))
      }
    }
  }

  private fun fetchGroupRightsByGroupId(modules: List<Module>) {
    when {
      groupName != null -> {
        fetchRights(modules, (Modules innerJoin GroupRights)
                .slice(Modules.id, GroupRights.access, Modules.priority)
                .select {
                  (Modules.id eq GroupRights.module) and (GroupRights.group
                          inSubQuery (Groups.slice(Groups.id).select { Groups.shortName eq groupName }))
                }
                .orderBy(Modules.priority to SortOrder.ASC, Modules.id to SortOrder.ASC)
                .withDistinct())
      }
      menuTreeUser != null -> {
        fetchRights(modules, (Modules innerJoin GroupRights)
                .slice(Modules.id, GroupRights.access, Modules.priority)
                .select {
                  (Modules.id eq GroupRights.module) and (GroupRights.group
                          inSubQuery (Users.slice(Users.id).select { Users.shortName eq menuTreeUser }))
                }
                .orderBy(Modules.priority to SortOrder.ASC, Modules.id to SortOrder.ASC)
                .withDistinct())
      }
      else -> {
        fetchRights(modules, (Modules innerJoin GroupRights)
                .slice(Modules.id, GroupRights.access, Modules.priority)
                .select { (Modules.id eq GroupRights.module) and (GroupRights.group eq getUserID()) }
                .orderBy(Modules.priority to SortOrder.ASC, Modules.id to SortOrder.ASC)
                .withDistinct())
      }
    }
  }

  private fun fetchUserRights(modules: List<Module>) {
    when {
      groupName != null -> {
        fetchRights(modules, (Modules innerJoin UserRights)
                .slice(Modules.id, UserRights.access, Modules.priority)
                .select {
                  (Modules.id eq UserRights.module) and (UserRights.user inSubQuery (
                          Groups.slice(Groups.id).select { Groups.shortName eq groupName }
                          ))
                }
                .orderBy(Modules.priority to SortOrder.ASC, Modules.id to SortOrder.ASC))
      }
      menuTreeUser != null -> {
        fetchRights(modules, (Modules innerJoin UserRights)
                .slice(Modules.id, UserRights.access, Modules.priority)
                .select {
                  (Modules.id eq UserRights.module) and (UserRights.user inSubQuery (
                          Users.slice(Users.id).select { Users.shortName eq menuTreeUser }
                          ))
                }
                .orderBy(Modules.priority to SortOrder.ASC, Modules.id to SortOrder.ASC))
      }
      else -> {
        fetchRights(modules, (Modules innerJoin UserRights)
                .slice(Modules.id, UserRights.access, Modules.priority)
                .select { (Modules.id eq UserRights.module) and (UserRights.user eq getUserID()) }
                .orderBy(Modules.priority to SortOrder.ASC, Modules.id to SortOrder.ASC))
      }
    }
  }

  private fun fetchRights(modules: List<Module>, query: Query) {
    transaction {
      query.forEach {
        val module = findModuleById(modules, it[Modules.id])

        module?.accessibility = if (it[UserRights.access]) Module.ACS_TRUE else Module.ACS_FALSE
      }
    }
  }

  private fun findModuleById(modules: List<Module>, id: Int): Module? {
    modules.forEach { module ->
      if (module.id == id) {
        return module
      }
    }
    return null
  }

  /**
   * Fetches the favorites from the database.
   */
  private fun fetchFavorites() {
    transaction {
      val query = if (isSuperUser && menuTreeUser != null) {

        val id = UserRights.select { UserRights.id eq 0 }.single()[UserRights.id]
        Modules.select { Modules.id eq  id }

        Favorites.slice(Favorites.module, Favorites.id)
                .select { Favorites.user inSubQuery(Users.slice(Users.id).select {Users.shortName eq  menuTreeUser  })
                }
                .orderBy(Favorites.id)
      } else {
        Favorites.slice(Favorites.module, Favorites.id).select { Favorites.user eq getUserID() }.orderBy(Favorites.id)
      }
      query.forEach {
        if (it[Favorites.module] != 0) {
          shortcutsID.add(it[Favorites.module])
        }
      }
    }
  }

  /*
   * Loads the accessible modules.
   */
  private fun loadModules(loadFavorites: Boolean): Array<Module> {
    var localModules: MutableList<Module> = ArrayList()

    transaction {
      localModules = fetchModules(ApplicationConfiguration.getConfiguration()!!.isUnicodeDatabase())
      if (groupName != null) {
        fetchGroupRightsByGroupId(localModules)
      } else {
        fetchGroupRightsByUserId(localModules)
        fetchUserRights(localModules)
      }
      if (loadFavorites) {
        fetchFavorites()
      }
    }

    if (!isSuperUser) {
      // walk downwards because we remove elements
      val iterator = localModules.listIterator(localModules.size - 1)

      while (iterator.hasPrevious()) {
        val module = iterator.previous()

        // remove all modules where access is explicitly denied
        if (module.accessibility == Module.ACS_FALSE) {
          iterator.remove()
        }
      }
    }
    // add default modules
    addLogoutModule(localModules)
    // order the menus alphabetically
    localizeModules(ApplicationContext.getDefaultLocale())
    localModules.sort()
    moduleArray = localModules.toTypedArray()
    return moduleArray
  }

  /**
   * Adds the default logout module
   */
  protected fun addLogoutModule(localModules: MutableList<Module>) {
    val logout = Module(Int.MAX_VALUE,
            USER_MENU,
            "logout",
            RootMenu.ROOT_MENU_LOCALIZATION_RESOURCE,
            LogoutModule::class.java.name,
            Module.ACS_TRUE, Int.MIN_VALUE,
            null)
    items.add(logout)
    localModules.add(logout)
  }

  /**
   * Sets the title of the frame
   */
  override fun setTitle(s: String) {
    if (s != null) {
      if (s.contains(VlibProperties.getString("program_menu"))) {
        super.setTitle(s)
      } else {
        super.setTitle(s + " - " + VlibProperties.getString("program_menu"))
      }
    } else {
      super.setTitle(VlibProperties.getString("program_menu"))
    }
  }

  /**
   * Sets the tool tip in the foot panel
   */
  fun setToolTip(s: String) {
    setInformationText(s)
  }

  fun getModules(): MutableList<Module> = items

  fun getModule(executable: Executable): Module? {
    items.forEach { item ->
      if (item.objectName != null && item.objectName == executable.javaClass.name) {
        return item
      }
    }
    return null
  }

  /**
   * Returns the list of available root menus.
   * @return The list of available root menus.
   */
  fun getRoots(): List<RootMenu> = listOf(*ROOT_MENUS)

  fun getShortcutsID(): List<Int> = shortcutsID

  override fun getType(): Int = Constants.MDL_MENU_TREE

  override fun getDisplay(): UMenuTree = super.getDisplay() as UMenuTree
}
