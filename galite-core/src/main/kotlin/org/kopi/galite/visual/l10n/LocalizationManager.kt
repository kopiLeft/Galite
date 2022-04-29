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

package org.kopi.galite.visual.l10n

import java.util.Hashtable
import java.util.Locale

import org.jdom2.Document
import org.jdom2.input.SAXBuilder
import org.kopi.galite.util.base.InconsistencyException

/**
 * Implements a localization manager.
 *
 * @param     locale          the locale used for localization management
 * @param     defaultLocale   the default locale used when there is no file in [locale]
 */
class LocalizationManager(val locale: Locale?, private val defaultLocale: Locale?) {

  // ----------------------------------------------------------------------
  // DATA MEMBERS
  // ----------------------------------------------------------------------
  private val documents = Hashtable<String, Document>()

  /**
   * Constructs a form localizer using the specified source.
   *
   * @param     source          the source qualified name
   */
  fun getFormLocalizer(source: String?): FormLocalizer {
    return FormLocalizer(getDocument(source))
  }

  /**
   * Constructs a block localizer using the specified source.
   *
   * @param     source          the source qualified name
   * @param     name            the identifier of the block
   */
  fun getBlockLocalizer(source: String, name: String): BlockLocalizer {
    return BlockLocalizer(this, getDocument(source), name)
  }

  /**
   * Constructs an actor localizer using the specified source.
   *
   * @param     source          the source qualified name
   * @param     name            the identifier of the actor
   */
  fun getActorLocalizer(source: String?, name: String): ActorLocalizer {
    return ActorLocalizer(getDocument(source), name)
  }

  /**
   * Constructs a menu localizer using the specified source.
   *
   * @param     source          the source qualified name
   * @param     name            the identifier of the menu
   */
  fun getMenuLocalizer(source: String?, name: String): MenuLocalizer {
    return MenuLocalizer(getDocument(source), name)
  }

  /**
   * Constructs a list localizer using the specified source.
   *
   * @param     source          the source qualified name
   * @param     name            the identifier of the list
   */
  fun getListLocalizer(source: String, name: String): ListLocalizer {
    return ListLocalizer(this, getDocument(source), name)
  }

  /**
   * Constructs a type localizer using the specified source.
   *
   * @param     source          the source qualified name
   * @param     name            the identifier of the type
   */
  fun getTypeLocalizer(source: String?, name: String?): TypeLocalizer {
    return TypeLocalizer(this, getDocument(source), name)
  }

  /**
   * Constructs a report localizer using the specified source.
   *
   * @param     source          the source qualified name
   */
  fun getReportLocalizer(source: String?): ReportLocalizer {
    return ReportLocalizer(this, getDocument(source))
  }

  /**
   * Constructs a chart localizer using the specified source.
   *
   * @param     source          the source qualified name
   */
  fun getChartLocalizer(source: String?): ChartLocalizer {
    return ChartLocalizer(this, getDocument(source))
  }

  /**
   * Constructs a module localizer using the specified source.
   *
   * @param     source          the source qualified name
   * @param     name            the identifier of the module
   */
  fun getModuleLocalizer(source: String, name: String): ModuleLocalizer {
    return ModuleLocalizer(getDocument(source), name)
  }

  /**
   * Constructs a message localizer using the specified source.
   *
   * @param     source          the source qualified name
   * @param     name            the identifier of the message
   */
  fun getMessageLocalizer(source: String, name: String): MessageLocalizer {
    return MessageLocalizer(getDocument(source), name)
  }

  /**
   * Constructs a property localizer using the specified source.
   *
   * @param     source          the source qualified name
   * @param     name            the property key.
   */
  fun getPropertyLocalizer(source: String, name: String): PropertyLocalizer {
    return PropertyLocalizer(getDocument(source), name)
  }

  /**
   * Constructs a property localizer using the specified source.
   *
   * @param     source          the source qualified name
   */
  fun getPropertyLocalizer(source: String): PropertyLocalizer {
    return PropertyLocalizer(getDocument(source))
  }

  /**
   * Returns the document manging the given source.
   *
   * @param     source          the source qualified name
   */
  private fun getDocument(source: String?): Document {
    if (!documents.containsKey(source)) {
      documents[source] = loadDocument(source)
    }
    return documents[source] ?: throw InconsistencyException("Cannot find document for $source")
  }

  /**
   * Loads the XML document with specified qualified name in current locale.
   *
   * @param     source          the qualified name of the document.
   */
  private fun loadDocument(source: String?): Document {
    var fileName: String
    var document: Document
    val builder = SAXBuilder()

    fileName = source!!.replace('.', '/') + "-" + locale.toString() + ".xml"
    try {
      document = builder.build(LocalizationManager::class.java.classLoader.getResourceAsStream(fileName))
    } catch (localeException: Exception) {
      if (defaultLocale == null) {
        throw InconsistencyException("Cannot load file " + fileName + localeException.message)
      }
      System.err.println("Warning: Cannot load file " + fileName + ": " + localeException.message)
      try {
        fileName = source.replace('.', '/') + "-" + defaultLocale.toString() + ".xml"
        document = builder.build(LocalizationManager::class.java.classLoader.getResourceAsStream(fileName))
      } catch (defaultLocaleException: Exception) {
        throw InconsistencyException("Cannot load file " + fileName + ": " + defaultLocaleException.message)
      }
    }
    // the URI is used to report the file name when a child lookup fails
    document.baseURI = fileName
    return document
  }
}
