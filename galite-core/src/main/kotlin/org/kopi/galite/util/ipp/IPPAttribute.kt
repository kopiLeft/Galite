package org.kopi.galite.util.ipp

import java.util.*

class IPPAttribute {

  // --------------------------------------------------------------------
// CONSTRUCTORS
// --------------------------------------------------------------------
  constructor(groupTag: Int, valueTag: Int, name: String) {
    this.groupTag = groupTag
    this.valueTag = valueTag
    this.name = name
  }


  constructor(`is`: IPPInputStream, groupTag: Int) {
    var read: Byte
    var n: Int
    var endAttribute = false
    this.groupTag = groupTag
       this.valueTag = `is`.readByte().toInt() // value-tag
    n = `is`.readShort().toInt() // name-length
    this.name = `is`.readString(n) // name
    while (!endAttribute) {
      when (valueTag) {
        IPPConstants.TAG_INTEGER, IPPConstants.TAG_ENUM -> values.add(IntegerValue(`is`))
        IPPConstants.TAG_BOOLEAN -> values.add(BooleanValue(`is`))
        IPPConstants.TAG_TEXT, IPPConstants.TAG_NAME, IPPConstants.TAG_KEYWORD, IPPConstants.TAG_STRING, IPPConstants.TAG_URI, IPPConstants.TAG_URISCHEME, IPPConstants.TAG_CHARSET, IPPConstants.TAG_LANGUAGE, IPPConstants.TAG_MIMETYPE -> values!!.add(StringValue(`is`))
        IPPConstants.TAG_DATE -> values.add(DateValue(`is`))
        IPPConstants.TAG_RESOLUTION -> values.add(ResolutionValue(`is`))
        IPPConstants.TAG_RANGE -> values.add(RangeValue(`is`))
        IPPConstants.TAG_TEXTLANG, IPPConstants.TAG_NAMELANG -> {
          values.add(LangValue(`is`))
          n = `is`.readShort().toInt()
          `is`.readString(n)
        }
        else -> {
          n = `is`.readShort().toInt()
          `is`.readString(n)
        }
      }
      read = `is`.peekByte()
      if (read < IPPConstants.TAG_UNSUPPORTED_VALUE) {
        endAttribute = true
      } else {
        val nameLengthNextAttribute: Short = `is`.peekShortAfterFirstByte()
        if (nameLengthNextAttribute.toInt() == 0) {
          //additional-value
          `is`.readByte() // value-tag
          `is`.readShort() // name-length
        } else {
          endAttribute = true
        }
      }
    }
  }

// --------------------------------------------------------------------
// ACCESSORS
// --------------------------------------------------------------------

  // --------------------------------------------------------------------
// ACCESSORS
// --------------------------------------------------------------------
  fun getName(): String? {
    return name
  }

  fun getValues(): Iterator<*>? {
    return values!!.iterator()
  }

  fun getGroup(): Int {
    return groupTag
  }

  fun addValue(value: IPPValue) {
    values!!.add(value)
  }

  fun getSize(lastGroup: Int): Int {
    var size = 0
    var firstValue = true
    val vals: Iterator<*> = values!!.iterator()
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
        size += name!!.length // name
        firstValue = false
      }
      size += value.size
    }
    return size
  }

  fun write(os: IPPOutputStream, lastGroup: Int) {
    var firstValue = true
    val vals: Iterator<*> = values!!.iterator()
    var value: IPPValue

    // if it is a new group, adding a group tag
    if (lastGroup != groupTag) {
      os.writeByte(groupTag)
    }
    while (vals.hasNext()) {
      value = vals.next() as IPPValue
      os.writeByte(valueTag) // value-tag
      if (firstValue) {
        os.writeShort(name!!.length) // name-length
        os.writeString(name!!)
        firstValue = false
      } else {
        os.writeShort(0) // name-length
      }
      value.write(os)
    }
  }

  fun dump() {
    val vals: Iterator<*> = values!!.iterator()
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
    val vals: Iterator<*> = values!!.iterator()
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

  // --------------------------------------------------------------------
// DATA MEMBERS
// --------------------------------------------------------------------
  private var groupTag = 0
  private var valueTag = 0
  private var name: String? = null
  private var values = arrayListOf<Any>()
}
