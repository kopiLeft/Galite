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

import com.helger.commons.csv.CSVWriter
import com.vaadin.flow.component.AbstractField
import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.contextmenu.ContextMenu
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.grid.Grid.Column
import com.vaadin.flow.component.grid.GridSortOrder
import com.vaadin.flow.component.grid.HeaderRow
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
import com.vaadin.flow.server.StreamRegistration
import com.vaadin.flow.server.StreamResource
import com.vaadin.flow.server.VaadinSession
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import org.galite.base.Application
import org.galite.visual.Window
import org.galite.visual.chart.AreaChart
import org.galite.visual.chart.LineChart
import org.vaadin.examples.form.menu.actorBar.menubar.Actor
import org.vaadin.examples.form.menu.actorBar.menubar.ActorBar
import java.io.InputStream
import java.io.StringWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.ArrayList
import kotlin.collections.set

@CssImport("./src/org/galite/styles/ReportStyle.css", themeFor = "vaadin-grid*")
class Report(Fields: List<Field>, title: String, actors: List<Actor>, application: Application) : Window(title, actors, application) {
  var title = title
  var report = Grid<MutableMap<String, String>>(55)
  var rows: MutableList<MutableMap<String, String>> = ArrayList()
  var actorBar = ActorBar(actors)
  var temporaryColumnId: MutableList<String> = ArrayList()

  init {
    addColumns(Fields)
    report.setHeightByRows(true);
    report.themeName = "grid-theme"
    sortingColumn(report.columns.first())
    report.setItems(rows)
    add(report)
    enableContextMenu()
  }

  // Add columns in the report based on the given fields
  fun addColumns(Fields: List<Field>) {
    for (field in Fields) {
      var column = report.addColumn { hashmap -> hashmap.get(field.name) }
          .setHeader(field.label)
          .setKey(field.label)
          .setSortable(true)
      column.setId(field.name)
      if (!field.footer.isEmpty())
        column.setFooter(field.footer)
    }
  }

  // Sort based on the given column
  fun sortingColumn(column: Grid.Column<MutableMap<String, String>>) {
    val sort: ArrayList<GridSortOrder<MutableMap<String, String>>> = ArrayList<GridSortOrder<MutableMap<String, String>>>()
    val order: GridSortOrder<MutableMap<String, String>> = GridSortOrder<MutableMap<String, String>>(column, SortDirection.ASCENDING)
    sort.add(order)
    report.sort(sort)
  }

  // Enable the reordering of columns
  fun enableReordering() {
    val columnOrder = Span()

    report.isColumnReorderingAllowed = true
    report.addColumnReorderListener { event ->
      columnOrder.text = event.columns.stream()
          .map(Column<MutableMap<String, String>>::getKey).collect(Collectors.joining(", "))
      sortingColumn(event.columns.first())
    }
  }

  //Return the selected row
  fun selectedRow(): MutableMap<String, String>? {
    var selectedRow: MutableMap<String, String>? = null
    report.asSingleSelect().addValueChangeListener { event ->
      val message = String.format("Selection changed from %s to %s",
          event.oldValue, event.value)
      selectedRow = event.value
    }
    return selectedRow
  }

  // Remove the column with the given key
  fun removeColumnByKey(key: String) {
    report.removeColumnByKey(key)
  }

  // Remove the column with the given key
  fun removeColumnById(id: String) {
    val indexOfColumn = report.columns.map { column -> column.id.get() }.indexOf(id)
    report.removeColumnByKey(report.columns[indexOfColumn].key)
  }

