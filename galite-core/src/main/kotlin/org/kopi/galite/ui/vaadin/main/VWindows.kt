package org.kopi.galite.ui.vaadin.main

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.HasEnabled
import com.vaadin.flow.component.Tag
import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.shared.Registration

/**
 * Widget that contains the opened windows
 * in the current application session.
 * By clicking on the link, a popup will be shown that contains
 * the opened windows and then the user can switch between them.
 */
class VWindows : Div(), HasEnabled {

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------

  /**
   * Sets the link text.
   * @param text The link text.
   */
  override fun setText(text: String) {
    label.text = text
  }

  /**
   * Shows the link localized label.
   */
  fun showLabel() {
    label.isVisible = true
  }

  /**
   * Hides the link localized label.
   */
  fun hideLabel() {
    label.isVisible = false
  }

  /**
   * Sets this windows link to be focused.
   * @param focus The focus state.
   */
  fun setFocused(focus: Boolean) {
    if (focus) {
      windowsLink.element.setAttribute("focus", true)
      inner.element.setAttribute("active", true)
    } else {
      windowsLink.element.setAttribute("focus", false)
      inner.element.setAttribute("active", false)
    }
  }

  /**
   * Registers a click handler to the welcome text.
   * @param handler The handler to be registered.
   * @return The registration .
   */
  fun addClickHandler(handler: (Any) -> Unit): Registration {
    return anchor.addAttachListener(handler)
  }

  /**
   * A simple panel that wraps an <li> element inside.
   */
  @Tag(Tag.LI)
  class VLIPanel : Component(), HasComponents

  /**
   * A simple panel that wraps an <ul> element inside.
   */
  @Tag(Tag.UL)
  class VULPanel : Component(), HasComponents

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  val windowsLink: VULPanel
  val inner: VLIPanel
  val anchor: Anchor
  val label: Span
  val icon: Icon
  private var enabled = false

  override fun setEnabled(enabled: Boolean) {
    this.enabled = enabled
    if (enabled) {
      setClassName("empty", true)
    } else {
      setClassName("empty", false)
    }
  }


  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------

  /**
   * Creates the opened windows handler widget.
   */
  init {
    setId("windows")
    windowsLink = VULPanel()
    inner = VLIPanel()
    anchor = Anchor()
    label = Span()
    icon = Icon()
    windowsLink.add(inner)
    anchor.href = "#"
    windowsLink.setId("windows_link")
    label.setClassName("hide", true)
    inner.add(anchor)
    add(windowsLink)
    anchor.add(label)
    anchor.add(icon)
    icon.element.setAttribute("name", "clone")
    hideLabel()
  }
}
