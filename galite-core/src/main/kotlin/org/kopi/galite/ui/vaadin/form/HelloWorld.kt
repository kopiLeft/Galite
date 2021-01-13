package org.kopi.galite.ui.vaadin.form

import com.vaadin.flow.component.Tag
import com.vaadin.flow.component.dependency.JsModule
import com.vaadin.flow.component.dependency.NpmPackage
import com.vaadin.flow.component.polymertemplate.EventHandler
import com.vaadin.flow.component.polymertemplate.PolymerTemplate

@Tag("hello-world")
@NpmPackage(value = "@polymer/paper-input", version = "3.0.2")
@JsModule("./hello-world.js")
class HelloWorld : PolymerTemplate<HelloWorldModel?>() {
  @EventHandler
  private fun sayHello() {
    // Called from the template click handler
    val userInput = model!!.getUserInput()
    if (userInput == null || userInput.isEmpty()) {
      model!!.setGreeting(EMPTY_NAME_GREETING)
    } else {
      model!!.setGreeting(String.format("Hello %s!", userInput))
    }
  }

  companion object {
    private const val EMPTY_NAME_GREETING = "Please enter your name"
  }

  /**
   * Creates the hello world template.
   */
  init {
    setId("template")
    model!!.setGreeting(EMPTY_NAME_GREETING)
  }
}