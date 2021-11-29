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

window.addAutofillListeners = function(inputField, autoFillIcon) {
   var focused = false

   autoFillIcon.setAttribute("hidden", true)

    inputField.addEventListener("focus", event => {
      focused = true
      autoFillIcon.removeAttribute("hidden") // autoFillIcon.visible = true
    })

    inputField.addEventListener("blur", event => {
      focused = false
      autoFillIcon.setAttribute("hidden", true) // autoFillIcon.visible = false
    })

    inputField.addEventListener("mouseover", event => {
      autoFillIcon.removeAttribute("hidden") // autoFillIcon.visible = true
    })

    inputField.addEventListener("mouseout", event => {
      if (!focused) {
        autoFillIcon.setAttribute("hidden", true) // autoFillIcon.visible = false
      }
    })
};
