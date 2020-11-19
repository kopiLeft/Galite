package org.kopi.galite.form.dsl

import org.kopi.galite.form.VConstants

enum class ModCommand(val value: Int){
  QUERY (VConstants.MOD_QUERY),
  UPDATE (VConstants.MOD_UPDATE),
  INSERT (VConstants.MOD_INSERT),
}