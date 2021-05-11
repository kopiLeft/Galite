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
package org.kopi.galite.ui.vaadin.login

import org.kopi.galite.ui.vaadin.base.Styles
import org.kopi.galite.ui.vaadin.base.VHiddenSeparator
import org.kopi.galite.ui.vaadin.base.VInputButton
import org.kopi.galite.ui.vaadin.base.VInputLabel
import org.kopi.galite.ui.vaadin.base.VInputText
import org.kopi.galite.ui.vaadin.common.VSelect
import org.kopi.galite.ui.vaadin.common.VSimpleTable


import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Focusable
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.KeyPressEvent
import com.vaadin.flow.component.ShortcutEvent
import com.vaadin.flow.component.Shortcuts
import com.vaadin.flow.component.Tag
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.data.value.ValueChangeMode
import com.vaadin.flow.dom.DomEvent
import com.vaadin.flow.dom.Element
import com.vaadin.flow.dom.ElementFactory

/**
 * The login box widget.
 */
class VLoginBox : Div() {
  //---------------------------------------------------
// IMPLEMENTATIONS
//---------------------------------------------------
  /**
   * Sets the welcome text.
   * @param text The welcome text.
   */
  fun setWelcomeText(text: String?) {
    table.setWelcomeText(text)
  }

  /**
   * Sets the welcome image.
   * @param uri The welcome image URI.
   */
  fun setWelcomeImage(uri: String?) {
    table.setWelcomeImage(uri)
  }

  /**
   * Sets the error.
   * @param error The error message.
   */
  fun setError(error: String?) {
    table.setError(error)
  }

  /**
   * Removes an error
   */
  fun removeError() {
    table.removeError()
  }

  /**
   * Sets the information text.
   * @param text The information text.
   */
  fun setInformationText(text: String?) {
    table.setInformationText(text)
  }

  /**
   * Sets the user name label.
   * @param label the user name label.
   */
  fun setUsernameLabel(label: String?) {
    table.setUsernameLabel(label)
  }

  /**
   * Returns the user name value.
   * @return the user name value.
   */
  val username: String
    get() = table.username

  /**
   * Sets the password label.
   * @param label The password label.
   */
  fun setPasswordLabel(label: String?) {
    table.setPasswordLabel(label)
  }

  /**
   * Returns the password value.
   * @return The password value.
   */
  val password: String
    get() = table.password

  /**
   * Sets the language label.
   * @param label the language label.
   */
  fun setLanguageLabel(label: String?) {
    table.setLanguageLabel(label)
  }

  /**
   * Appends a supported language.
   * @param language The language display name.
   * @param isocode The iso code of the language and the name to be returned for the selection.
   */
  fun addLanguage(language: String, isocode: String?) {
    table.addLanguage(language, isocode)
  }

  /**
   * The language selected value.
   */
  var selectedLanguage: String
    get() = table.selectedLanguage
    set(language) {
      table.selectedLanguage = language
    }

  /**
   * Sets the login displayed text.
   * @param text The login text.
   */
  fun setLoginText(text: String?) {
    table.setLoginText(text)
  }

  /**
   * Adds a language change handler.
   * @param listener The change handler.
   */
  fun addChangeHandler(listener: (DomEvent?) -> Unit) {
    table.addChangeHandler(listener)
  }

  /**
   * Adds a click handler.
   * @param handler The click handler.
   */
  fun addClickHandler(handler: (Any) -> Unit) {
    table.addClickHandler(handler)
  }

  /**
   * Focus on the first field in the login box.
   */
  fun focus() {
    table.focus()
  }

  //---------------------------------------------------
// INNER CLASSES
//---------------------------------------------------
  /**
   * Special table element for login box.
   */
  @Tag("table")
  private class Table : Component() {
    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    /**
     * Sets the welcome text.
     * @param text The welcome text.
     */
    fun setWelcomeText(text: String?) {
      welcomeText.text = text
    }

    /**
     * Sets the welcome image.
     * @param uri The welcome image URI.
     */
    fun setWelcomeImage(uri: String?) { // welcomeImage.setClassName(Styles.LOGIN_BOX_IMAGE); TODO
      welcomeImage.setAttribute("src", uri)
      // welcomeImage.addClassName(ResourcesUtil.getResourceName(uri)); TODO
    }

    /**
     * Sets the error.
     * @param error The error message.
     */
    fun setError(error: String?) {
      content.setError(error)
    }

    /**
     * Removes an error
     */
    fun removeError() {
      content.removeError()
    }

    /**
     * Sets the information text.
     * @param text The information text.
     */
    fun setInformationText(text: String?) {
      content.setInformationText(text)
    }

