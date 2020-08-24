package org.galite.tests.demo.report

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import org.galite.base.Application
import org.galite.visual.report.Field
import org.galite.visual.report.Report
import org.galite.visual.report.report
import org.vaadin.examples.form.menu.actorBar.menubar.Actor
import org.vaadin.examples.form.menu.menubar.GMenuBar
import java.util.*

class ReportDemo: Application() {
    var actors = listOf(Actor("Project","Project","Quit","", Icon(VaadinIcon.USER)), Actor("Project","Project","Hilati","", Icon(VaadinIcon.USER)))
    var fields = listOf(Field("firstName", "", "First", "", ""), Field("lastName", "", "Last", "", ""))

    val root = report(fields, "Dynamic report", actors, this){
        val column1 = "firstName"
        val column2 = "lastName"
        val fakeBean: MutableMap<String, String> = HashMap()
        fakeBean[column1] = "Olli"
        fakeBean[column2] = "Tietäväinen"
        enableReordering()
        selectedRow()
        addFiltre(report.columns)
        addLigne(fakeBean)
        remplirTest()
        addCharts(this@ReportDemo)
    }


    override var menus: GMenuBar
        get() = TODO("Not yet implemented")
        set(value) {}
    override var windows: LinkedList<Component>?
        get() = TODO("Not yet implemented")
        set(value) {}
}