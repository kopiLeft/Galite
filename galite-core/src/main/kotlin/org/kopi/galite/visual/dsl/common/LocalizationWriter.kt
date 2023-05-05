/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.visual.dsl.common

import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import java.util.Stack

import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.output.Format
import org.jdom2.output.XMLOutputter

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ExpressionWithColumnType

import org.kopi.galite.visual.domain.Domain
import org.kopi.galite.util.base.InconsistencyException

/**
 * Constructs a localization writer unit
 */
open class LocalizationWriter {

  // -------------------------------------------------------------------
  // DATA MEMBERS
  // -------------------------------------------------------------------
  private val currentHierarchy = Stack<Element>()

  /**
   * Writes the XML tree to the specified file.
   *
   * @param     directory       the directory where the localization file should be created
   * @param        baseName        the base name of the file to open
   * @param     locale          the locale
   */
  fun write(directory: String, baseName: String, locale: Locale) {
    val doc = Document(peekNode(null))
    val format = Format.getPrettyFormat()
    format.encoding = "UTF-8"
    format.lineSeparator = "\n"

    val writer = XMLOutputter(format)
    val fileName = "$baseName-$locale.xml"
    val directoryFile = File(directory)

    if (!directoryFile.exists()) {
      directoryFile.mkdirs()
    }
    writer.output(doc, FileOutputStream(File(directory, fileName)))
  }
  // -------------------------------------------------------------------

  /**
   * Creates localization for a menu.
   */
  fun genMenuDefinition(ident: String, label: String) {
    val self = Element("menu")

    self.setAttribute("ident", ident)
    self.setAttribute("label", label)
    peekNode(null).addContent(self)
  }

  /**
   * Creates localization for an actor.
   */
  fun genActorDefinition(ident: String, label: String, help: String?) {
    val self = Element("actor")

    self.setAttribute("ident", ident)
    self.setAttribute("label", label)
    if (help != null) {
      self.setAttribute("help", help)
    }
    peekNode(null).addContent(self)
  }

  fun genTypeDefinition(ident: String, type: Domain<*>) {
    val self = Element("type")
    self.setAttribute("ident", ident)
    pushNode(self)
    type.genTypeLocalization(this)
    popNode(self)
    peekNode(null).addContent(self)
  }

  fun genType(list: FieldList<*>?) {
    list?.genLocalization(this)
  }

  fun <T : Comparable<T>?> genCodeType(codes: List<CodeDescription<T>>) {
    val self = Element("code")
    pushNode(self)
    codes.forEach { code ->
      code.genLocalization(this)
    }
    popNode(self)
    peekNode("type").addContent(self)
  }

  /**
   * Creates the localization for a message.
   */
  fun genMessageDefinition(ident: String, text: String?) {
    val self = Element("message")

    self.setAttribute("ident", ident)
    if (text != null) {
      self.setAttribute("text", text)
    }
    peekNode(null).addContent(self)
  }

  fun genFieldList(columns: MutableList<ListDescription>) {
    val self = Element("list")
    pushNode(self)
    for (i in columns.indices) {
      columns[i].genLocalization(this)
    }
    popNode(self)
    peekNode("type").addContent(self)
  }

  fun genCodeDesc(ident: String, label: String) {
    val self = Element("codedesc")

    self.setAttribute("ident", ident)
    self.setAttribute("label", label)
    peekNode("code").addContent(self)
  }

  fun genListDesc(column: ExpressionWithColumnType<*>, title: String) {
    val self = Element("listdesc")

    self.setAttribute("column", if (column is Column<*>) column.name else title)
    self.setAttribute("title", title)
    peekNode("list").addContent(self)
  }

  // -------------------------------------------------------------------
  // ELEMENT STACK
  // -------------------------------------------------------------------
  protected fun pushNode(node: Element) {
    currentHierarchy.push(node)
  }

  protected fun popNode(expected: Element?) {
    val actual = currentHierarchy.pop() as Element
    if (expected != null && actual != expected) {
      throw InconsistencyException("Unexpected name of element to pop")
    }
  }

  protected fun peekNode(expected: String?): Element {
    val top = currentHierarchy.peek() as Element
    if (expected != null && top.name != expected) {
      throw InconsistencyException("Unexpected name of element to peek")
    }
    return top
  }
}