    /**
     * Sets the user name label.
     * @param label the user name label.
     */
    fun setUsernameLabel(label: String?) {
      content.setUsernameLabel(label)
    }

    /**
     * Sets the password label.
     * @param label The password label.
     */
    fun setPasswordLabel(label: String?) {
      content.setPasswordLabel(label)
    }

    /**
     * Returns the user name value.
     * @return the user name value.
     */
    val username: String
      get() = content.getUsername()

    /**
     * Returns the password value.
     * @return The password value.
     */
    val password: String
      get() = content.getPassword()

    /**
     * Sets the language label.
     * @param label the language label.
     */
    fun setLanguageLabel(label: String?) {
      content.setLanguageLabel(label)
    }

    /**
     * Appends a supported language.
     * @param language The language display name.
     * @param isocode The iso code of the language and the name to be returned for the selection.
     */
    fun addLanguage(language: String, isocode: String?) {
      content.addLanguage(language, isocode)
    }

    /**
     * The language selected value.
     */
    var selectedLanguage: String
      get() = content.selectedLanguage
      set(language) {
        content.selectedLanguage = language
      }

    /**
     * Sets the login displayed text.
     * @param text The login text.
     */
    fun setLoginText(text: String?) {
      content.setLoginText(text)
    }

    /**
     * Adds a language change handler.
     * @param listener The change handler.
     */
    fun addChangeHandler(listener: (DomEvent?) -> Unit) {
      content.addChangeHandler(listener)
    }

    /**
     * Adds a click handler.
     * @param handler The click handler.
     */
    fun addClickHandler(handler: (Any) -> Unit) {
      content.addClickHandler(handler)
    }

    /**
     * Focus on the first field in the login box.
     */
    fun focus() {
      content.focus();
    }

    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------
    private val welcomeText: Element
    private val welcomeImage: Element
    private val content: LoginPane
    //---------------------------------------
    // CONSTRUCTOR
    //---------------------------------------
    /**
     * Creates the table widget.
     */
    init {
      element.setProperty("align", "center")
      element.setProperty("border", "0")
      element.setProperty("cellPadding", "0")
      element.setProperty("cellSpacing", "0")
      val headerRow = Element("tr")
      val contentRow = Element("tr")
      element.appendChild(headerRow)
      element.appendChild(contentRow)
      val headerColumn = Element("td")
      val contentColumn = Element("td")
      headerRow.appendChild(headerColumn)
      contentRow.appendChild(contentColumn)
      headerColumn.setProperty("align", "left")
      contentColumn.setProperty("align", "center")
      welcomeText = Element("b")
      welcomeImage = Element("img")
      welcomeImage.setAttribute("class", Styles.LOGIN_BOX_IMAGE)
      headerColumn.appendChild(welcomeText)
      headerColumn.appendChild(ElementFactory.createBr())
      headerColumn.appendChild(welcomeImage)
      content = LoginPane()
      contentColumn.appendChild(content.element)
    }
  }

  /**
   * The login panel.
   */
  private class LoginPane : Div(), Focusable<LoginPane> {
    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    fun onEnterEvent(keyDownEvent: ShortcutEvent?) {
      login.click()
    }

    /**
     * Sets the error.
     * @param error The error message.
     */
    fun setError(error: String?) {
      errorIndicator.text = error
    }

    /**
     * Removes an error
     */
    fun removeError() {
      if (errorIndicator.text != null
              && errorIndicator.text.length > 0) {
        errorIndicator.text = ""
      }
    }

    /**
     * Sets the information text.
     * @param text The information text.
     */
    fun setInformationText(text: String?) {
      InformationText.text = text
    }

    /**
     * Sets the user name label.
     * @param label the user name label.
     */
    fun setUsernameLabel(label: String?) {
      usernameLabel.text = label
    }

    /**
     * Returns the user name value.
     * @return the user name value.
     */
    fun getUsername(): String {
      return username.value
    }

    /**
     * Sets the password label.
     * @param label The password label.
     */
    fun setPasswordLabel(label: String?) {
      passwordLabel.text = label
    }

    /**
     * Returns the password value.
     * @return The password value.
     */
    fun getPassword(): String {
      return password.value
    }

    /**
     * Sets the language label.
     * @param label the language label.
     */
    fun setLanguageLabel(label: String?) {
      languageLabel.text = label
    }

    /**
     * Appends a supported language.
     * @param language The language display name.
     * @param isocode The iso code of the language and the name to be returned for the selection.
     */
    fun addLanguage(language: String, isocode: String?) {
      this.language.addItem(language, isocode)
    }

