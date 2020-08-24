package org.galite.visual.chart

import com.vaadin.flow.templatemodel.TemplateModel

interface ChartModel : TemplateModel {
  fun setUserdata(data: String)
  fun getUserdata(): String

  fun getChartselection(): String

}
