package org.kopi.galite.form.dsl

import org.kopi.galite.form.VConstants
import org.kopi.galite.form.VConstants.Companion.TRG_CHANGED
import org.kopi.galite.form.VConstants.Companion.TRG_INIT
import org.kopi.galite.form.VConstants.Companion.TRG_POSTFORM
import org.kopi.galite.form.VConstants.Companion.TRG_PREFORM
import org.kopi.galite.form.VConstants.Companion.TRG_QUITFORM
import org.kopi.galite.form.VConstants.Companion.TRG_RESET

open class FormEvent(i: Int) {

  var event: Int=0

  init {
    when (i) {
      TRG_PREFORM -> event = TRG_PREFORM
      TRG_POSTFORM -> event = TRG_POSTFORM
      TRG_INIT -> event = TRG_INIT
      TRG_QUITFORM -> event = TRG_QUITFORM
      TRG_RESET -> event = TRG_RESET
      TRG_CHANGED -> event = TRG_CHANGED
    }
  }
}
