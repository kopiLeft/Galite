package org.kopi.galite.ui.vaadin.actor

import com.flowingcode.vaadin.addons.ironicons.IronIcons
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.KeyModifier
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon

open class VClickableNavigationItem : VNavigationItem() {
  private val DEPENDENT_STYLENAME_DISABLED_ITEM = "disabled"

  override fun setCaption(text: String?) {
    caption.text = text
  }

  override fun setDescription(key: Key?, keyModifier: KeyModifier?) {
    if (key != null && key != Key.UNIDENTIFIED) {
      val modifier = keyModifier?.keys?.get(0)
      acceleratorKey.text = if(modifier != null) modifier + "-" + key.keys[0] else " " + key.keys[0]
    } else {
      acceleratorKey.text = ""
    }
  }

  override fun setIcon(iconName: Any?) {
    if (iconName is VaadinIcon) {
      icon = Icon(iconName)
    } else if (iconName is IronIcons) {
      icon = iconName.create()
    }
  }

  override fun setEnabled(isEnabled: Boolean) {
    className = if(isEnabled) {
      "actor-navigationItem"
    } else {
      "actor-navigationItem-$DEPENDENT_STYLENAME_DISABLED_ITEM"
    }
    super.setEnabled(isEnabled)
  }
}