    /**
     * The language selected value.
     */
    var selectedLanguage: String
      get() = language.getSelectedValue()
      set(language) {
        this.language.setSelectedValue(language)
      }

    /**
     * Sets the login displayed text.
     * @param text The login text.
     */
    fun setLoginText(text: String?) {
      login.value = text
    }

    /**
     * Adds a language change handler.
     * @param listener The change handler.
     */
    fun addChangeHandler(listener: (DomEvent?) -> Unit) {
      language.addChangeHandler(listener)
    }

    /**
     * Adds a click handler.
     * @param handler The click handler.
     */
    fun addClickHandler(handler: (Any) -> Unit) {
      login.addClickListener(handler)
    }

    /**
     * Focus on the first field in the login box.
     */
    override fun focus() {
      username.focus()
    }

    //---------------------------------------
    // DATA MEMBERS
    //---------------------------------------
    private val table: VSimpleTable
    private val errorIndicator: Span
    private val InformationText: Span
    private val usernameLabel: VInputLabel
    private val username: VInputText
    private val passwordLabel: VInputLabel
    private val password: PasswordField
    private val languageLabel: VInputLabel
    private val language: VSelect
    private val login: VInputButton

    /**
     * Creates the login panel
     */
    init {
      className = Styles.LOGIN_BOX_PANE
      element.setAttribute("hideFocus", "true")
      element.style["outline"] = "0px"
      table = VSimpleTable()
      errorIndicator = Span()
      errorIndicator.setId("post_error")
      errorIndicator.className = Styles.LOGIN_BOX_PANE_ERROR
      InformationText = Span()
      InformationText.className = Styles.LOGIN_BOX_PANE_INFO
      usernameLabel = VInputLabel()
      usernameLabel.setHtmlFor("user_name")
      username = VInputText()
      username.setId("user_name")
      username.setName("user_name")
      username.setSize(10)
      username.tabIndex = 1
      username.addKeyPressListener { event: KeyPressEvent? -> removeError() }
      passwordLabel = VInputLabel()
      passwordLabel.setHtmlFor("user_password")
      password = PasswordField()
      password.setId("user_password")
      password.element.setAttribute("name", "user_password")
      password.tabIndex = 2
      password.maxLength = 20
      password.addKeyPressListener { event: KeyPressEvent? -> removeError() }
      languageLabel = VInputLabel()
      language = VSelect()
      language.element.setAttribute("name", "login_language")
      language.width = "152px"
      login = VInputButton()
      login.setId("login_button")
      login.className = Styles.INPUT_BUTTON_PRIMARY
      login.setName("Login")
      login.tabIndex = 3
      table.setCellSpacing(2)
      table.setCellPadding(0)
      table.setBorderWidth(0)
      table.element.setProperty("align", "center")
      table.element.setProperty("width", "100%")
      table.addCell(true, errorIndicator)
      table.setTdColSpan(2)
      table.setTdHeight("15px")
      table.addCell(true, InformationText)
      table.setTdColSpan(2)
      table.setTdHeight("15px")
      table.addCell(true, VHiddenSeparator(12))
      table.setTdColSpan(2)
      table.addCell(true, usernameLabel)
      table.setTdHeight("15px")
      table.lastTd?.setAttribute("scope", "row")
      table.addCell(false, username)
      table.setTdHeight("15px")
      table.addCell(true, passwordLabel)
      table.setTdHeight("20%")
      table.addCell(false, password)
      table.setTdHeight("15px")
      table.addCell(true, languageLabel)
      table.setTdWidth("15px")
      table.addCell(false, language)
      table.setTdHeight("15px")
      table.addCell(true, VHiddenSeparator(16))
      table.setTdColSpan(2)
      table.addCell(true, null)
      table.setTdWidth("150px")
      table.addCell(false, login)
      table.setTdHeight("15px")
      table.setTdWidth("250px")
      setFieldsToEagerMode()
      add(table)
      Shortcuts.addShortcutListener(this,
                                    { keyDownEvent: ShortcutEvent? -> onEnterEvent(keyDownEvent) },
                                    Key.ENTER)
              .listenOn(this)
    }

    // Issue: https://github.com/vaadin/flow/issues/5959
    private fun setFieldsToEagerMode() {
      username.valueChangeMode = ValueChangeMode.EAGER
      password.valueChangeMode = ValueChangeMode.EAGER
    }
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private val table: Table
  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  /**
   * Creates the login box widget.
   */
  init {
    className = Styles.LOGIN_BOX
    table = Table()
    add(table)
  }
}
