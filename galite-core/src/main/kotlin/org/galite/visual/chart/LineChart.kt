package org.galite.visual.chart

import com.vaadin.flow.component.Tag
import com.vaadin.flow.component.dependency.JsModule
import com.vaadin.flow.component.dependency.NpmPackage
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.polymertemplate.PolymerTemplate
import com.vaadin.flow.dom.PropertyChangeEvent
import org.galite.base.Application
import org.vaadin.examples.form.menu.actorBar.menubar.Actor

class LineChart(val data: String = "", actors: List<Actor>, application: Application) : Chart("AreaChart", actors, application) {
  val chart = LineChart(data)

  init {
    add(chart)
  }

  @Tag("line-chart")
  @NpmPackage(value = "@google-web-components/google-chart", version = "3.4.0")
  @JsModule("./src/org/galite/line-chart.js")
  class LineChart(var data: String) : PolymerTemplate<ChartModel>() {

    init {
      model.setUserdata(data)
      element.addPropertyChangeListener("chartselection", this::chartSelect)
    }

    private fun chartSelect(event: PropertyChangeEvent?) {
      Notification.show("" + event?.value)
    }

  }

  override fun setData() {
    TODO("Not yet implemented")
  }
}