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

import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.streams.toList

import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import com.github.mvysny.kaributesting.v10._expect
import com.github.mvysny.kaributesting.v10._expectOne
import com.github.mvysny.kaributesting.v10._find
import com.github.mvysny.kaributesting.v10._get
import com.github.mvysny.kaributesting.v10._text

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H4
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import org.junit.Ignore

import org.kopi.galite.database.Users
import org.kopi.galite.testing.open
import org.kopi.galite.tests.examples.Training
import org.kopi.galite.tests.examples.initDatabase
import org.kopi.galite.tests.examples.initModules
import org.kopi.galite.tests.localization.actor.ExternActor
import org.kopi.galite.tests.localization.block.ExternBlock
import org.kopi.galite.tests.localization.code.ExternCode
import org.kopi.galite.tests.localization.list.ExternList
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.visual.ApplicationContext
import org.kopi.galite.visual.domain.CodeDomain
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.domain.ListDomain
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.common.PredefinedCommand
import org.kopi.galite.visual.dsl.form.Block
import org.kopi.galite.visual.dsl.form.Border
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.dsl.form.ReportSelectionForm
import org.kopi.galite.visual.l10n.LocalizationManager
import org.kopi.galite.visual.ui.vaadin.actor.Actor
import org.kopi.galite.visual.ui.vaadin.field.TextField
import org.kopi.galite.visual.ui.vaadin.field.VCodeField
import org.kopi.galite.visual.ui.vaadin.form.DListDialog
import org.kopi.galite.visual.ui.vaadin.list.GridListDialog
import org.kopi.galite.visual.ui.vaadin.list.ListTable
import org.kopi.galite.visual.ui.vaadin.window.VActorPanel

class FormLocalTests : GaliteVUITestBase() {
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
  fun `test titles of form blocks`() {
    val innerBlockLocalizer = localizationManager
      .getBlockLocalizer("org/kopi/galite/tests/localization/LocalizedForm", "InternBlock")
    val externBlockLocalizer = localizationManager
      .getBlockLocalizer("org/kopi/galite/tests/localization/block/ExternBlock", "ExternBlock")
    val internBlockCaption = _find<VerticalLayout> { classes = "caption-container" }[0]
      ._get<H4> { classes = "block-title" }
    val externBlockCaption = _find<VerticalLayout> { classes = "caption-container" }[1]
      ._get<H4> { classes = "block-title" }

    assertEquals(innerBlockLocalizer.getTitle(), internBlockCaption.text)
    assertEquals(externBlockLocalizer.getTitle(), externBlockCaption.text)
  }

  @Test
  fun `test fields label`() {
    val localizationInternBlock = localizationManager
      .getBlockLocalizer("org/kopi/galite/tests/localization/LocalizedForm", "InternBlock")
    val localizationExternBlock = localizationManager
      .getBlockLocalizer("org/kopi/galite/tests/localization/block/ExternBlock", "ExternBlock")

    val internField = localizationInternBlock.getFieldLocalizer("FLD_0")
    val externCode = localizationInternBlock.getFieldLocalizer("FLD_1")
    val externList = localizationInternBlock.getFieldLocalizer("FLD_2")
    val internCode = localizationInternBlock.getFieldLocalizer("FLD_3")
    val internList = localizationInternBlock.getFieldLocalizer("FLD_4")
    val externField = localizationExternBlock.getFieldLocalizer("FLD_0")
    val externField1 = localizationExternBlock.getFieldLocalizer("FLD_1")

    val firstBlock = _find<VerticalLayout> { classes = "k-block" }[0]
    val secondBlock = _find<VerticalLayout> { classes = "k-block" }[1]
    val internLabel = firstBlock._find<Span> { classes = "label" }[0]
    val externCodeLabel = firstBlock._find<Span> { classes = "label" }[1]
    val externListLabel = firstBlock._find<Span> { classes = "label" }[2]
    val internCodeLabel = firstBlock._find<Span> { classes = "label" }[3]
    val internListLabel = firstBlock._find<Span> { classes = "label" }[4]
    val externLabel = secondBlock._find<Span> { classes = "label" }[0]
    val externLabel1 = secondBlock._find<Span> { classes = "label" }[1]

    assertEquals(internField.getLabel(), internLabel.text)
    assertEquals(externCode.getLabel(), externCodeLabel.text)
    assertEquals(externList.getLabel(), externListLabel.text)
    assertEquals(internCode.getLabel(), internCodeLabel.text)
    assertEquals(internList.getLabel(), internListLabel.text)
    assertEquals(externField.getLabel(), externLabel.text)
    assertEquals(externField1.getLabel(), externLabel1.text)
  }

  @Test
  fun `test actors name`() {
    val actorPanel = _get<VActorPanel> { id = "actors" }
    val actors = actorPanel._find<Actor> { classes = "k-actor" }
    val internActor = localizationManager.getActorLocalizer("org/kopi/galite/tests/localization/LocalizedForm", "actor0")
    val reportActor = localizationManager.getActorLocalizer("org/kopi/galite/tests/localization/LocalizedForm", "actor2")
    val externActor = localizationManager.getActorLocalizer("org/kopi/galite/tests/localization/actor/ExternActor", "ExternActor")

    assertEquals(internActor.getLabel(), actors[0]._text)
    assertEquals(externActor.getLabel(), actors[1]._text)
    assertEquals(reportActor.getLabel(), actors[2]._text)
  }

