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

package org.kopi.galite.util.ipp

class IPPAttribute(var groupTag: Int, var valueTag: Int, var name: String) {

  // --------------------------------------------------------------------
// CONSTRUCTORS
// --------------------------------------------------------------------
  constructor(iPPInputStream: IPPInputStream, groupTag: Int) : this(groupTag,
          iPPInputStream.readByte().toInt(),
          iPPInputStream.readString(iPPInputStream.readShort().toInt())!!) {
    var read: Byte
    var n: Int
    var endAttribute = false
    while (!endAttribute) {
      when (valueTag) {
        IPPConstants.TAG_INTEGER, IPPConstants.TAG_ENUM -> values.add(IntegerValue(iPPInputStream))
        IPPConstants.TAG_BOOLEAN -> values.add(BooleanValue(iPPInputStream))
        IPPConstants.TAG_TEXT, IPPConstants.TAG_NAME, IPPConstants.TAG_KEYWORD, IPPConstants.TAG_STRING, IPPConstants.TAG_URI, IPPConstants.TAG_URISCHEME, IPPConstants.TAG_CHARSET, IPPConstants.TAG_LANGUAGE, IPPConstants.TAG_MIMETYPE -> values.add(StringValue(iPPInputStream))
        IPPConstants.TAG_DATE -> values.add(DateValue(iPPInputStream))
        IPPConstants.TAG_RESOLUTION -> values.add(ResolutionValue(iPPInputStream))
        IPPConstants.TAG_RANGE -> values.add(RangeValue(iPPInputStream))
        IPPConstants.TAG_TEXTLANG, IPPConstants.TAG_NAMELANG -> {
          values.add(LangValue(iPPInputStream))
          n = iPPInputStream.readShort().toInt()
          iPPInputStream.readString(n)
        }
        else -> {
          n = iPPInputStream.readShort().toInt()
          iPPInputStream.readString(n)
        }
      }
      read = iPPInputStream.peekByte()
      when {
        read < IPPConstants.TAG_UNSUPPORTED_VALUE -> {
          endAttribute = true
        }
        else -> {
          val nameLengthNextAttribute: Short = iPPInputStream.peekShortAfterFirstByte()
          when {
            nameLengthNextAttribute.toInt() == 0 -> {
              //additional-value
              iPPInputStream.readByte() // value-tag
              iPPInputStream.readShort() // name-length
            }
            else -> {
              endAttribute = true
            }
          }
        }
      }
    }
  }
  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  fun addValue(value: IPPValue) {
    values.add(value)
  }

  fun getSize(lastGroup: Int): Int {
    var size = 0
    var firstValue = true
    val vals: Iterator<*> = values.iterator()
    var value: IPPValue

    // if it is a new group, adding a group tag
    if (lastGroup != groupTag) {
      size++
    }
    while (vals.hasNext()) {
      value = vals.next() as IPPValue
      size++ // value-tag
      size += 2 // name-length
      if (firstValue) {
        size += name.length // name
        firstValue = false
      }
      size += value.getSize()
    }
    return size
  }

  fun write(os: IPPOutputStream, lastGroup: Int) {
    var firstValue = true
    val vals: Iterator<*> = values.iterator()
    var value: IPPValue

    // if it is a new group, adding a group tag
    if (lastGroup != groupTag) {
      os.writeByte(groupTag)
    }
    while (vals.hasNext()) {
      value = vals.next() as IPPValue
      os.writeByte(valueTag) // value-tag
      if (firstValue) {
        os.writeShort(name.length) // name-length
        os.writeString(name)
        firstValue = false
      } else {
        os.writeShort(0) // name-length
      }
      value.write(os)
    }
  }

  fun dump() {
    val vals = values.iterator()
    var value: IPPValue
    println("")
    println("Group Tag : $groupTag")
    println("Value Tag : $valueTag")
    println("Att Name : $name")
    println("Values :")
    while (vals.hasNext()) {
      value = vals.next() as IPPValue
      value.dump()
    }
  }

  fun simpleDump() {
    val vals: Iterator<*> = values.iterator()
    var value: IPPValue
    print("$name = ")
    while (vals.hasNext()) {
      value = vals.next() as IPPValue
      print(value.toString())
      if (vals.hasNext()) {
        print(", ")
      }
    }
    println()
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------
  var values = arrayListOf<Any>()
}
