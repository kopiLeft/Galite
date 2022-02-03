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

import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.kopi.galite.testing.open
import org.kopi.galite.tests.examples.initModules
import org.kopi.galite.tests.localization.actor.ExternActor
import org.kopi.galite.tests.localization.block.ExternBlock
import org.kopi.galite.tests.ui.vaadin.GaliteVUITestBase
import org.kopi.galite.visual.domain.INT
import org.kopi.galite.visual.dsl.common.Icon
import org.kopi.galite.visual.dsl.form.Block
import org.kopi.galite.visual.dsl.form.Border
import org.kopi.galite.visual.dsl.form.Form
import org.kopi.galite.visual.dsl.form.Key
import org.kopi.galite.visual.l10n.LocalizationManager
import org.kopi.galite.visual.ui.vaadin.window.VActorPanel

import com.github.mvysny.kaributesting.v10._expect
import com.github.mvysny.kaributesting.v10._find
import com.github.mvysny.kaributesting.v10._get
import com.github.mvysny.kaributesting.v10._text
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H4
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.VerticalLayout

class FormLocalTests : GaliteVUITestBase() {
  val form = LocalizedForm().also { it.model }
  val localizationManager = LocalizationManager(Locale.UK, Locale.UK)

  @Before
  fun `login to the App`() {
    login()

    // Open the form
    form.open()
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
    val internField = localizationManager
      .getBlockLocalizer("org/kopi/galite/tests/localization/LocalizedForm", "InternBlock")
      .getFieldLocalizer("FLD_0")

    val externField = localizationManager
      .getBlockLocalizer("org/kopi/galite/tests/localization/block/ExternBlock", "ExternBlock")
      .getFieldLocalizer("FLD_0")

    val externField1 = localizationManager
      .getBlockLocalizer("org/kopi/galite/tests/localization/block/ExternBlock", "ExternBlock")
      .getFieldLocalizer("FLD_1")

    val internLabel = _find<VerticalLayout> { classes = "k-block" }[0]._find<Span> { classes = "label" }[0]
    assertEquals(internField.getLabel(), internLabel.text)

    val externLabel = _find<VerticalLayout> { classes = "k-block" }[1]._find<Span> { classes = "label" }[0]
    assertEquals(externField.getLabel(), externLabel.text)

    val externLabel1 = _find<VerticalLayout> { classes = "k-block" }[1]._find<Span> { classes = "label" }[1]
    assertEquals(externField1.getLabel(), externLabel1.text)
  }

  @Test
  fun `test actors name`() {
    val actorPanel = _get<VActorPanel> { id = "actors" }
    val actors = actorPanel._find<org.kopi.galite.visual.ui.vaadin.actor.Actor> { classes = "k-actor" }

    val internActor = localizationManager.getActorLocalizer("org/kopi/galite/tests/localization/LocalizedForm", "actor0")
    val externActor = localizationManager.getActorLocalizer("org/kopi/galite/tests/localization/actor/ExternActor", "actor1")

    assertEquals(internActor.getLabel(), actors[0]._text)
    assertEquals(externActor.getLabel(), actors[1]._text)
  }

  @Test
  fun `test menu names`() {
    val button = _get<Button> { classes = "actors-rootNavigationItem" }

    val internMenu = localizationManager.getMenuLocalizer("org/kopi/galite/tests/localization/LocalizedForm", "Menu Interne")
    val externMenu = localizationManager.getMenuLocalizer("org/kopi/galite/tests/localization/menu/ExternMenu", "Menu Externe")

    button.click()
    _expect<Dialog> {  }
    val dialog = _get<Dialog> {}

    assertNotNull(dialog)

    val navigationColumns = _find<VerticalLayout> { classes= "actor-navigationColumn" }

    assertEquals(3, navigationColumns.size)

    val headers = _find<Span> { classes = "header" }.map { it._get<Div> {  } }

    assertEquals("File", headers[0].text)
    assertEquals(internMenu.getLabel(), headers[1].text)
    assertEquals(externMenu.getLabel(), headers[2].text)
  }

  companion object {
    @BeforeClass
    @JvmStatic
    fun initTestModules() {
      transaction {
        initModules()
      }
    }
  }
}

class LocalizedForm: Form(title = "Formulaire", locale = Locale.FRANCE) {
  val  internMenu = menu("Menu Interne")

  val internActor = actor(
    menu = internMenu,
    label = "Actor Interne",
    help = "",
  ) {
    key = Key.F6
    icon = Icon.REPORT
  }

  val externActor = actor(ExternActor())

  val internBlock = insertBlock(InternBlock())
  val externBlock = insertBlock(ExternBlock())

  inner class InternBlock: Block("Block Interne", 1, 1) {
    val field = visit(domain = INT(30), position = at(1, 1)) {
      label = "field interne"
    }

    init {
      border = Border.LINE
      command(item = internActor) {}
      command(item = externActor) {}

    }
  }
}
