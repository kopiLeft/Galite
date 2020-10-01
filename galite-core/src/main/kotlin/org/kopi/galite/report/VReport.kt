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

package org.kopi.galite.report

import org.kopi.galite.visual.Action
import org.kopi.galite.visual.VWindow
import java.awt.print.Printable

abstract class VReport : VWindow(), Printable {

   object TYP_PDF

   object TYP_XLSX

   object TYP_XLS

   object TYP_CSV

   fun showHelp() {
     TODO()
   }
   fun unfoldSelectedColumn(){
     TODO()
   }
   fun foldSelectedColumn(){
     TODO()
   }
   fun sortSelectedColumn(){
     TODO()
   }
   fun unfoldSelection(){
     TODO()
   }
   fun foldSelection(){
     TODO()
   }
   fun export(typPdf: Any){
     TODO()
   }
   fun close(){
     TODO()
   }
   override fun performAsyncAction(action: Action){
     TODO()
   }
   fun performAction(action: Action, block: Boolean){
     TODO()
   }
   fun createPrintJob(){
     TODO()
   }
}
