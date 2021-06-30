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
import org.kopi.galite.common.Window
import org.kopi.galite.db.DBContext
import org.kopi.galite.l10n.LocalizationManager
import org.kopi.galite.l10n.ModuleLocalizer
import org.kopi.galite.util.base.InconsistencyException
import kotlin.jvm.Throws

/**
 * Represents an Module.
 *
 * @param id                  the ident of the module
 * @param parent              the ident of the parent of the current module
 * @param shortname           the shortname
 * @param source              the source
 * @param objectName          the name of the object which are linked with the module
 * @param accessibility       the access of the current user to the module
 * @param priority            the priority
 * @param icon                the mnemonic
 */
class Module(val id: Int,
             val parent: Int,
             private val shortname: String,
             private val source: String,
             objectName: String?,
             var accessibility: Int,
             private var priority: Int,
             icon: String?)
  : Comparable<Module> {

  /**
   * return the mnemonic
   */
  var icon: Image? = null
    private set

  /**
   * the name of the object which are linked with the module
   * this object is the name of the class to be executed when this module
   * is called.
   */
  var objectName: String? = objectName
    private set

  /**
   * return description of the module
   */
  var description: String = ""
    private set

  /**
   * return the help
   */
  var help: String? = null
    private set

  /**
   * return the mnemonic
   */
  var smallIcon: Image? = null
    private set

  init {
    if (icon != null) {
      this.icon = ImageHandler.imageHandler.getImage(icon)
      smallIcon = ImageHandler.imageHandler.getImage(icon)
      if (smallIcon == null) {
        smallIcon = smallIcon!!.getScaledInstance(16, 16, Image.SCALE_SMOOTH)
      }
    }
  }

  /**
   *
   * @param    context        the context where to look for application
   */
  fun run(context: DBContext, model: VWindow? = null) {
    startForm(context, objectName, description, smallIcon, model)
  }

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
      description = loc.getLabel()!!
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

  override fun compareTo(other: Module): Int {
    return if (priority == other.priority) {
      description.compareTo(other.description)
    } else {
      priority - other.priority
    }
  }

  companion object {
    fun getExecutable(objectName: String?): Executable {
      return try {
        val instance = Class.forName(objectName).kotlin.objectInstance ?: Class.forName(objectName).newInstance()
        return if(instance is Window) {
          instance.model
        } else {
          instance as Executable
        }
      } catch (iae: IllegalAccessException) {
        throw VRuntimeException(iae)
      } catch (ie: InstantiationException) {
        throw VRuntimeException(ie)
      } catch (cnfe: ClassNotFoundException) {
        throw VRuntimeException(cnfe)
      }
    }

    @JvmOverloads
    @Throws(VException::class)
    fun startForm(ctxt: DBContext,
                  objectName: String?,
                  description: String,
                  icon: Image? = null,
                  model: VWindow? = null): Executable? {
      return try {
        if (ApplicationContext.getDefaults().isDebugModeEnabled) {
          System.gc()
          Thread.yield()
        }

        val form: Executable = getExecutable(objectName)

        if (form is VWindow) {
          form.smallIcon = icon
          model?.ui?.let {
            form.ui = it
          }
        }
        form.dBContext = ctxt
        form.doNotModal()
        form
      } catch (v: VException) {
        v.printStackTrace()
        throw v
      } catch (t: Throwable) {
        throw t // TODO: To be removed
        ApplicationContext.reportTrouble("Form loading",
                                         "Module.startForm(DBContext ctxt, " +
                                                 "String object, String description, ImageIcon icon)",
                                         t.message,
                                         t)
        ApplicationContext.displayError(ApplicationContext.getMenu()!!.getDisplay(),
                                        MessageCode.getMessage("VIS-00041"))
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
}
