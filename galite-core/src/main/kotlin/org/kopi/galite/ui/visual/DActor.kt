/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id: DActor.java 35180 2017-07-14 16:31:39Z hacheni $
 */
package org.kopi.galite.ui.visual

import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Span
import org.kopi.galite.visual.UActor
import org.kopi.galite.visual.VActor
import java.awt.event.KeyEvent

/**
 * The `DActor` is the vaadin implementation of
 * the [UActor]. The actor can be represented by a [Button]
 * if it has a valid icon name.
 *
 *
 *
 * The actor action is handled by a [ShortcutListener] registered
 * of the [DWindow] which is the receiver of all actors actions.
 *
 */
class DActor(private var model: VActor) : Span(), UActor {
  // --------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------
  override fun setModel(model: VActor) {
    this.model = model
  }

  override fun getModel(): VActor {
    return model
  }

  override fun isEnabled(): Boolean {
    return super.isEnabled()
  }

  override fun setEnabled(enabled: Boolean) {
    super.setEnabled(enabled)
  }

  override fun isVisible(): Boolean {
    return super.isVisible()
  }

  override fun setVisible(visible: Boolean) {
    super.setVisible(visible)
  }

  companion object {
    // --------------------------------------------------
    // PRIVATE METHODS
    // --------------------------------------------------
    /**
     * Creates the actor description.
     * @param model The actor model.
     * @return The actor description.
     */
    private fun getDescription(model: VActor): String? {
      return if (model.acceleratorKey > 0) {
        if (model.acceleratorModifier == 0) {
          model.help + " [" + KeyEvent.getKeyText(model.acceleratorKey) + "]"
        } else {
          model.help + " [" + KeyEvent.getKeyModifiersText(model.acceleratorModifier) + "-" + KeyEvent.getKeyText(model.acceleratorKey) + "]"
        }
      } else {
        model.help
      }
    }

    /**
     * Returns the corrected accelerator key.
     * @param acceleratorKey The original accelerator key.
     * @return The corrected accelerator key.
     */
    private fun correctAcceleratorKey(acceleratorKey: Int): Int {
      return if (acceleratorKey == 10) 13 else acceleratorKey
    }

    /**
     * Returns the corrected modifier accelerator key.
     * @param acceleratorModifier The original modifier accelerator key.
     * @return The corrected modifier accelerator key.
     */
    private fun correctAcceleratorModifier(acceleratorModifier: Int): Int {
      TODO()
    }
  }
  // --------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------
  /**
   * Creates the visible actor component from a given model.
   * @param model The **not null** model.
   */
  init {
    isEnabled = false

    val label = Span(model.actorIdent)
    add(label)


    model.setDisplay(this)
  }
}
