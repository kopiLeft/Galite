package org.vaadin.examples.form.menu.actorBar.menubar

import com.vaadin.flow.component.icon.Icon

class Actor(Name: String, Menu: String, Label: String, Help: String, ActorIcon: Icon) {
  var name: String = Name
  var menu: String = Menu
  var label: String = Label
  var help: String = Help
  var icon: Icon = ActorIcon
}