  // Add filtre on the top of each column
  fun addFiltre(Columns: List<Column<MutableMap<String, String>>>) {
    lateinit var filterRow: HeaderRow
    if (report.headerRows.size > 1)
      filterRow = report.headerRows[1]
    else
      filterRow = report.appendHeaderRow()
    var dataProvider = report.dataProvider as ListDataProvider

    for (column in Columns) {
      val InputFilter = TextField()
      InputFilter.addValueChangeListener { event ->
        dataProvider.addFilter { HashMap ->
          StringUtils.containsIgnoreCase(HashMap.get(column.id.get()),
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
  fun addLigne(Ligne: MutableMap<String, String>) {
    rows.add(Ligne)
    report.dataProvider.refreshAll()
  }

  // Add charts to Dynamic report
  fun addCharts(application: Application) {
    var data = ""
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
    var actors = listOf(Actor("Project", "Project", "Quit", "", Icon(VaadinIcon.USER)), Actor("Project", "Project", "Hilati", "", Icon(VaadinIcon.USER)))
    var lineChart = LineChart("", actors, application)
    var piechart = AreaChart("", actors, application)
    var chartlayout = HorizontalLayout()
    add(chartlayout)
    type.addValueChangeListener { event: AbstractField.ComponentValueChangeEvent<MultiSelectListBox<String>, MutableSet<String>>? ->
      if (event!!.value.contains("Pie chart") && !chartlayout.children.toArray().contains(piechart)) {
        piechart = AreaChart("[[\"null\", \"hello\",\"wael\",\"c\"], [1,33,332, 5555], [2,34,43, 3]]", actors, application)
        chartlayout.add(piechart)
      } else if (!event!!.value.contains("Pie chart"))
        chartlayout.remove(piechart)
      if (event!!.value.contains("Line chart") && !chartlayout.children.toArray().contains(lineChart)) {
        lineChart = LineChart("[[\"null\", \"hello\",\"wael\",\"c\"], [1,33,332, 5555], [2,34,43, 3]]", actors, application)
        chartlayout.add(lineChart)
      } else if (!event!!.value.contains("Line chart"))
        chartlayout.remove(lineChart)
    }

    columns.addValueChangeListener { event ->
      for (column in event.value) {
        //data = ajoutercolumntodata(data, column)
      }

    }

  }

  // à supprimer en production
  fun remplirTest() {
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

  override fun exportAsExcel(actorBar: ActorBar, format: String) {
    var csvIndex = actorBar.menuBar.items.map { menuItem -> menuItem.text }.indexOf(format.toUpperCase())
    if (csvIndex > 0) {
      var csvActor = actorBar.menuBar.items[csvIndex]
      csvActor.addClickListener { e ->
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.BASIC_ISO_DATE
        val formatted = current.format(formatter)
        val resource = StreamResource(title.filter { it.isLetterOrDigit() } + formatted + "." + format.toLowerCase(), ::createCSV)
        val registration: StreamRegistration = VaadinSession.getCurrent().getResourceRegistry().registerResource(resource)
        UI.getCurrent().page.open(registration.resourceUri.toString())
      }
    }
  }

  fun createCSV(): InputStream {
    var stringWriter = StringWriter()
    var csvWriter = CSVWriter(stringWriter)
    var header = report.columns.map { column -> column.key }
    csvWriter.writeNext(header)
    rows.forEach { row -> csvWriter.writeNext(row.values) }
    return IOUtils.toInputStream(stringWriter.toString(), "UTF-8")
  }

  override fun exportAsPdf(actorBar: ActorBar) {
    var pdfIndex = actorBar.menuBar.items.map { menuItem -> menuItem.text }.indexOf("PDF")
    if (pdfIndex > 0) {
      var pdfActor = actorBar.menuBar.items[pdfIndex]
      pdfActor.addClickListener { e ->
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.BASIC_ISO_DATE
        val formatted = current.format(formatter)
        var converter = ReportToPDF()
        converter.ReportToPDF(this)
        val resource = StreamResource(title.filter { it.isLetterOrDigit() } + formatted + ".pdf", converter::getStream)
        val registration: StreamRegistration = VaadinSession.getCurrent().getResourceRegistry().registerResource(resource)
        UI.getCurrent().page.open(registration.resourceUri.toString())
      }
    }
  }

  // Enable the context menu
  fun enableContextMenu() {
    val expression = "event.currentTarget.getEventContext(event).column.id"
    // Listen for "contextmenu" event on the client side
    report.getElement().addEventListener("contextmenu") { event ->
      // Get coordinates for the ContextMenu position (see addEventData() below)
      val clientX = event.eventData.getNumber("event.clientX")
      val clientY = event.eventData.getNumber("event.clientY")

      // Get the selected Item
      //  val key = event.eventData.getString("event.currentTarget.getEventContext(event).item.key")
      val columnId = event.eventData.getString(expression)
      //element.executeJs("alert($key1)")
      //val item = report.getDataCommunicator().getKeyMapper().get("1")
      var contextMenu: ContextMenu? = null

      //if (item != null) {

      // Visually select the item (Feedback for user)
      if (contextMenu == null) {
        // create the ContextMenu and add it to the Layout
        contextMenu = ContextMenu()
        add(contextMenu);
      }

      // Clear the contextMenu
      contextMenu.removeAll();

      // Now add MenuItems that can use the rightclicked item in the Grid
      contextMenu.addItem("Ajouter une colonne").addClickListener { addTemporaryColumn() }
      contextMenu.addItem("Personnaliser la colonne").addClickListener { }
      if (columnId in temporaryColumnId) {
        contextMenu.addItem("Supprimer la colonne").addClickListener { removeColumnById(columnId) }
        contextMenu.addItem("Attribuer une formule").addClickListener { print("hola") }
      }

      // Open the ContextMenu by code at given Position
      contextMenu.getElement().executeJs("this.open({ target: $0, detail: { x: $1, y: $2 }, clientX: $1, clientY: $2, preventDefault: () => {}, stopPropagation: () => {} })",
          report, clientX, clientY)
    }.addEventData("event.preventDefault()") // Prevent the default Browser ContextMenu
        .addEventData("event.clientX") // Get X Coordinate
        .addEventData("event.clientY") // Get Y coordinate
        .addEventData(expression)
    // Workaround to get the key of the clicked Item
    // .addEventData("event.currentTarget.getEventContext(event).item.key")


  }

  fun addTemporaryColumn() {
    val columnId = "col" + (report.columns.size + 1)
    var column = report.addColumn { hashmap -> hashmap.get(columnId) }
        .setHeader(columnId)
        .setKey(columnId)
        .setSortable(true)
    column.setId(columnId)
    temporaryColumnId.add(columnId)
    addFiltre(listOf(column))
    /*if(!field.footer.isEmpty())
        column.setFooter(field.footer)*/
  }

  private var table: Table? = null
}

fun HasComponents.report(Fields: List<Field>, title: String, actors: List<Actor>, application: Application, function: Report.() -> Unit): Report {
  val report = Report(Fields, title, actors, application)
  report.function()
  return report
}