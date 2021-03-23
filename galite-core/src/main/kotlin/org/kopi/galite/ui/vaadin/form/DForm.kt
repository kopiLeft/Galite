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
package org.kopi.galite.ui.vaadin.form

import java.io.File

import org.kopi.galite.form.BlockListener
import org.kopi.galite.form.BlockRecordListener
import org.kopi.galite.form.UBlock
import org.kopi.galite.form.UForm
import org.kopi.galite.form.VBlock
import org.kopi.galite.form.VField
import org.kopi.galite.form.VFieldException
import org.kopi.galite.form.VForm
import org.kopi.galite.ui.vaadin.visual.DWindow
import org.kopi.galite.util.PrintJob
import org.kopi.galite.util.base.InconsistencyException
import org.kopi.galite.visual.Action
import org.kopi.galite.visual.VRuntimeException

/**
 * The `DForm` is the vaadin implementation of the [UForm] specifications.
 *
 * @param model The form model.
 */
class DForm(model: VForm) : DWindow(model), UForm, FormListener {

  /**
   * Returns the current page index.
   * @return The current page index.
   */
  var currentPage = -1
  val content: Form = Form(pageCount, model.pages)
  private val blockListener: BlockListener = BlockAccessHandler()
  private val blockViews: Array<DBlock?>
  private val blockRecordHandler: BlockRecordHandler = BlockRecordHandler()

  init {
    // content.locale = application.defaultLocale.toString() TODO
    model.addFormListener(this)
    //content.addFormListener(this) TODO
    getModel().setDisplay(this)
    val blockCount = vForm.getBlockCount()
    blockViews = arrayOfNulls(blockCount)
    for (i in 0 until blockCount) {
      val blockModel = vForm.getBlock(i)
      if (!blockModel.isInternal) {
        val blockView = createViewForBlock(blockModel)
        blockViews[i] = blockView
        addBlock(blockView, blockModel.pageNumber)
      }
      blockModel.addBlockListener(blockListener)
    }
    setContent(content)
    getModel().enableCommands()
  }

  //---------------------------------------------------
  // IMPLEMENTATIONS
  //---------------------------------------------------
  /**
   * Creates the block view for a given block model
   * @param blockModel The block model.
   */
  protected fun createViewForBlock(blockModel: VBlock): DBlock {
    val blockView: DBlock
    if (!blockModel.isMulti()) {
      blockView = DBlock(this, blockModel)
    } else {
      if (blockModel.noChart() && blockModel.noDetail()) {
        throw InconsistencyException(
                "Block " + blockModel.name + " is \"NO DEATIL\" and \"NO CHART\" at the same time")
      }
      blockView = when {
        blockModel.noChart() -> {
          DBlock(this, blockModel)
        }
        blockModel.noDetail() -> {
          DGridBlock(this, blockModel)
        }
        else -> {
          DGridMultiBlock(this, blockModel)
        }
      }
    }
    return blockView
  }

  /**
   * Returns the number of pages.
   * @return The number of pages.
   */
  val pageCount: Int
    get() = vForm.pages.size

  /**
   * Returns the title of the specified page.
   * @return The title of the specified page.
   */
  fun getPageTitle(index: Int): String {
    return vForm.pages[index]
  }

  override fun reportError(e: VRuntimeException) {
    if (e.cause is VFieldException && e.message != null) {
      displayFieldError(e.cause as VFieldException)
    } else {
      super.reportError(e)
    }
  }

  /**
   * Displays a field error caused by the given exception.
   * @param fe The error cause.
   */
  fun displayFieldError(fe: VFieldException) {
    val field: VField = fe.field
    field.displayFieldError(fe.message)
  }

  /**
   * Goes to the page with index = i
   * @param i The page index.
   */
  fun gotoPage(i: Int) {
    currentPage = i
    //BackgroundThreadHandler.access(Runnable { TODO
      content.gotoPage(i)
    //})
  }

  /**
   * Releases the form.
   */
  override fun release() {
    if (vForm != null) {
      vForm.removeFormListener(this)
      for (i in blockViews.indices) {
        vForm.getBlock(i).removeBlockListener(blockListener)
      }
    }
    super.release()
  }

  /**
   * Adds a block view into a given page.
   * @param block The block view.
   * @param page The page index.
   */
  private fun addBlock(block: DBlock, page: Int) {
    if (!block.model.isInternal) {
      content.addBlock(block,
                       page,
                       block.model.isFollow,
                       block.model.noDetail())
    }
  }

  /**
   * Returns the [VForm] model.
   * @return The [VForm] model.
   */
  val vForm: VForm
    get() = super.getModel() as VForm

  override fun run() {
    vForm.prepareForm()

    // initialize the access of the blocks
    val blockcount = vForm.getBlockCount()

    for (i in 0 until blockcount) {
      vForm.getBlock(i).updateBlockAccess()
    }
    vForm.executeAfterStart()
  }

