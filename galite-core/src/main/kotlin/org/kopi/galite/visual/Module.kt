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

import org.kopi.galite.base.Image
import org.kopi.galite.db.DBContext
import org.kopi.galite.l10n.LocalizationManager
import org.kopi.galite.l10n.ModuleLocalizer
import org.kopi.galite.util.base.InconsistencyException

class Module(val id: Int,
             val parent: Int,
             private val shortname: String,
             private val source: String,
             value: String?,
             var access: Int,
             private var priority: Int,
             icon: String?)
  : Comparable<Module> {

  /**
   * return the mnemonic
   */
  fun getSmallIcon(): Image? = smallIcon

  /**
   *
   * @param    context        the context where to look for application
   */
  fun run(context: DBContext) {
    startForm(context, value, description, getSmallIcon())
  }

  /**
   *
   */
  override fun toString(): String = shortname

  // ---------------------------------------------------------------------
  // LOCALIZATION
  // ---------------------------------------------------------------------
  /**
   * Localize this module
   *
   * @param     manager         the manger to use for localization
   */
  fun localize(manager: LocalizationManager) {
    val loc: ModuleLocalizer

    try {
      loc = manager.getModuleLocalizer(source, shortname)
      description = loc.getLabel()
      help = loc.getHelp()
    } catch (e: InconsistencyException) {
      // If the module localization is not found, report it
      ApplicationContext.reportTrouble(shortname,
                                       source,
                                       "Module '$shortname' was not found in '$source'",
                                       e)
      description = "!!! $shortname !!!"
      help = description
    }
  }

  override fun compareTo(module: Module): Int {
    return if (priority == module.priority) {
      description.compareTo(module.description)
    } else {
      priority - module.priority
    }
  }

  /**
   * return the mnemonic
   */
  var icon: Image? = null
    private set

  /**
   * return the name of the object which are linked with the module
   * this object is the name of the class to be executed when this module
   * is called.
   */
  var value : String
    private set

  /**
   * return description of the module
   */
  lateinit var description: String
    private set

  /**
   * return the help stirng
   */
  lateinit var help: String
    private set

  private var smallIcon: Image? = null

  companion object {
    fun getKopiExecutable(value: String): Executable {
      return try {
        Class.forName(value).newInstance() as Executable
      } catch (iae: IllegalAccessException) {
        throw VRuntimeException(iae)
      } catch (ie: InstantiationException) {
        throw VRuntimeException(ie)
      } catch (cnfe: ClassNotFoundException) {
        throw VRuntimeException(cnfe)
      }
    }

    fun startForm(ctxt: DBContext,
                  value: String,
                  description: String,
                  icon: Image? = null): Executable? {
      return try {
        if (ApplicationContext.getDefaults().isDebugModeEnabled()) {
          System.gc()
          Thread.yield()
        }
        val form: Executable = getKopiExecutable(value)

        if (form is VWindow) {
          form.smallIcon = icon
        }
        form.dBContext = ctxt
        form.doNotModal()
        form
      } catch (v: VException) {
        v.printStackTrace()
        throw v
      } catch (t: Throwable) {
        ApplicationContext.reportTrouble("Form loading",
                                         "Module.startForm(DBContext ctxt, String object, String description, ImageIcon icon)",
                                         t.message!!,
                                         t)
        ApplicationContext.displayError(ApplicationContext.getMenu().getDisplay(), MessageCode.getMessage("VIS-00041"))
        null
      }
    }

    // ---------------------------------------------------------------------
    // DATA MEMBERS
    // ---------------------------------------------------------------------
    const val ACS_PARENT = 0
    const val ACS_TRUE = 1
    const val ACS_FALSE = 2
  }

  init {
    this.value = value!!
    //!!! graf 2006.01.30: temporary work-around
    //!!! remove as soon as all modules have been
    //!!! renamed to "com.kopiright." at every
    //!!! customer installation.
    if (this.value.startsWith("at.dms.")) {
      this.value = "com.kopiright." + this.value.substring("at.dms.".length)
    }
    //!!! graf 2006.01.30: end
    if (icon != null) {
      this.icon = ImageHandler.imageHandler!!.getImage(icon)
      smallIcon = ImageHandler.imageHandler!!.getImage(icon)
      if (smallIcon == null) {
        smallIcon = smallIcon!!.getScaledInstance(16, 16, Image.SCALE_SMOOTH)
      }
    }
  }
}
