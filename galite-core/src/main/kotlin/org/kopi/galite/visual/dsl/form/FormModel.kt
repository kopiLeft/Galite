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
package org.kopi.galite.visual.dsl.form

import org.kopi.galite.visual.cross.VReportSelectionForm
import org.kopi.galite.visual.db.DBContext
import org.kopi.galite.visual.db.DBContextHandler
import org.kopi.galite.visual.dsl.common.Trigger
import org.kopi.galite.visual.form.VBlock
import org.kopi.galite.visual.form.VConstants
import org.kopi.galite.visual.form.VDictionaryForm
import org.kopi.galite.visual.form.VForm
import org.kopi.galite.visual.report.VReport
import org.kopi.galite.visual.visual.ApplicationContext

class FormModel: VForm {
  val form: Form

  internal constructor(form: Form): super() {
    this.form = form
    initIntern(false)
  }

  internal constructor(form: Form, ctxt: DBContext) : super(ctxt) {
    this.form = form
    initIntern(true)
  }

  internal constructor(form: Form, ctxt: DBContextHandler) : super(ctxt) {
    this.form = form
    initIntern(true)
  }

  override val locale get() = form.locale ?: ApplicationContext.getDefaultLocale()

  override fun init() {
    initialize(form)
  }
}

class DictionaryFormModel: VDictionaryForm {
  val form: DictionaryForm

  internal constructor(form: DictionaryForm, ctxt: DBContextHandler) : super(ctxt) {
    this.form = form
    initIntern(true)
  }

  internal constructor(form: DictionaryForm, ctxt: DBContext) : super(ctxt) {
    this.form = form
    initIntern(true)
  }

  internal constructor(form: DictionaryForm): super() {
    this.form = form
    initIntern(false)
  }

  override val locale get() = form.locale ?: ApplicationContext.getDefaultLocale()

  override fun init() {
    initialize(form)
  }
}

class ReportSelectionFormModel: VReportSelectionForm {
  val form: ReportSelectionForm

  internal constructor(form: ReportSelectionForm, ctxt: DBContextHandler) : super(ctxt) {
    this.form = form
    initIntern(true)
  }

  internal constructor(form: ReportSelectionForm, ctxt: DBContext) : super(ctxt) {
    this.form = form
    initIntern(true)
  }

  internal constructor(form: ReportSelectionForm): super() {
    this.form = form
    initIntern(false)
  }

  override val locale get() = form.locale ?: ApplicationContext.getDefaultLocale()

  override fun init() {
    initialize(form)
  }

  override fun createReport(): VReport {
    return form.createReport().model
  }
}

fun VForm.initialize(form: Form) {
  buildForm(form)
  buildBlocks(form)
}

private fun VForm.buildForm(form: Form) {
  source = form.sourceFile
  setTitle(form.title)
  pages = form.pages.map {
    it.title
  }.toTypedArray()
  pagesIdents = form.pages.map {
    it.ident
  }.toTypedArray()
  addActors(form.actors.map { actor ->
    actor.buildModel(form.sourceFile)
  }.toTypedArray())
  commands = form.commands.map { command ->
    command.buildModel(this, actors)
  }.toTypedArray()

  handleTriggers(form)
}

/**
 * Handling form triggers
 */
private fun VForm.handleTriggers(form: Form) {
  // FORM TRIGGERS
  val formTriggerArray = arrayOfNulls<Trigger>(VConstants.TRG_TYPES.size)
  form.triggers.forEach { trigger ->
    for (i in VConstants.TRG_TYPES.indices) {
      if (trigger.events shr i and 1 > 0) {
        formTriggerArray[i] = trigger
      }
    }
    VKT_Triggers[0] = formTriggerArray
  }

  // COMMANDS TRIGGERS
  form.commands.forEach {
    val fieldTriggerArray = arrayOfNulls<Trigger>(VConstants.TRG_TYPES.size)
    // TODO : Add commands triggers here
    VKT_Triggers.add(fieldTriggerArray)
  }
}

private fun VForm.buildBlocks(form: Form) {
  val formBlocks = form.blocks.map { buildBlock(it) }
    .toTypedArray()

  blocks = formBlocks
}

private fun VForm.buildBlock(formBlock: FormBlock): VBlock {
  val vBlock = formBlock.getBlockModel(this, source)

  vBlock.setInfo(formBlock.pageNumber, this)
  vBlock.initIntern()
  formBlock.fields.forEach { formField ->
    formField.initialValues.forEach {
      formField.vField.setObject(it.key, it.value)
    }
  }

  return vBlock
}
