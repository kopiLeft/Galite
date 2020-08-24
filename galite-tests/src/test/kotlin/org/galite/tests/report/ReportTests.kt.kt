package org.galite.tests.report

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import org.galite.base.Application
import org.galite.visual.report.Field
import org.galite.visual.report.Report
import org.vaadin.examples.form.menu.actorBar.menubar.Actor
import org.vaadin.examples.form.menu.menubar.GMenuBar
import java.util.*
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.Test
import java.util.HashMap

class ReportTests : Application(){
    var actors = listOf(Actor("Project","Project","Quit","", Icon(VaadinIcon.USER)), Actor("Project","Project","Hilati","", Icon(VaadinIcon.USER)))
    var rapport = Report(listOf(Field("firstName","","First","",""),Field("lastName","","Last","","")), "", actors,this)

    @Test
    fun enableReordering() {
        rapport.enableReordering()
        var grid = rapport.report
        assertTrue(grid.isColumnReorderingAllowed)
        grid.setColumnOrder(rapport.report.columns.asReversed())
        assertEquals(grid.columns.first(), grid.sortOrder.first().sorted)
    }

    @Test
    fun removeColumn() {
        var size = rapport.report.columns.size
        rapport.removeColumn(rapport.report.columns.first().key)
        assertEquals(size - 1, rapport.report.columns.size)
    }

    @Test
    fun addColumn() {
        var rapport = Report(listOf(Field("lastName","","Last","","")), "", actors,this)
        var grid = rapport.report
        var size = grid.columns.size
        rapport.addColumns(listOf(Field("firstName","","First","","")))
        assertEquals(size+1,grid.columns.size)
        var testkey = grid.columns.map { column -> column.key }.contains("firstName")
        assertTrue(testkey)
    }

    @Test
    fun addLigne() {
        var size = rapport.rows.size
        val ligne: MutableMap<String, String> = HashMap()
        ligne["FIRST"] = "wael"
        ligne["LAST"] = "hilati"
        rapport.addLigne(ligne)
        assertEquals(size+1, rapport.rows.size)
    }

    @Test
    fun dragable() {
        rapport.dragable()
        assertTrue(rapport.report.isRowsDraggable)
    }

    @Test
    fun selectedRow() {
        val ligne: MutableMap<String, String> = HashMap()
        ligne["FIRST"] = "wael"
        ligne["LAST"] = "hilati"
        rapport.addLigne(ligne)
        rapport.report.select(ligne)
        assertEquals(ligne,rapport.report.asSingleSelect().value)
    }

    override var menus: GMenuBar
        get() = TODO("Not yet implemented")
        set(value) {}
    override var windows: LinkedList<Component>?
        get() = TODO("Not yet implemented")
        set(value) {}
}