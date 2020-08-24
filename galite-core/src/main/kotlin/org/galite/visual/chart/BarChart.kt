package org.galite.visual.chart

import com.vaadin.flow.component.Tag
import com.vaadin.flow.component.dependency.JsModule
import com.vaadin.flow.component.dependency.NpmPackage
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.polymertemplate.PolymerTemplate
import com.vaadin.flow.dom.PropertyChangeEvent
import org.galite.base.Application
import org.vaadin.examples.form.menu.actorBar.menubar.Actor

class BarChart(val data: String = "", actors : List<Actor>, application : Application) : Chart("AreaChart", actors, application) {
    val chart = BarChart()

    init {
        add(chart)
    }

    override fun setData() {
        chart.data = Formatter.encode(dimension!!)
    }

    @Tag("bar-chart")
    @NpmPackage(value = "@google-web-components/google-chart", version = "3.4.0")
    @JsModule("./src/org/galite/bar-chart.js")
    class BarChart() : PolymerTemplate<ChartModel>() {
        var data: String = ""
            set(value) {
                model.setUserdata(value)
            }

        init {
            element.addPropertyChangeListener("chartselection", this::chartSelect)
        }

        private fun chartSelect(event: PropertyChangeEvent?) {
            Notification.show("" + event?.value)
        }
    }
}




