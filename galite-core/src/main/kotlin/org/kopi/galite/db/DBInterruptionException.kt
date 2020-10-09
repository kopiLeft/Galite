package org.kopi.galite.db

import java.sql.SQLException

class DBInterruptionException : DBException {
  constructor() : super(SQLException("DBInterruptionException"))

  constructor(query: String?) : super(query, SQLException("DBInterruptionException"))
}