  override fun onPageSelection(page: Int) {
    if (currentPage != page) {
      performAsyncAction(object : Action("setSelectedIndex") {
        override fun execute() {
          vForm.gotoPage(page)
        }
      })
    }
  }

  override fun gotoNextPosition() {
    performAsyncAction(object : Action("gotoNextPosition") {
      override fun execute() {
        vForm.getActiveBlock()!!.gotoNextRecord()
      }
    })
  }

  override fun gotoPrevPosition() {
    performAsyncAction(object : Action("gotoPrevPosition") {
      override fun execute() {
        vForm.getActiveBlock()!!.gotoPrevRecord()
      }
    })
  }

  override fun gotoLastPosition() {
    performAsyncAction(object : Action("gotoLastPosition") {
      override fun execute() {
        vForm.getActiveBlock()!!.gotoLastRecord()
      }
    })
  }

  override fun gotoFirstPosition() {
    performAsyncAction(object : Action("gotoFirstPosition") {
      override fun execute() {
        vForm.getActiveBlock()!!.gotoFirstRecord()
      }
    })
  }

  override fun gotoPosition(posno: Int) {
    performAsyncAction(object : Action("gotoPosition") {
      override fun execute() {
        vForm.getActiveBlock()!!.gotoRecord(posno - 1)
      }
    })
  }

  override fun currentBlockChanged(oldBlock: VBlock?, newBlock: VBlock?) {
    if (oldBlock != null) {
      oldBlock.removeBlockRecordListener(blockRecordHandler)
      if (getBlockView(oldBlock) is DGridBlock) {
        (getBlockView(oldBlock) as DGridBlock?)!!.clear()
      }
    }
    if (newBlock != null) {
      newBlock.addBlockRecordListener(blockRecordHandler)
      blockRecordHandler.blockRecordChanged(newBlock.getSortedPosition(newBlock.record), newBlock.recordCount)
    }
    if (newBlock != null) {
      if (newBlock.pageNumber != currentPage) {
        gotoPage(newBlock.pageNumber)
        if (getBlockView(oldBlock) is DGridBlock) {
          (getBlockView(oldBlock) as DGridBlock?)!!.scrollToStart()
        }
      }
    }
  }

  override fun setFieldSearchOperator(op: Int) {
    // nothing to do
  }

  override fun getBlockView(block: VBlock?): UBlock? {
    val blocks: Array<VBlock> = vForm.blocks
    for (i in blocks.indices) {
      if (block == blocks[i]) {
        return blockViews[i]
      }
    }
    return null
  }

  override fun printForm(): PrintJob? {
    // TODO
    return null
  }

  override fun printSnapshot() {
    // TODO
  }

  override var runtimeDebugInfo: Throwable? = null
    private set

  override fun launchDocumentPreview(file: String) {
    val f = File(file)
    fileProduced(f, f.name)
  }

  //---------------------------------------------------
  // INNER CLASSES
  //---------------------------------------------------
  /**
   * The `BlockAccessHandler` is the [DForm]
   * implementation of the [BlockListener]
   */
  private inner class BlockAccessHandler : BlockListener {

    //---------------------------------------
    // IMPLEMENTATIONS
    //---------------------------------------
    override fun blockClosed() {}
    override fun blockChanged() {}
    override fun blockCleared() {}
    override fun blockAccessChanged(block: VBlock, newAccess: Boolean) {
      //BackgroundThreadHandler.access(Runnable { TODO
      if (pageCount == 1) {
        return
      }
      //enable/disable tab of pages
      val pageNumber = block.pageNumber
      val blocks = vForm.blocks
      if (newAccess) {
        // content.setEnabled(true, pageNumber) TODO
      } else {
        // tab is visible (another visible block there?)
        for (i in blocks.indices) {
          if (pageNumber == blocks[i].pageNumber && blocks[i].isAccessible) {
            return
          }
        }
        // content.setEnabled(false, pageNumber) TODO
      }
    }

    override fun blockViewModeLeaved(block: VBlock, actviceField: VField?) {}
    override fun blockViewModeEntered(block: VBlock, actviceField: VField?) {}
    override fun validRecordNumberChanged() {}
    override fun recordInfoChanged(rec: Int, info: Int) {}
    override fun orderChanged() {}
    override fun filterHidden() {}
    override fun filterShown() {}

    // Please don't use
    override fun getCurrentDisplay(): UBlock? = null // Please don't use
  }

  /**
   * The `BlockRecordHandler` is the [DForm]
   * implementation of the [BlockRecordListener]
   */
  private inner class BlockRecordHandler : BlockRecordListener {

    //---------------------------------------
    // IMPLEMENTATION
    //---------------------------------------
    override fun blockRecordChanged(current: Int, count: Int) {
      // TODO BackgroundThreadHandler.access(Runnable {
      content.setPosition(current, count)
      //})
    }
  }
}
