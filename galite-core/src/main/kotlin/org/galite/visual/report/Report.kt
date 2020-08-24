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

package org.galite.visual.report

import com.vaadin.flow.component.AbstractField
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.GridSortOrder
import com.vaadin.flow.component.grid.GridVariant
import com.vaadin.flow.component.grid.dnd.GridDropLocation
import com.vaadin.flow.component.grid.dnd.GridDropMode
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H4
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.listbox.MultiSelectListBox
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.provider.ListDataProvider
import com.vaadin.flow.data.provider.SortDirection
import com.vaadin.flow.data.value.ValueChangeMode
import org.apache.commons.lang3.StringUtils
import org.galite.base.Application
import org.galite.visual.Window
import org.galite.visual.chart.*
import org.vaadin.examples.form.menu.actorBar.menubar.Actor
import java.util.HashMap
import java.util.stream.Collectors

@CssImport("./src/org/galite/styles/ReportStyle.css", themeFor = "vaadin-grid*")
class Report(Fields: List<Field>, title :String, actors : List<Actor>, application : Application): Window(title,actors,application) {
    var report = Grid<MutableMap<String, String>>()
    var rows: MutableList<MutableMap<String, String>> = ArrayList()

    init {
        addColumns(Fields)
        report.setHeightByRows(true);
        report.themeName = "grid-theme"
        sortingColumn(report.columns.first())
        report.setItems(rows)
        add(report)
    }

    // Add columns in the report based on the given fields
    fun addColumns(Fields: List<Field>){
        for (field in Fields){
            var column = report.addColumn { hashmap -> hashmap.get(field.name) }
                                .setHeader(field.label)
                                .setKey(field.name)
                                .setSortable(true)
            if(!field.footer.isEmpty())
                column.setFooter(field.footer)
        }
    }

    // Sort based on the given column
    fun sortingColumn(column : Grid.Column<MutableMap<String, String>>){
        val sort: ArrayList<GridSortOrder<MutableMap<String, String>>> = ArrayList<GridSortOrder<MutableMap<String, String>>>()
        val order: GridSortOrder<MutableMap<String, String>> = GridSortOrder<MutableMap<String, String>>(column, SortDirection.ASCENDING)
        sort.add(order)
        report.sort(sort)
    }

    // Enable the reordering of columns
    fun enableReordering(){
        val columnOrder = Span()

        report.isColumnReorderingAllowed = true
        report.addColumnReorderListener { event ->
            columnOrder.text = event.columns.stream()
                    .map(Grid.Column<MutableMap<String, String>>::getKey).collect(Collectors.joining(", "))
            sortingColumn(event.columns.first())
        }
    }

    //Return the selected row
    fun selectedRow() : MutableMap<String, String>? {
        var selectedRow : MutableMap<String, String>? = null
        report.asSingleSelect().addValueChangeListener { event ->
            val message = String.format("Selection changed from %s to %s",
                    event.oldValue, event.value)
            selectedRow = event.value
        }
        return selectedRow
    }

    // Remove the column with the given key
    fun removeColumn(key : String){
        report.removeColumnByKey(key)
    }

    // Add filtre on the top of each column
    fun addFiltre(){
        val filterRow = report.appendHeaderRow()
        var dataProvider = report.dataProvider as ListDataProvider

        for (column in report.columns) {
            val InputFilter = TextField()
            InputFilter.addValueChangeListener { event ->
                dataProvider.addFilter { HashMap ->
                    StringUtils.containsIgnoreCase(HashMap.get(column.key),
                            InputFilter.value)
                }
            }

            InputFilter.valueChangeMode = ValueChangeMode.EAGER

            filterRow.getCell(column).setComponent(InputFilter)
            InputFilter.setSizeFull()
            InputFilter.placeholder = "Filter"
        }
    }

