/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
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

package org.galite.base

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.Route
import org.vaadin.examples.form.menu.menubar.GMenuBar
import java.util.*


/**
 * The entry point for all Galite applications.
 */
@Route("")
abstract class Application: VerticalLayout()  {
    abstract var menus: GMenuBar
    abstract var windows: LinkedList<Component>?

    init {
        gotoWelcomeView()
        isPadding =false
        if(UI.getCurrent() != null){
            UI.getCurrent().getElement().getStyle().set("margin", "0");
        }
        setHeightFull()
        style.set("background-color","#f2f2f2")
    }

    fun onLogin() {

    }

    fun startApplication() {

    }

    fun attachComponent(component: Component?) {

    }

    fun detachComponent(component: Component?) {

    }

    fun gotoWelcomeView() {

    }

    /**
     * Adds a window to this main window.
     * @param window The window to be added.
     */
    fun addWindow(window: Component){
        if (windows == null) {
            windows = LinkedList<Component>()
        }
        if (windows!!.isNotEmpty()){
            remove(windows!!.last)
        }
        windows!!.add(window)
        add(window)
    }

    /**
     * Removes the given window.
     * @param window The window to be removed.
     */
    fun removeWindow(window: Component) {
        windows!!.remove(window)
        remove(window)
        if (windows!!.isNotEmpty()){
            add(windows!!.last)
        }
    }
}

