/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
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

class IPPAttribute {

  var group: Int
    private set

  private var valueTag: Int

  var name: String
    private set

  private var values: MutableList<IPPValue>

  // --------------------------------------------------------------------
  // CONSTRUCTORS
  // --------------------------------------------------------------------

  constructor(groupTag: Int, valueTag: Int, name: String) {
    group = groupTag
    this.valueTag = valueTag
    this.name = name
    values = mutableListOf()
  }

  constructor(inputStream: IPPInputStream, groupTag: Int) {
    var read: Byte
    var n = inputStream.readShort().toInt() // name-length
    var endAttribute = false

    group = groupTag
    values = mutableListOf()
    valueTag = inputStream.readByte().toInt() // value-tag
    name = inputStream.readString(n) // name

    while (!endAttribute) {
      when (valueTag) {
        IPPConstants.TAG_INTEGER, IPPConstants.TAG_ENUM -> values.add(IntegerValue(inputStream))

        IPPConstants.TAG_BOOLEAN -> values.add(BooleanValue(inputStream))

        IPPConstants.TAG_TEXT,
        IPPConstants.TAG_NAME,
        IPPConstants.TAG_KEYWORD,
        IPPConstants.TAG_STRING,
        IPPConstants.TAG_URI,
        IPPConstants.TAG_URISCHEME,
        IPPConstants.TAG_CHARSET,
        IPPConstants.TAG_LANGUAGE,
        IPPConstants.TAG_MIMETYPE -> values.add(StringValue(inputStream))

        IPPConstants.TAG_DATE -> values.add(DateValue(inputStream))

        IPPConstants.TAG_RESOLUTION -> values.add(ResolutionValue(inputStream))

        IPPConstants.TAG_RANGE -> values.add(RangeValue(inputStream))

        IPPConstants.TAG_TEXTLANG, IPPConstants.TAG_NAMELANG -> {
          values.add(LangValue(inputStream))
          n = inputStream.readShort().toInt()
          inputStream.readString(n)
        }
        else -> {
          n = inputStream.readShort().toInt()
          inputStream.readString(n)
        }
      }
      read = inputStream.peekByte()
      if (read < IPPConstants.TAG_UNSUPPORTED_VALUE) {
        endAttribute = true
      } else {
        val nameLengthNextAttribute = inputStream.peekShortAfterFirstByte()

        if (nameLengthNextAttribute.toInt() == 0) {
          //additional-value
          inputStream.readByte() // value-tag
          inputStream.readShort() // name-length
        } else {
          endAttribute = true
        }
      }
    }
  }

  fun getValues(): Iterator<*> = values.iterator()

  fun addValue(value: IPPValue) {
    values.add(value)
  }

  fun getSize(lastGroup: Int): Int {
    var size = 0
    var firstValue = true
    val vals = values.iterator()
    var value: IPPValue

    // if it is a new group, adding a group tag
    if (lastGroup != group) {
      size++
    }
    while (vals.hasNext()) {
      value = vals.next()

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
    val vals = values.iterator()
    var value: IPPValue

    // if it is a new group, adding a group tag
    if (lastGroup != group) {
      os.writeByte(group)
    }
    while (vals.hasNext()) {
      value = vals.next()

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
    println("Group Tag : $group")
    println("Value Tag : $valueTag")
    println("Att Name : $name")
    println("Values :")

    while (vals.hasNext()) {
      value = vals.next()
      value.dump()
    }
  }

  fun simpleDump() {
    val vals = values.iterator()
    var value: IPPValue

    print("$name = ")

    while (vals.hasNext()) {
      value = vals.next()
      print(value.toString())
      if (vals.hasNext()) {
        print(", ")
      }
    }
    println()
  }
}
