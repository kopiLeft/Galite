// Generated by optgen from ConnectionOptions.xml
package org.kopi.galite.visual.db

import org.kopi.galite.visual.util.base.Options

import gnu.getopt.Getopt
import gnu.getopt.LongOpt

open class ConnectionOptions @JvmOverloads constructor(name: String = "Connection") : Options(name) {
  @JvmField
  var database: String? = null

  @JvmField
  var driver: String? = null

  @JvmField
  var username: String? = null

  @JvmField
  var password: String? = null

  @JvmField
  var lookupUserId = true

  @JvmField
  var trace = 0
  var properties: Array<String?>? = null

  @JvmField
  var schema: String? = null

  override fun processOption(code: Int, g: Getopt): Boolean {
    return when (code.toChar()) {
      'b' -> {
        database = getString(g, "")
        true
      }
      'd' -> {
        driver = getString(g, "")
        true
      }
      'u' -> {
        username = getString(g, "")
        true
      }
      'p' -> {
        password = getString(g, "")
        true
      }
      'U' -> {
        lookupUserId = !true
        true
      }
      't' -> {
        trace = getInt(g, 1)
        true
      }
      'q' -> {
        properties = addString(properties, getString(g, ""))
        true
      }
      's' -> {
        schema = getString(g, "")
        true
      }
      else -> super.processOption(code, g)
    }
  }

  override val options: Array<String?>
    get() {
      val parent = super.options
      val total = arrayOfNulls<String>(parent.size + 8)
      System.arraycopy(parent, 0, total, 0, parent.size)
      total[parent.size + 0] = "  --database, -b<String>: The URL of the database"
      total[parent.size + 1] = "  --driver, -d<String>: The JDBC driver to use to access the database"
      total[parent.size + 2] = "  --username, -u<String>: The username for the database"
      total[parent.size + 3] = "  --password, -p<String>: The password for the database"
      total[parent.size + 4] = "  --lookupUserId, -U:   Lookup user ID in database? [true]"
      total[parent.size + 5] = "  --trace, -t<int>:     Set the trace level to print database queries before execution (0: none, 1: all but FETCH, 2: all) [0]"
      total[parent.size + 6] = "  --properties, -q<String>: These properties override or complete the properties stored in the database."
      total[parent.size + 7] = "  --schema, -s<String>: The current database schema to be set."
      return total
    }

  override val shortOptions: String
    get() = "b:d:u:p:Ut::q:s:" + super.shortOptions

  public override fun version() {
    println("Version 2.3B released 17 September 2007")
  }

  public override fun usage() {
    System.err.println("usage: Main [option]*")
  }

  override val longOptions: Array<LongOpt?>
    get() {
      val parent = super.longOptions
      val total = arrayOfNulls<LongOpt>(parent.size + LONGOPTS.size)
      System.arraycopy(parent, 0, total, 0, parent.size)
      System.arraycopy(LONGOPTS, 0, total, parent.size, LONGOPTS.size)
      return total
    }

  companion object {
    private val LONGOPTS = arrayOf(
            LongOpt("database", LongOpt.REQUIRED_ARGUMENT, null, 'b'.toInt()),
            LongOpt("driver", LongOpt.REQUIRED_ARGUMENT, null, 'd'.toInt()),
            LongOpt("username", LongOpt.REQUIRED_ARGUMENT, null, 'u'.toInt()),
            LongOpt("password", LongOpt.REQUIRED_ARGUMENT, null, 'p'.toInt()),
            LongOpt("lookupUserId", LongOpt.NO_ARGUMENT, null, 'U'.toInt()),
            LongOpt("trace", LongOpt.OPTIONAL_ARGUMENT, null, 't'.toInt()),
            LongOpt("properties", LongOpt.REQUIRED_ARGUMENT, null, 'q'.toInt()),
            LongOpt("schema", LongOpt.REQUIRED_ARGUMENT, null, 's'.toInt())
    )
  }
}
