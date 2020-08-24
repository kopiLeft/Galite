/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
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

package org.galite

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import org.galite.base.Application
import org.galite.domain.Domain
import org.galite.visual.Window
import org.galite.visual.chart.*
import org.galite.visual.report.Field
import org.galite.visual.report.Report
import org.galite.visual.report.report
import org.vaadin.examples.form.menu.actorBar.menubar.Actor
import org.vaadin.examples.form.menu.menubar.GMenuBar
import java.util.*


class TestApplication: Application()  {
  override var menus: GMenuBar = GMenuBar("http://google.com", "./../images/logo.png")
  override var windows: LinkedList<Component>? = null

  init {
    add(menus)
    var actors = listOf(Actor("Project","Project","Quit","", Icon(VaadinIcon.USER)), Actor("Project","Project","Hilati","", Icon(VaadinIcon.USER)))
    var arc=  chart(ChartType.BAR, actors, this) {
      val city = dimension(StringTestType(10)) {
        label = "city"
      }

      val measure1 = measure(IntTestType(10)) {
        label = "measure1"
      }

      val measure2 = measure(IntTestType(10)) {
        label = "measure2"
      }

      city.add("Tunis") {
        this[measure1] = 12
        this[measure2] = 20
      }

      city.add("Bizerte") {
        this[measure1] = 45
        this[measure2] = 33
      }

      city.add("Sfax") {
        this[measure1] = 96
        this[measure2] = 45
      }
    }

    var actors2 = listOf(Actor("Project","Project","Quit","", Icon(VaadinIcon.USER)), Actor("Project","Project","Hilati","", Icon(VaadinIcon.USER)))
    val addwind2 = Button("Add wind 2") { event -> addWindow(arc)}
    val but = Button("Disabled on click") { event -> removeWindow(arc)}
    add(but)
    add(addwind2)



    // Dynamic Report Demo
    var fields = listOf(Field("firstName", "", "First", "", ""), Field("lastName", "", "Last", "", ""))
    val rapport = report(fields, "Dynamic report", actors, this){
      val column1 = "firstName"
      val column2 = "lastName"
      val fakeBean: MutableMap<String, String> = HashMap()
      fakeBean[column1] = "Olli"
      fakeBean[column2] = "Tietäväinen"
      enableReordering()
      selectedRow()
      addFiltre()
      addLigne(fakeBean)
      remplirTest()
      addCharts(this@TestApplication)
    }
    val addwind = Button("Add wind 1") { event -> addWindow(rapport)}
    add(addwind)


  }
}

class StringTestType(val param: Int): Domain<String>(25)
class IntTestType(val param: Int): Domain<Int>(25)