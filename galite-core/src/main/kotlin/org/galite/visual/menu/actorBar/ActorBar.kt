package org.vaadin.examples.form.menu.actorBar.menubar

import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.menubar.MenuBar
import com.vaadin.flow.component.orderedlayout.VerticalLayout

@CssImport("./src/org/galite/styles/ActorBarStyle.css", themeFor = "vaadin-menu-bar*")
class ActorBar(Actors: List<Actor>) : VerticalLayout() {
  val menuBar = MenuBar()

  init {

    isPadding = false
    style.set("margin", "0")
    style.set("background-color", "#009bd4")
    menuBar.setOpenOnHover(true)
    menuBar.setThemeName("actor-bar-theme")

    /* val project: MenuItem = menuBar.addItem("Project")
     val account: MenuItem = menuBar.addItem("Account")
     menuBar.addItem("Sign Out") { e -> selected.setText("Sign Out") }

     val projectSubMenu = project.subMenu
     val users = projectSubMenu.addItem("Users")
     val billing = projectSubMenu.addItem("Billing")

     val usersSubMenu = users.subMenu
     usersSubMenu.addItem("List") { e: ClickEvent<MenuItem?>? -> selected.setText("List") }
     usersSubMenu.addItem("Add") { e: ClickEvent<MenuItem?>? -> selected.setText("Add") }

     val billingSubMenu = billing.subMenu
     billingSubMenu.addItem("Invoices") { e: ClickEvent<MenuItem?>? -> selected.setText("Invoices") }
     billingSubMenu.addItem("Balance Events"
     ) { e: ClickEvent<MenuItem?>? -> selected.setText("Balance Events") }

     account.subMenu.addItem("Edit Profile"
     ) { e -> selected.setText("Edit Profile") }
     account.subMenu.addItem("Privacy Settings"
     ) { e -> selected.setText("Privacy Settings") }
     // Actor("Project","Project","Wael","","")*/
    addActors(Actors)
    //addActors(listOf(Actor("Project","Project","Quit","",Icon(VaadinIcon.USER)),Actor("Project","Project","Hilati","",Icon(VaadinIcon.USER))))
    add(menuBar)
  }

  fun addActors(actors: List<Actor>) {
    /*if(menuBar.items.map { MenuItem -> MenuItem.text}.contains(actor.menu)){
        var menuindex = menuBar.items.map { MenuItem -> MenuItem.text}.indexOf(actor.menu)
        menuBar.items[menuindex].subMenu.addItem(actor.label)
    }
    else
        menuBar.addItem(actor.label)
}*/
    for (actor in actors) {
      var menu = menuBar.addItem(actor.label)
      menu.addComponentAsFirst(actor.icon)
    }

  }

  fun addActor(actor: Actor) {
    var menu = menuBar.addItem(actor.label)
    menu.addComponentAsFirst(actor.icon)
  }

}