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

package org.kopi.galite.visual.pivottable.triggers

import java.math.BigDecimal

import org.kopi.galite.visual.pivottable.VCalculateColumn
import org.kopi.galite.visual.pivottable.VPivotTableRow

///////////////////////////////////////////////////////////////////////////
// This file regroups predefined pivot table triggers
///////////////////////////////////////////////////////////////////////////


/**
 * calcul basé sur deux colonnes : se baser sur les valeurs des deux colonnes pour calculer
 * les valeurs d'une autre colonne
 */
fun customCompute(posCol1: Int,
                  posCol2: Int,
                  calc: TwoColumnsComputation): VCalculateColumn {

  return object : VCalculateColumn() {
    fun sumColumn(row: VPivotTableRow, column: Int, pos: Int): Number {
      return if (row.getValueAt(column + pos) != null) {
          row.getValueAt(column + pos) as Number
      } else {
        BigDecimal.ZERO
      }
    }

    override fun evalNode(row: VPivotTableRow, column: Int): Any {
      val   sum1 = sumColumn(row, column, posCol1)
      val   sum2 = sumColumn(row, column, posCol2)

      return calc.compute(sum1, sum2)
    }

    override fun evalLeaf(row: VPivotTableRow, column: Int): Any {
      return evalNode(row, column)
    }
  }
}

/**
 * calcul basé sur une colonne : se baser sur les valeurs d'une colonne pour calculer
 * les valeurs d'une autre colonne
 */
fun customCompute(posCol1: Int,
                  calc: OneColumnsComputation
): VCalculateColumn {

  return object : VCalculateColumn() {
    fun sumColumn(row: VPivotTableRow, column: Int, pos: Int): Number {
      return if (row.getValueAt(column + pos) != null) {
          row.getValueAt(column + pos) as Number
        } else {
          BigDecimal.ZERO
        }
    }

    override fun evalNode(row: VPivotTableRow, column: Int): Any {
      val   sum1 = sumColumn(row, column, posCol1)
      return calc.compute(sum1)
    }

    override fun evalLeaf(row: VPivotTableRow, column: Int): Any {
      return evalNode(row, column)
    }
  }
}

/**
 * Multiplication de deux colonnes
 */
fun multi(posCol1: Int, posCol2: Int): VCalculateColumn {
  return customCompute(posCol1, posCol2, object : TwoColumnsComputation() {
    override fun compute(c1: Number, c2: Number): BigDecimal {
      val valeur1 = c1 as BigDecimal
      val valeur2 = c2 as BigDecimal

      return  valeur1.multiply(valeur2)
    }
  })
}

/**
 * Multiplication d'une colonne
 */
fun div(posCol1: Int): VCalculateColumn {
  return customCompute(posCol1, object : OneColumnsComputation() {
    override fun compute(c1: Number): BigDecimal {
      val valeur1 = c1 as BigDecimal

      return  valeur1.div(BigDecimal(10))
    }
  })
}

//---------------------------------------------------
// INNER CLASSES
//---------------------------------------------------

/**
 * Classe utilitaire pour effectuer un calcul basé
 * sur deux colonnes.
 */
abstract class TwoColumnsComputation {
  /**
   * Effectuer le calcul basé sur deux object.
   */
  abstract fun compute(c1: Number, c2: Number): Number
}

/**
 * Classe utilitaire pour effectuer un calcul basé
 * sur une colonne.
 */
abstract class OneColumnsComputation {
  /**
   * Effectuer le calcul basé sur deux object.
   */
  abstract fun compute(c1: Number): Number
}