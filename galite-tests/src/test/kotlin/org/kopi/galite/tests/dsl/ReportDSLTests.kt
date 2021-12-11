/*
 * Copyright (c) 2013-2021 kopiLeft Services SARL, Tunis TN
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
package org.kopi.galite.tests.dsl

import java.util.Locale

import kotlin.test.assertEquals

import org.junit.Test
import org.kopi.galite.tests.ui.vaadin.VApplicationTestBase
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.STRING
import org.kopi.galite.visual.dsl.report.FieldAlignment
import org.kopi.galite.visual.dsl.report.Report

class ReportDSLTests : VApplicationTestBase() {

  @Test
  fun `test generated model from a basic report`() {
    val report = BasicReport()
    val model = report.model

    assertEquals(report.locale, model.locale)
    assertEquals(report.title, model.getTitle())
    assertEquals(report.help, model.help)
  }

  @Test
  fun `test report fields`() {
    val report = ReportWithData()
    val reportModel = report.model

    assertEquals(5, reportModel.model.columns.size)
    assertEquals(true, reportModel.model.columns[0]!!.isHidden())
    assertEquals(report.id.domain.width, reportModel.model.columns[0]!!.width)

    assertEquals(false, reportModel.model.columns[1]!!.isHidden())
    assertEquals(report.firstName.domain.width, reportModel.model.columns[1]!!.width)
    assertEquals(report.firstName.label, reportModel.model.columns[1]!!.label)
    assertEquals(report.firstName.help, reportModel.model.columns[1]!!.help)
    assertEquals(report.firstName.align.value, reportModel.model.columns[1]!!.align)
    assertEquals(listOf("client 1", "client 1", "client 2"), report.getRowsForField(report.firstName))

    assertEquals(false, reportModel.model.columns[2]!!.isHidden())
    assertEquals(report.addressClt.domain.width, reportModel.model.columns[2]!!.width)
    assertEquals(report.addressClt.label, reportModel.model.columns[2]!!.label)
    assertEquals(report.addressClt.help, reportModel.model.columns[2]!!.help)
    assertEquals(report.addressClt.align.value, reportModel.model.columns[2]!!.align)
    assertEquals(listOf("Tunis", "Bizerte", "Ben Arous"), report.getRowsForField(report.addressClt))

    assertEquals(false, reportModel.model.columns[3]!!.isHidden())
    assertEquals(report.ageClt.domain.width, reportModel.model.columns[3]!!.width)
    assertEquals(report.ageClt.label, reportModel.model.columns[3]!!.label)
    assertEquals(report.ageClt.help, reportModel.model.columns[3]!!.help)
    assertEquals(report.ageClt.align.value, reportModel.model.columns[3]!!.align)
    assertEquals(listOf(20, 20, 30), report.getRowsForField(report.ageClt))
  }

  @Test
  fun `test report rows`() {
    val report = ReportWithData()

    report.add {
      this[report.id] = 1
      this[report.firstName] = "client Test"
      this[report.addressClt] = "Test"
      this[report.ageClt] = 10
    }
    report.model.initReport()
    val reportModel = report.model.model

    assertEquals(5, reportModel.getVisibleRowCount())
    assertEquals(1, reportModel.getRow(2)?.getValueAt(0))
    assertEquals("client Test", reportModel.getRow(2)?.getValueAt(1))
    assertEquals("Test", reportModel.getRow(2)?.getValueAt(2))
    assertEquals(10, reportModel.getRow(2)?.getValueAt(3))
  }

  /* @Test
   fun `test report triggers`() {
     val report = ReportWithData()
     val reportModel = report.model

     assertEquals(true, reportModel.hasTrigger(VConstants.TRG_INIT))
     assertEquals(true, reportModel.hasTrigger(VConstants.TRG_PREBLK))
   }*/
}

class BasicReport : Report() {
  override val locale = Locale.UK
  override val title = "Clients Report"
  override val help = "This is a Report that contains information about clients"
}

class ReportWithData : Report() {
  override val locale = Locale.UK
  override val title = "Clients Report"

  val preReport = trigger(PREREPORT) {}
  val postReport = trigger(POSTREPORT) {}

  val id = field(INT(11)) {
    hidden = true
  }

  val firstName = field(STRING(25)) {
    label = "First Name"
    help = "The client first name"
    align = FieldAlignment.LEFT
  }

  val addressClt = field(STRING(50)) {
    label = "Address"
    help = "The client address"
    align = FieldAlignment.CENTER
  }

  val ageClt = field(INT(2)) {
    label = "Age"
    help = "The client age"
  }

  init {
    add {
      this[id] = 1
      this[firstName] = "client 1"
      this[addressClt] = "Tunis"
      this[ageClt] = 20
    }
    add {
      this[id] = 2
      this[firstName] = "client 1"
      this[addressClt] = "Bizerte"
      this[ageClt] = 20
    }
    add {
      this[id] = 3
      this[firstName] = "client 2"
      this[addressClt] = "Ben Arous"
      this[ageClt] = 30
    }
  }
}
