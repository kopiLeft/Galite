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

import org.kopi.galite.common.LocalizationWriter
import org.kopi.galite.common.Window
import org.kopi.galite.form.VForm
import java.io.File
import java.io.IOException

/**
 * Represents a form.
 */
abstract class Form: Window() {

  /** Form's blocks. */
  val formBlocks = mutableListOf<FormBlock>()

  /** Form's pages. */
  val pages = mutableListOf<FormPage>()

  /** the help text TODO: Move to super class */
  var help: String? = null

  /**
   * Adds a new block to this form.
   *
   * @param        buffer                the buffer size of this block
   * @param        visible                the number of visible elements
   */
  fun block(buffer: Int, visible: Int, title: String, init: FormBlock.() -> Unit): FormBlock {
    val block = FormBlock(buffer, visible, title)
    block.init()
    formBlocks.add(block)
    return block
  }

  /**
   * Adds a new page to this form.
   *
   * @param        tite                the title of the page
   */
  fun page(title: String, init: FormPage.() -> Unit): FormPage {
    val page = FormPage(title, title) // TODO
    page.init()
    pages.add(page)
    return page
  }

  // ----------------------------------------------------------------------
  // ACCESSORS
  // ----------------------------------------------------------------------

  /**
   * Get block
   */
  open fun getFormElement(ident: String?): FormElement? {
    for (j in formBlocks.indices) {
      if (formBlocks[j].ident == ident || formBlocks[j].shortcut == ident) {
        return formBlocks[j]
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
              ?: this.javaClass.classLoader.getResource("")?.path + this.javaClass.packageName.replace(".", "/")
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
            pages.toTypedArray(),
            formBlocks.toTypedArray()
    )
  }

  /**
   * Returns the qualified source file name where this object is defined.
   */
  private val sourceFile: String
    get() {
      val basename = this.javaClass.packageName.replace(".", "/") + File.separatorChar
      return basename + this.javaClass.simpleName
    }


  /** Form model */
  val formModel: VForm by lazy {
    genLocalization()

    object : VForm() {
      override fun init() {
        source = sourceFile
        blocks = formBlocks.map { formBlock ->
          formBlock.getBlockModel(this).also { vBlock ->
            vBlock.setInfo(formBlock.pageNumber)
          }
        }.toTypedArray()
      }

      init {
      }
    }
  }
}
