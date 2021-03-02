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
package org.kopi.galite.ui.vaadin.list

import com.vaadin.flow.data.binder.Result
import com.vaadin.flow.data.binder.ValueContext
import com.vaadin.flow.data.converter.Converter
import org.kopi.galite.list.VListColumn

/**
 * A list data model converter based on data model strings transformation.
 */
class ListConverter(private val model: VListColumn?) : Converter<String?, Any?> {

  override fun convertToModel(value: String?, context: ValueContext?): Result<Any?>? = null // not used

  override fun convertToPresentation(value: Any?, context: ValueContext?): String? = model!!.formatObject(value).toString()

  val modelType: Class<Any>
    get() = Any::class.java

  val presentationType: Class<String>
    get() = String::class.java
}