  @Test
  fun `test menu names`() {
    val button = _get<Button> { classes = "actors-rootNavigationItem" }

    val internMenu = localizationManager
      .getMenuLocalizer("org/kopi/galite/tests/localization/LocalizedForm", "Menu Interne")
    val externMenu = localizationManager
      .getMenuLocalizer("org/kopi/galite/tests/localization/menu/ExternMenu", "Menu Externe")

    button.click()
    _expect<Dialog> {  }
    val dialog = _get<Dialog> {}

    assertNotNull(dialog)

    val navigationColumns = _find<VerticalLayout> { classes= "actor-navigationColumn" }

    assertEquals(3, navigationColumns.size)

    val headers = _find<Span> { classes = "header" }.map { it._get<Div> {  } }

    assertEquals("File", headers[0].text)
    assertEquals(externMenu.getLabel(), headers[1].text)
    assertEquals(internMenu.getLabel(), headers[2].text)
  }

  @Test
  fun `test extern code type`() {
    val localizationType = localizationManager
      .getTypeLocalizer("org/kopi/galite/tests/localization/code/ExternCode", "ExternCode")
    val firstCode = localizationType.getCodeLabel("id$0")
    val secondCode = localizationType.getCodeLabel("id$1")

    val comboBox = _find<VCodeField> { classes = "k-textinput" }[0]
    val items = comboBox.content.genericDataView.items.toList()

    assertEquals(firstCode, items[0])
    assertEquals(secondCode, items[1])
  }

  @Test
  fun `test intern code type`() {
    val localizationType = localizationManager
      .getTypeLocalizer("org/kopi/galite/tests/localization/LocalizedForm", "FLD_3")
    val firstCode = localizationType.getCodeLabel("id$0")
    val secondCode = localizationType.getCodeLabel("id$1")

    val comboBox = _find<VCodeField> { classes = "k-textinput" }[1]
    val items = comboBox.content.genericDataView.items.toList()

    assertEquals(firstCode, items[0])
    assertEquals(secondCode, items[1])
  }

  @Ignore // !! FIXME !! mgrati 20231213 : notice generates test error when replacing Galite connection to use HikariCP
  @Test
  fun `test extern list type`() {
    val localizationList = localizationManager
      .getListLocalizer("org/kopi/galite/tests/localization/list/ExternList", "ExternList")
    val id = localizationList.getColumnTitle("ID")
    val trainingName = localizationList.getColumnTitle("Name")

    val trainingField = _find<VerticalLayout> { classes = "k-block" }[0]._find<TextField> { classes = "k-textfield" }[2]
    val autofill = trainingField._get<IronIcon> {}

    autofill._clickAndWait(500)

    // Check that the list dialog is displayed
     _expectOne<GridListDialog>()

    val listDialog = _get<GridListDialog>()
    listDialog._expectOne<Grid<*>>()

    val grid = _get<DListDialog>()._get<ListTable>()
    val titles =  grid.model.titles

    assertEquals(id, titles[0])
    assertEquals(trainingName, titles[1])
  }

  @Ignore // !! FIXME !! mgrati 20231213 : notice generates test error when replacing Galite connection to use HikariCP
  @Test
  fun `test intern list type`() {
    val localizationList = localizationManager
      .getListLocalizer("org/kopi/galite/tests/localization/LocalizedForm", "FLD_4")
    val id = localizationList.getColumnTitle("ID")
    val category = localizationList.getColumnTitle("type")

    val categoryField = _find<VerticalLayout> { classes = "k-block" }[0]._find<TextField> { classes = "k-textfield" }[4]
    val autofill = categoryField._get<IronIcon> {}

    autofill._clickAndWait(500)

    // Check that the list dialog is displayed
    _expectOne<GridListDialog>()

    val listDialog = _get<GridListDialog>()
    listDialog._expectOne<Grid<*>>()

    val grid = _get<DListDialog>()._get<ListTable>()
    val titles =  grid.model.titles

    assertEquals(id, titles[0])
    assertEquals(category, titles[1])
  }

  companion object {
    @BeforeClass
    @JvmStatic
    fun initTestModules() {
      org.jetbrains.exposed.sql.transactions.transaction(connection.dbConnection) {
        initModules()
        initDatabase()
      }
    }
  }
}

class LocalizedForm: ReportSelectionForm(title = "Formulaire", locale = Locale.FRANCE) {
  val internMenu = menu("Menu Interne")
  val internActor = actor(menu = internMenu, label = "Actor Interne", help = "", ) {
    key = Key.F6
    icon = Icon.REPORT
  }

  val externActor = actor(ExternActor())

  val autoFill = actor(menu = internMenu, label = "Autofill", help = "Autofill", command = PredefinedCommand.AUTOFILL)

  override val report = actor(menu = internMenu, label = "Creer un rapport", help = "Creer un rapport") {
    key = Key.F8
    icon = Icon.REPORT
  }

  val internBlock = insertBlock(InternBlock())
  val externBlock = insertBlock(ExternBlock())

  inner class InternBlock: Block("Block Interne", 1, 10) {
    val u = table(Users)

    val field = visit(domain = INT(30), position = at(1, 1)) {
      label = "field interne"
    }

    val active = visit(domain = ExternCode(), position = at(2, 1)) {
      label = "code externe"
    }

    val training = visit(domain = ExternList(), position = at(3, 1)) {
      label = "list externe"
    }

    val approve = visit(domain = object : CodeDomain<String>() {
                                            init {
                                              "approuver" keyOf "approuver"
                                              "non approuver" keyOf "non approuver"
                                            }}, position = at(4, 1)) {
      label = "code interne"
    }

    val category = visit(domain = object : ListDomain<String>(20) {
                                              override val table = Training
                                                init {
                                                  "identifiant" keyOf table.id
                                                  "categorie" keyOf table.type
                                                }
                                              }, position = at(5, 1)) {
      label = "list interne"
    }

    init {
      border = Border.LINE
      command(item = internActor) {}
      command(item = externActor) {}
      command(item = report) {
        createReport {
          LocalizedReport()
        }
      }
    }
  }
}
