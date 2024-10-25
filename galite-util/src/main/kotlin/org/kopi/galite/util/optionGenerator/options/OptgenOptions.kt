package org.kopi.galite.util.optionGenerator.options

import gnu.getopt.Getopt
import gnu.getopt.LongOpt

class OptgenOptions @JvmOverloads constructor(name: String? = "Optgen") : org.kopi.galite.util.base.Options(name) {
  var release: String? = null

  override fun processOption(code: Int, g: Getopt): Boolean {
    when (code) {
      'r'.code -> {
        release = getString(g, "")
        return true
      }
      else -> return super.processOption(code, g)
    }
  }

  override val options: Array<String?>
    get() {
      val parent: Array<String?> = super.options
      val total = arrayOfNulls<String>(parent.size + 1)
      System.arraycopy(parent, 0, total, 0, parent.size)
      total[parent.size + 0] = "  --release, -r<String>: Sets the release version of the program"

      return total
    }

  override val shortOptions: String
    get() = "r:" + super.shortOptions

  override fun version() {
    println("Version 2.1B released 17. July 2002")
  }

  public override fun usage() {
    System.err.println("usage: org.kopi.galite.util.optionGenerator.optgenMain [option]* [--help] <file>+")
  }

  override val longOptions: Array<LongOpt?>
    get() {
      val parent: Array<LongOpt?> = super.longOptions
      val total = arrayOfNulls<LongOpt>(parent.size + LONGOPTS.size)

      System.arraycopy(parent, 0, total, 0, parent.size)
      System.arraycopy(LONGOPTS, 0, total, parent.size, LONGOPTS.size)

      return total
    }

  companion object {
    private val LONGOPTS = arrayOf(
      LongOpt("release", LongOpt.REQUIRED_ARGUMENT, null, 'r'.code)
    )
  }
}