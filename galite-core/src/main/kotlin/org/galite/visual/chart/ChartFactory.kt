package org.galite.visual.chart

import org.galite.base.Application
import org.vaadin.examples.form.menu.actorBar.menubar.Actor
import java.lang.IllegalStateException

object ChartFactory {

    fun createChart(chartType: ChartType, actors: List<Actor>, application: Application): Chart = when(chartType) {
        ChartType.AREA -> AreaChart("",actors, application)
        ChartType.BAR -> BarChart("",actors, application)
        ChartType.Line -> LineChart("",actors, application)
        ChartType.PIE -> PieChart("",actors, application)
    }

}
