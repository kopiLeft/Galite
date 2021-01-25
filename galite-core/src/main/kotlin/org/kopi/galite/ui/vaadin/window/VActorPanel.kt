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
package org.kopi.galite.ui.vaadin.window

import java.util.LinkedList

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasEnabled
import com.vaadin.flow.component.HasSize
import com.vaadin.flow.component.html.Div

import org.kopi.galite.ui.vaadin.actor.VActorsNavigationPanel
import org.kopi.galite.ui.vaadin.base.Styles

/**
 * The actor components container.
 */
class VActorPanel : Div(), HasSize {

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private val actorsContainer = Div()
  private val menu = Div()
  private val actorsPane = Div()
  private val actors = LinkedList<Component>()
  private val moreActors = VMoreActors()
  private val actorsWidths = HashMap<Component, Int>()
  private val actorsNavigationItem = VActorsRootNavigationItem()

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  init {
    className = Styles.WINDOW_VIEW_ACTORS
    this.setId("actors")
    menu.setId("menu")
    actorsContainer.className = "actors-slide-menu"
    actorsPane.add(actorsContainer)
    add(menu)
    add(actorsPane)
    menu.add(actorsNavigationItem)
  }

  //---------------------------------------------------
  // ACCESSORS
  //---------------------------------------------------
  /**
   * Adds an actor to this actor panel.
   * @param actor The actor to be added.
   */
  fun addActor(actor: Component) {
    actors.add(actor)
    actorsContainer.add(actor)
  }

  /**
   * Adds the actors menu to be shown.
   * @param panel The menu to be shown.
   */
  fun addActorsNavigationPanel(panel: VActorsNavigationPanel) {
    actorsNavigationItem.setActorsNavigationPanel(panel)
  }

  /**
   * Removes an actor from this panel.
   * @param actor The actor to be removed.
   */
  protected fun removeActor(actor: Component) {
    actors.remove(actor)
    actorsContainer.remove(actor)
  }

  /**
   * Returns the total actor width.
   * @return The total actor width.
   */
  protected fun getActorsWidth(): Int {
    if (actors == null || actors.isEmpty()) {
      return 0
    }
    var width = 0

    for (actor in actors) {
      if (actor != null) {
        width += actor.element.getAttribute("Width").toInt()
        if (!actorsWidths.containsKey(actor)) {
          actorsWidths[actor] = actor.element.getAttribute("Width").toInt()
        }
      }
    }
    return width
  }

  /**
   * Renders the actor panel according to the total actor width.
   * @param width The browser window width.
   */
  protected fun render(width: Int) {
    if (getActorsWidth() > width) {
      //TODO
    } else {
      restoreExtraActors(width)
    }
  }

  /**
   * Renders the more actors list.
   * @param width The browser window width.
   */
  protected fun <T> addMoreActors(width: Int) where T : Component?, T : HasEnabled? {
    val extraActors: LinkedList<Component> = getExtraActors(width)

    if (!extraActors.isEmpty()) {
      if (moreActors.isEmpty()) {
        for (actor in extraActors) {
          if (actor != null) {
            moreActors.addActor(actor as T)
          }
        }
      } else {
        val allActors: LinkedList<Component> = LinkedList<Component>(extraActors)

        allActors.addAll(moreActors.getActors())
        moreActors.clear()
        for (actor in allActors) {
          if (actor != null) {
            moreActors.addActor(actor as T)
          }
        }
      }
      if (!actors.contains(moreActors)) {
        addActor(moreActors)
      }
    }
  }

  /**
   * Returns the list of actors that has exceeded the total browser window width.
   * @param width The browser window width.
   * @return The of the extra actors.
   */
  protected fun getExtraActors(width: Int): LinkedList<Component> {
    val extraActors: LinkedList<Component> = LinkedList<Component>()
    // add spacing between actors.
    while (getActorsWidth() + getMoreActorsWidth() > width) {
      var actor: Component?
      actor = actors.peekLast()
      if (moreActors != null && actor === moreActors) {
        actor = if (actors.size >= 2) {
          actors[actors.size - 2]
        } else {
          null
        }
      }
      if (actor != null) {
        extraActors.addFirst(actor)
        removeActor(actor)
      }
    }
    return extraActors
  }

  /**
   * Restores the extra actors in the actor panel.
   */
  protected fun restoreExtraActors(width: Int) {
    if (actors == null || !actors.contains(moreActors)) {
      return  // no extra actors are added.
    }
    for (actor in moreActors.getActors()) {
      if (getMoreActorsWidth() + getActorsWidth() + actorsWidths[actor]!! < width) {
        // the actor should be restored
        moreActors.removeActor(actor)
        removeActor(moreActors)
        addActor(actor)
        if (moreActors.isEmpty()) {
          removeActor(moreActors)
          break
        } else {
          addActor(moreActors)
        }
      } else {
        break
      }
    }
  }

  /**
   * Returns the more item client width.
   * @return The more item client width.
   */
  protected fun getMoreActorsWidth(): Int {
    return if (actors.contains(moreActors)) moreActors.element.getAttribute("offsetWidth").toInt() else 0
  }
}
