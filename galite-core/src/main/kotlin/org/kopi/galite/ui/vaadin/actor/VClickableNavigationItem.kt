package org.kopi.galite.ui.vaadin.actor

import com.flowingcode.vaadin.addons.ironicons.IronIcons
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.KeyModifier
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon


open class VClickableNavigationItem : Span() {
  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private val caption = Div()
  private val acceleratorKey = Span()
  private var icon : Component = Icon("")
  //private var enabled = false
  private val DEPENDENT_STYLENAME_DISABLED_ITEM = "disabled"

  init {
    super.isEnabled()
  }
  open fun setCaption(text: String?) {
    caption.text = text
  }

  open fun getCaption(): String? {
    return caption.element.text
  }

  open fun setDescription(key: Key?, keyModifier: KeyModifier?) {
    if (key != null && key != Key.UNIDENTIFIED) {
      val modifier = keyModifier?.keys?.get(0)
      acceleratorKey.text = if(modifier != null) modifier + "-" + key.keys[0] else " " + key.keys[0]
    } else {
      acceleratorKey.text = ""
    }

  }

  open fun setIcon(iconName: Any?) {
    if (iconName is VaadinIcon) {
      icon = Icon(iconName)
    } else if (iconName is IronIcons) {
      icon = iconName.create()
    }
  }

  override fun setEnabled(isEnabled: Boolean) {
    if(isEnabled) {
      className = "actor-navigationItem"
    } else {
      className = "actor-navigationItem-$DEPENDENT_STYLENAME_DISABLED_ITEM"
    }
    super.setEnabled(isEnabled)
  }

  fun createComponent() {
    className = "actor-navigationItem"
    acceleratorKey.className = "acceleratorKey"
    add(icon, caption, acceleratorKey)
  }
}
