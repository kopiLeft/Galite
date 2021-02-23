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

import java.util.Locale

import org.kopi.galite.form.VFieldUI

class DGridEditorImageField(
        columnView: VFieldUI,
        label: DGridEditorLabel?,
        align: Int,
        width: Int,
        height: Int,
        options: Int
) : DGridEditorField<Resource?>(columnView, label, align, options) {
  //---------------------------------------------------
  // IMPLEMENTATION
  //---------------------------------------------------
  fun updateText() {
    object = (model as VImageField).getImage(blockView.getRecordFromDisplayLine(position))
  }

  /**
   * Sets the object associated to record r
   * @param r The position of the record
   * @param s The object to set in
   */
  var `object`: Any?
    get() = image
    set(s) {
      //BackgroundThreadHandler.access(Runnable { TODO
        if (s != null) {
          getEditor().setImage(s as ByteArray?)
          setBlink(false)
          setBlink(true)
        }
      //})
      image = s as ByteArray?
    }

  override fun createEditor(): GridEditorImageField {
    return GridEditorImageField()
  }

  override fun createConverter(): Converter<Resource, Any> {
    return object : Converter<Resource?, Any?>() {
      val presentationType: Class<Any>
        get() = Resource::class.java
      val modelType: Class<Any>
        get() = Any::class.java

      fun convertToPresentation(value: Any?, targetType: Class<out Resource?>?, locale: Locale?): Resource? {
        return if (value == null) {
          null
        } else {
          ImageResource(ImageStreamSource(value as ByteArray?))
        }
      }

      fun convertToModel(value: Resource?, targetType: Class<out Any?>?, locale: Locale?): Any? {
        return if (value == null) {
          null
        } else {
          (value as GridEditorImageField.ImageResource).getImage()
        }
      }
    }
  }

  override fun createRenderer(): Renderer<Resource> {
    return object : ImageRenderer() {
      fun encode(resource: Resource?): JsonValue? {
        if (resource == null) {
          return null
        }
        val key = "key" + keyCounter++
        val reference: ResourceReference = ResourceReference.create(resource, UI.getCurrent(), key)
        (UI.getCurrent() as VApplication).getState().resources.put(key, reference)
        return encode(reference, URLReference::class.java)
      }
    }
  }

  override fun getEditor(): GridEditorImageField {
    return super.getEditor() as GridEditorImageField
  }

  //---------------------------------------------------
  // DATA MEMBERS
  //---------------------------------------------------
  private var image: ByteArray?

  companion object {
    private var keyCounter = 0
  }

  //---------------------------------------------------
  // CONSTRUCTOR
  //---------------------------------------------------
  init {
    getEditor().setImageWidth(width)
    getEditor().setImageHeight(height)
  }
}