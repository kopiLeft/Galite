/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.visual.ui.vaadin.form

import com.vaadin.flow.component.HasStyle
import org.kopi.galite.visual.VColor
import org.kopi.galite.visual.form.VBlock
import org.kopi.galite.visual.form.VColorField
import org.kopi.galite.visual.form.VField
import org.kopi.galite.visual.ui.vaadin.grid.GridEditorField
import java.awt.Color

/**
 * Grid block data source item
 */
data class GridBlockItem(val record: Int,
                         var foregroundColor: Color? = Color.BLACK,
                         var backgroundColor: Color? = Color.WHITE) {

  // --------------------------------------------------
  // IMPLEMENTATION
  // --------------------------------------------------
  fun getValue(field: VField): Any? {
    return field.getObject(record)
  }

  fun setBackgroundColor(field: VField) {
    backgroundColor = getColor(field)
  }

  fun setColor(model: VBlock, c: Color?) {
    model.fields.forEach { it.setColor(record, VColor(0, 0, 0), VColor(c!!.red, c!!.green, c.blue)) }
  }

  fun getColor(field: VField): Color? {
    return if (field is VColorField) {
      field.getObject(record) as Color?
    } else {
      null
    }
  }

  override fun toString(): String = "record : $record, foregroundColor : $foregroundColor, backgroundColor : $backgroundColor"
}
