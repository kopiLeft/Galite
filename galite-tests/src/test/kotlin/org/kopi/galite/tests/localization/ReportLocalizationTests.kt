/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
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
package org.kopi.galite.tests.localization

import java.util.Locale

import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import com.github.mvysny.kaributesting.v10._get

import org.kopi.galite.testing.expect
import org.kopi.galite.testing.open
import org.kopi.galite.testing.triggerCommand
import org.kopi.galite.tests.examples.Training
import org.kopi.galite.tests.examples.initModules
import org.kopi.galite.tests.localization.actor.ExternActor
import org.kopi.galite.tests.localization.code.ExternCode
import org.kopi.galite.tests.localization.list.ExternList
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.visual.ApplicationContext
import org.kopi.galite.visual.domain.CodeDomain
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.ListDomain
import org.kopi.galite.visual.dsl.report.Report
import org.kopi.galite.visual.l10n.LocalizationManager
import org.kopi.galite.visual.ui.vaadin.report.DReport
import org.kopi.galite.visual.ui.vaadin.report.DTable

/**
 * ReportLocalizationTests Report
 */
class ReportLocalizationTests : GaliteVUITestBase() {
  val form = LocalizedForm()
  val localizationManager = LocalizationManager(Locale.UK, Locale.UK)

  @Before
  fun `login to the App`() {
    login()

    // Open the form
    form.open()
  }

  @After
  fun `close pool connection`() {
    ApplicationContext.getDBConnection()?.poolConnection?.close()
  }

  @Test
  fun `test report fields localization`() {
    form.report.triggerCommand()
    val reportTable = _get<DReport>().getTable() as DTable
    val titles = reportTable.model.accessibleColumns.filter { !it!!.label.isNullOrEmpty() }.map { it!!.label }.toList()

    val localizationReport = localizationManager.getReportLocalizer("org/kopi/galite/tests/localization/LocalizedReport")
    val expectedTitles = listOf(localizationReport.getFieldLocalizer("ANM_0").getLabel(),
                      localizationReport.getFieldLocalizer("ANM_1").getLabel(),
                      localizationReport.getFieldLocalizer("ANM_2").getLabel(),
                      localizationReport.getFieldLocalizer("ANM_3").getLabel(),
                      localizationReport.getFieldLocalizer("ANM_4").getLabel()
    )

    assertCollectionsEquals(expectedTitles, titles)
  }

  @Test
  fun `test intern et extern code type`() {
    form.report.triggerCommand()
    val reportTable = _get<DReport>().getTable() as DTable
    val localizationExternType = localizationManager
      .getTypeLocalizer("org/kopi/galite/tests/localization/code/ExternCode", "ExternCode")
    val firstExternCode = localizationExternType.getCodeLabel("id$0")
    val secondExternCode = localizationExternType.getCodeLabel("id$1")
    val localizationInternType = localizationManager
      .getTypeLocalizer("org/kopi/galite/tests/localization/LocalizedReport", "ANM_3")
    val firstInternCode = localizationInternType.getCodeLabel("id$0")
    val secondInternCode = localizationInternType.getCodeLabel("id$1")

    reportTable.expect(arrayOf(
      arrayOf("", "", "", "", "", ""),
      arrayOf("1", firstExternCode, "formation 1", firstInternCode, "categorie 1", ""),
      arrayOf("2", secondExternCode, "formation 2", secondInternCode, "categorie 2", "")
    ))
  }

  companion object {
    @BeforeClass
    @JvmStatic
    fun initTestModules() {
      org.jetbrains.exposed.sql.transactions.transaction(connection.dbConnection) {
        initModules()
      }
    }
  }
}

class LocalizedReport : Report(title = "Rapport", locale = Locale.FRANCE) {
  val externActor = actor(ExternActor())

  val field = field(INT(30)) {
    label = "field interne"
  }

  val active = field(ExternCode()) {
    label = "externe code field"
  }

  val training = field(ExternList()) {
    label = "externe list field"
  }

  val approve = field(object : CodeDomain<String>() {
                                init {
                                  "approuver" keyOf "approuver"
                                  "non approuver" keyOf "non approuver"
                                }}
                ) {
    label = "interne code field"
  }

  val category = field( object : ListDomain<String>(20) {
                        override val table = Training
                          init {
                            "identifiant" keyOf table.id
                            "categorie" keyOf table.type
                        }}
                      ) {
    label = "interne list field"
  }

  init {
    command(item = externActor) {}

    add {
      this[field] = 1
      this[active] = "oui"
      this[training] = "formation 1"
      this[approve] = "approuver"
      this[category] = "categorie 1"
    }
    add {
      this[field] = 2
      this[active] = "non"
      this[training] = "formation 2"
      this[approve] = "non approuver"
      this[category] = "categorie 2"
    }
  }
}
