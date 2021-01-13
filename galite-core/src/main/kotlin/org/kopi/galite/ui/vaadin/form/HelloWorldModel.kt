package org.kopi.galite.ui.vaadin.form

import com.vaadin.flow.templatemodel.TemplateModel

interface HelloWorldModel : TemplateModel {
  /**
   * Gets user input from corresponding template page.
   *
   * @return user input string
   */
  fun getUserInput() : String

  /**
   * Sets greeting that is displayed in corresponding template page.
   *
   * @param greeting
   *            greeting string
   */
  fun setGreeting(greeting : String)
}
