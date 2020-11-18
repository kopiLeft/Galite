package org.kopi.galite.tests.form

import org.junit.Test
import org.kopi.galite.form.VForm
import org.kopi.galite.tests.JApplicationTestBase

class TriggerTests: JApplicationTestBase() {
}
object TestTrigger : VForm() {
  init {
    init {
      print("hello")
    }
    reset{

    }
    preform {

    }

  }
  @Test
  fun simpleTriggerTest() {
  }

}