package org.kopi.galite.visual.pivottable

import org.kopi.galite.visual.dsl.pivottable.Dimension
import org.kopi.galite.visual.report.VCellFormat

/**
 * Represents a pivot table column description
 * @param    ident        The identifier of the field
 * @param    position     The position of the dimension field
 */
class VBooleanColumn(ident: String?,
                     position: Dimension.Position?,
                     format: VCellFormat?)
      : VPivotTableColumn(ident,
                          position,
                          format) {

  }