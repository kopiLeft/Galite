/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2020 kopiRight Managed Solutions GmbH, Wien AT
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
package org.kopi.galite.form.dsl

import java.util.ArrayList

import org.kopi.galite.form.BlockAlignment

/**
 * This class describe the alignment of multi blocks
 *
 * @param block                 block alignment
 * @param target                the target column vector
 * @param source                the source column vector
 */
class FormBlockAlign(private val sourceBlock: FormBlock,
                     private val targetBlock: FormBlock,
                     private val sources: ArrayList<Int>,
                     private val targets: ArrayList<Int>) {

  /**
   * @return an IntArray contains the position of source fields in the target block
   */
  fun genTargetPositions() : IntArray {
    val columnCount: Int = sourceBlock.blockFields.size
    val targetResult = arrayListOf<Int>()
    var pos = 0

    for (i in 0 until columnCount) {
      if (sources[pos] != i + 1) {
        targetResult.add(-1)
      } else {
        targetResult.add(targets[pos] - 1)
        pos += 1
      }
    }
    return targetResult.toTypedArray().toIntArray()
  }

  fun getBlockAlignModel() : BlockAlignment = BlockAlignment(targetBlock.vBlock, genTargetPositions())
}
