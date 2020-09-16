package org.kopi.galite.util.ipp

abstract class IPPValue {
  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------
  abstract val size: Int
  abstract fun write(os: IPPOutputStream)
  abstract fun dump()
}
