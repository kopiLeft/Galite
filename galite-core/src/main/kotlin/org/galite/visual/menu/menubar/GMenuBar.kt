package org.vaadin.examples.form.menu.menubar

import com.vaadin.flow.component.ClickEvent
import com.vaadin.flow.component.Text
import com.vaadin.flow.component.contextmenu.MenuItem
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Image
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.menubar.MenuBar
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout


@CssImport("./src/org/galite/styles/MenuStyle.css", themeFor = "vaadin-menu-bar*")
class GMenuBar(href: String, logo: String) : VerticalLayout() {
  val menuBar = MenuBar()

  init {
    isPadding = false
    style.set("background-color", "white")
    var menuContainer = HorizontalLayout()
    menuContainer.isPadding = false
    menuContainer.style.set("margin-top", "8")
    var logo = Image(logo, "logo")
    logo.width = "180px"
    logo.height = "75px"
    logo.style.set("padding-right", "20px")
    logo.style.set("padding-bottom", "5px")
    logo.style.set("background-color", "white")
    logo.style.set("cursor", "pointer")
    logo.style.set("border-right", "1px solid #f2f2f2")
    logo.addClickListener { event -> getUI().get().page.open(href, "_blank"); }
    menuContainer.add(logo)
    menuContainer.add(menuBar)
    menuBar.setOpenOnHover(true)
    menuBar.setThemeName("module-bar-theme")
    menuBar.style.set("margin", "0px")
    val project: MenuItem = menuBar.addItem("Project")
    project.setId("wael")
    val account: MenuItem = menuBar.addItem("Account")
    val selected = Text("")
    val message = Div(Text("Selected: "), selected)
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
    // Actor("Project","Project","Wael","","")
    addItem(listOf(Menu("hi", "Project")))
    add(menuContainer)
  }

  fun addItem(menus: List<Menu>) {
    /*if(menuBar.items.map { MenuItem -> MenuItem.text}.contains(actor.menu)){
        var menuindex = menuBar.items.map { MenuItem -> MenuItem.text}.indexOf(actor.menu)
        menuBar.items[menuindex].subMenu.addItem(actor.label)
    }
    else
        menuBar.addItem(actor.label)
}*/
    for (menu in menus) {
      var menu = menuBar.addItem(menu.label)
      menu.addComponentAsFirst(Icon(VaadinIcon.USER))
    }

  }
}