    // Enable the drag and drop of element in the report
    fun dragable() {
        report.setRowsDraggable(true);

        var draggedItem: MutableMap<String, String>? = null
        report.addDragStartListener { event ->
            draggedItem = event.draggedItems[0]
            report.setDropMode(GridDropMode.BETWEEN)
        }

        report.addDragEndListener { event ->
            draggedItem = null
            report.setDropMode(null)
        }

        report.addDropListener { event ->
            val dropOverItem = event.dropTargetItem.get()
            if (!dropOverItem!!.equals(draggedItem)) {
                rows.remove(draggedItem!!)
                val dropIndex: Int = (rows.indexOf(dropOverItem)
                        + if (event.dropLocation === GridDropLocation.BELOW) 1 else 0)
                if (draggedItem != null) rows.add(dropIndex, draggedItem as MutableMap<String, String>)
                report.dataProvider.refreshAll()
            }
        }
    }

    // Add the given line in the current report
    fun addLigne(Ligne : MutableMap<String, String>){
        rows.add(Ligne)
    }

    // Add charts to Dynamic report
    fun addCharts(application: Application){
        var data =""
        var horizontalLayout = HorizontalLayout()
        val div = Div()
        div.add(H4("Type of chart"))
        val type = MultiSelectListBox<String>()
        type.setItems("Pie chart", "Line chart")
        div.add(type)
        horizontalLayout.add(div)
        val div2 = Div()
        div2.add(H4("Columns"))
        val columns = MultiSelectListBox<String>()
        columns.setItems(report.columns.map { column -> column.key })
        div2.add(columns)
        horizontalLayout.add(div2)
        add(horizontalLayout)
        var actors = listOf(Actor("Project","Project","Quit","", Icon(VaadinIcon.USER)), Actor("Project","Project","Hilati","", Icon(VaadinIcon.USER)))
        var lineChart = LineChart("", actors, application)
        var piechart = AreaChart("", actors, application)
        var chartlayout = HorizontalLayout()
        add(chartlayout)
        type.addValueChangeListener {
            event: AbstractField.ComponentValueChangeEvent<MultiSelectListBox<String>, MutableSet<String>>? ->
            if (event!!.value.contains("Pie chart") && !chartlayout.children.toArray().contains(piechart)) {
                piechart = AreaChart("[[\"null\", \"hello\",\"wael\",\"c\"], [1,33,332, 5555], [2,34,43, 3]]", actors, application)
                chartlayout.add(piechart)
                print(event!!.value)

            }
            else if (!event!!.value.contains("Pie chart"))
                chartlayout.remove(piechart)
            if (event!!.value.contains("Line chart") && !chartlayout.children.toArray().contains(lineChart)) {
                lineChart = LineChart("[[\"null\", \"hello\",\"wael\",\"c\"], [1,33,332, 5555], [2,34,43, 3]]", actors, application)
                chartlayout.add(lineChart)
            }
            else if (!event!!.value.contains("Line chart"))
                chartlayout.remove(lineChart)
        }

        columns.addValueChangeListener{ event ->
            for(column in event.value){
                //data = ajoutercolumntodata(data, column)
            }

        }

    }

    // à supprimer en production
    fun remplirTest(){
        val FIRST = "firstName"
        val LAST = "lastName"
        val fakeBean: MutableMap<String, String> = HashMap()
        fakeBean[FIRST] = "Olli"
        fakeBean[LAST] = "Tietäväinen"
        val fakeBean1: MutableMap<String, String> = HashMap()
        fakeBean1[FIRST] = "Ali"
        fakeBean1[LAST] = "Ben saleh"
        val fakeBean2: MutableMap<String, String> = HashMap()
        fakeBean2[FIRST] = "Wael"
        fakeBean2[LAST] = "Hilati"
        val fakeBean3: MutableMap<String, String> = HashMap()
        fakeBean3[FIRST] = "Koppi"
        fakeBean3[LAST] = "left"

        addLigne(fakeBean)
        addLigne(fakeBean1)
        addLigne(fakeBean2)
        addLigne(fakeBean3)
    }


    private var table: Table? = null
}

fun HasComponents.report(Fields: List<Field>, title :String, actors : List<Actor>, application : Application, function: Report.() -> Unit): Report {
    val report = Report(Fields, title, actors, application)
    report.function()
    return report
}