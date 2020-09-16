package org.kopi.galite.util.ipp


class IPP {
  // --------------------------------------------------------------------
  // CONSTRUCTOR
  // --------------------------------------------------------------------
  constructor() {
    header = IPPHeader()
    data = ByteArray(0)
  }

  fun setData(data: ByteArray?) {
    this.data = data!!
  }

  constructor(`is`: IPPInputStream) {
    var endAttributes = false
    var groupTag: Int = IPPConstants.TAG_ZERO
    var read: Byte
    header = IPPHeader(`is`)
    data = ByteArray(0)
    while (!endAttributes) {
      read = `is`.peekByte()
      if (read.toInt() == IPPConstants.TAG_END) {
        `is`.readByte()
        endAttributes = true
      } else if (read < IPPConstants.TAG_UNSUPPORTED_VALUE) {
        // it is a new group tag
        groupTag = `is`.readByte().toInt()
      } else {
        // new attribute
        attributes.add(IPPAttribute(`is`, groupTag))
      }
    }
    data = `is`.readArray()!!
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------
  val isAnError: Boolean
    get() = header.isAnError

  val status: String?
    get() = header.status

  fun getHeader(): IPPHeader {
    return header
  }

  fun getAttributes(): Iterator<*> {
    return attributes.iterator()
  }

  fun addAttribute(attribute: IPPAttribute) {
    attributes.add(attribute)
  }

  fun setRequest(requestID: Int, operationID: Short) {
    header.setRequestID(requestID)
    header.setOperationID(operationID)
  }

  // 1 for the TAG_END
  val size: Int
    get() {
      var size: Int = header.size
      val atts: Iterator<*> = attributes.iterator()
      var lastGroup = -1
      while (atts.hasNext()) {
        val attribute = atts.next() as IPPAttribute
        size += attribute.getSize(lastGroup)
        lastGroup = attribute.getGroup()
      }
      return size + 1 + data.size // 1 for the TAG_END
    }


  fun write(os: IPPOutputStream) {
    val atts: Iterator<*> = attributes.iterator()
    var lastGroup = -1
    header.write(os)
    while (atts.hasNext()) {
      val attribute = atts.next() as IPPAttribute
      attribute.write(os, lastGroup)
      lastGroup = attribute.getGroup()
    }
    os.writeByte(IPPConstants.TAG_END)
    os.writeArray(data)
  }

  fun dump() {
    val atts: Iterator<*> = attributes.iterator()
    header.dump()
    while (atts.hasNext()) {
      val attribute = atts.next() as IPPAttribute
      attribute.dump()
    }
  }

  fun simpleDump() {
    val atts: Iterator<*> = attributes.iterator()
    while (atts.hasNext()) {
      val attribute = atts.next() as IPPAttribute
      attribute.simpleDump()
    }
  }

  // --------------------------------------------------------------------
  // DATA MEMBERS
  // --------------------------------------------------------------------
  private var header: IPPHeader
  private var attributes = mutableListOf<IPPAttribute>()
  private var data: ByteArray
  companion object {
    const val DEBUG = false
  }
}
