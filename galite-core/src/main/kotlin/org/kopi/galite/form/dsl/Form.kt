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
package org.kopi.galite.form.dsl

import java.io.File
import java.io.IOException

import org.kopi.galite.common.Actor
import org.kopi.galite.common.LocalizationWriter
import org.kopi.galite.common.Menu
import org.kopi.galite.common.Window
import org.kopi.galite.form.VForm
import org.kopi.galite.visual.VActor

/**
 * Represents a form.
 */
abstract class Form: Window() {

  /** Form's actors. */
  val formActors = mutableListOf<Actor>()

  /** Form's blocks. */
  val formBlocks = mutableListOf<FormBlock>()

  /** Form's pages. */
  val pages = mutableListOf<FormPage>()

  /** Form's menus. */
  val menus = mutableListOf<Menu>()

  /** the help text TODO: Move to super class */
  var help: String? = null

  /**
   * Adds a new actor to this form.
   *
   * An Actor is an item to be linked with a command.
   *
   * @param menu                 the containing menu
   * @param label                the label
   * @param help                 the help
   */
  fun actor(ident: String, menu: Menu, label: String, help: String, init: Actor.() -> Unit): Actor {
    val actor = Actor(ident, menu, label, help)
    actor.init()
    formActors.add(actor)
    return actor
  }

  /**
   * Adds a new block to this form.
   *
   * @param        buffer                 the buffer size of this block
   * @param        visible                the number of visible elements
   * @param        name                   the simple identifier of this block
   * @param        title                  the title of the block
   */
  fun block(buffer: Int, visible: Int, name: String, title: String, init: FormBlock.() -> Unit): FormBlock =
          insertBlock(FormBlock(buffer, visible, name, title), init)

  /**
   * Adds a new block to this form.
   *
   * @param        block                 the block to insert
   */
  fun <T: FormBlock> insertBlock(block: T, init: (T.() -> Unit)? = null): T {
    if (init != null) {
      block.init()
    }
    block.initialize(this)
    formBlocks.add(block)
    return block
  }

  /**
   * Adds a new page to this form. You can use this method to create Pages in your form, this is optional
   * and will create a Tab for each page you create under the form's toolbar.
   *
   * @param        title                the title of the page
   * @return       the form page. You can use it as a parameter to a block it to define that the block
   * will be inserted to this page. You can put as much blocks you want in each page
   */
  fun page(title: String, init: FormPage.() -> Unit): FormPage {
    val page = FormPage("Id\$${pages.size}", title)
    page.init()
    pages.add(page)
    return page
  }

  /**
   * Adds a new menu to this form. Defining a menu means adding an entry to the menu bar in the top of the form
   *
   * @param label                the menu label in default locale
   * @return                     the menu. It is used later to adding actors to this menu by specifying
   * the menu name in the actor definition.
   */
  fun menu(label: String): Menu {
    val menu = Menu(label)
    menus.add(menu)
    return menu
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Get block
   */
  open fun getFormElement(ident: String?): FormElement? {
    formBlocks.forEach { formBlock ->
      if (formBlock.ident == ident || formBlock.shortcut == ident) {
        return formBlock
      }
    }
    return null
  }

  // ----------------------------------------------------------------------
  // XML LOCALIZATION GENERATION
  // ----------------------------------------------------------------------

  fun genLocalization(destination: String? = null) {
    if (locale != null) {
      val baseName = this::class.simpleName
      requireNotNull(baseName)
      val destination = destination
              ?: this.javaClass.classLoader.getResource("")?.path +
              this.javaClass.packageName.replace(".", "/")
      try {
        val writer = FormLocalizationWriter()
        genLocalization(writer)
        writer.write(destination, baseName, locale!!)
      } catch (ioe: IOException) {
        ioe.printStackTrace()
        System.err.println("cannot write : $baseName")
      }
    }
  }

  fun genLocalization(writer: LocalizationWriter) {
    (writer as FormLocalizationWriter).genForm(title,
                                               menus.toTypedArray(),
                                               formActors.toTypedArray(),
                                               pages.toTypedArray(),
                                               formBlocks.toTypedArray()
    )
  }

  /**
   * Returns the qualified source file name where this object is defined.
   */
  protected val sourceFile: String
    get() {
      val basename = this.javaClass.packageName.replace(".", "/") + File.separatorChar
      return basename + this.javaClass.simpleName
    }

  /** Form model */
  override val model: VForm by lazy {
    genLocalization()

    object : VForm() {
      override fun init() {
        initialize()
      }

      init {
      }
    }
  }

  fun VForm.initialize() {
    source = sourceFile
    pages = this@Form.pages.map {
      it.ident
    }.toTypedArray()
    this.actors = formActors.map {
      VActor(it.menu.label, sourceFile, it.ident, sourceFile, it.icon, it.keyCode, it.keyModifier)
    }.toTypedArray()
    blocks = formBlocks.map { formBlock ->
      formBlock.getBlockModel(this, source).also { vBlock ->
        vBlock.setInfo(formBlock.pageNumber)
        vBlock.initIntern()
        formBlock.blockFields.forEach { formField ->
          formField.initialValues.forEach {
            formField.vField.setObject(it.key, it.value) // FIXME temporary workaround
          }
        }
      }
    }.toTypedArray()

    //TODO ----------begin-------------
    this.commands = arrayOf()
    VKT_Triggers = arrayOf(IntArray(200))
    //TODO ----------end-------------
  }
}
