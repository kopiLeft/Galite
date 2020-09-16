package org.kopi.galite.util.ipp

class IntegerValue : IPPValue {
  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------
  // value-length + value
  override val size: Int
    get() {
      return 2 + 4
    }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------
  private var value = 0

  // --------------------------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------------------------
  constructor(value: Int) {
    this.value = value
  }

  constructor(`is`: IPPInputStream) {
    `is`.readShort() //value-length
    value = `is`.readInteger() //value
  }

  override fun write(os: IPPOutputStream) {
    os.writeShort(4)
    os.writeInteger(value)
  }

  override fun dump() {
    println("\tint : $value")
  }

  override fun toString(): String = value.toString()
}
