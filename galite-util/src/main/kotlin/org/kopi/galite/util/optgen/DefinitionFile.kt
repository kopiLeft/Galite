/*
 * Copyright (c) 2013-2026 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2026 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.util.optgen

import java.io.File
import java.io.PrintWriter

import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.input.SAXBuilder

import org.kopi.galite.util.base.InconsistencyException

/**
 * Constructs a  definition file
 */
internal class DefinitionFile(private val sourceFile: String,
                              private val fileHeader: String?,
                              private val packageName: String,
                              private val parent: String?,
                              private val prefix: String,
                              private var version: String?,
                              private val usage: String?,
                              private val definitions: List<OptionDefinition>)
{
  companion object {
    /**
     * Reads and parses a token definition file
     *
     * @param   sourceFile      the name of the source file
     * @return  a class info structure holding the information from the source
     *
     */
    fun read(sourceFile: String): DefinitionFile {
      val document: Document

      try {
        document = SAXBuilder().build(File(sourceFile))
      } catch (e: Exception) {
        throw InconsistencyException("Cannot load file $sourceFile: ${e.message}")
      }
      val root = document.rootElement

      return DefinitionFile(sourceFile,
                            root.getAttributeValue("fileHeader"),
                            root.getAttributeValue("package"),
                            root.getAttributeValue("parent"),
                            root.getAttributeValue("prefix"),
                            root.getAttributeValue("version"),
                            root.getAttributeValue("usage"),
                            getOptions(root)
      )
    }

    /**
     * Reads options from the xml definition file
     *
     * @param     element      the xml root element
     * @return    a class info structure holding the information from the source
     */
    private fun getOptions(element: Element): List<OptionDefinition> {
      val params = element.getChildren("param")

      return params.map { current ->
        val type = current.getAttributeValue("type")
        val arg = current.getAttributeValue("optionalDefault") ?: if (type != "boolean") "" else null

        OptionDefinition(current.getAttributeValue("longname"),
                         current.getAttributeValue("shortname"),
                         type,
                         !current.getAttributeValue("multiple").isNullOrBlank(),
                         current.getAttributeValue("default"),
                         arg,
                         current.getAttributeValue("help"))
      }
    }
  }

  // --------------------------------------------------------------------
  // ACCESSORS
  // --------------------------------------------------------------------

  /**
   * Sets the version. Overrides the version supplied in the definitions file.
   */
  fun setVersion(version: String?) {
    this.version = version
  }

  /**
   * Returns the literal prefix
   */
  fun getPrefix(): String {
    return prefix
  }

  val className: String
    get() = "$packageName.${prefix}Options"

  // --------------------------------------------------------------------
  // CHECK OPERATIONS
  // --------------------------------------------------------------------

  /**
   * Checks for duplicate identifiers.
   */
  fun checkIdentifiers() {
    val identifiers: HashMap<String, String> = hashMapOf()

    definitions.forEach { it.checkIdentifiers(identifiers, sourceFile) }
  }

  /**
   * Checks for duplicate shortcuts.
   */
  fun checkShortcuts() {
    val shortcuts: HashMap<String, String> = hashMapOf()

    definitions.forEach { it.checkShortcuts(shortcuts, sourceFile) }
  }

  // --------------------------------------------------------------------
  // PRINT OPERATIONS
  // --------------------------------------------------------------------

  /**
   * Generates the option parser in a java class.
   *
   * @param    out        the output stream
   */
  fun printJavaFile(out: PrintWriter) {
    if (!fileHeader.isNullOrBlank()) {
      out.println(fileHeader)
    }
    out.print("// Generated by optgen from $sourceFile")
    out.println()
    out.println("package $packageName;")
    out.println()
    out.println("import gnu.getopt.Getopt;")
    out.println("import gnu.getopt.LongOpt;")
    out.println()
    out.print("public class " + prefix + "Options")
    out.print(if (parent == null) "" else " extends $parent")
    out.println(" {")

    // CONSTRUCTORS
    out.println()
    out.println("  public " + prefix + "Options(String name) {")
    out.println("    super(name);")
    out.println("  }")
    out.println()
    out.println("  public " + prefix + "Options() {")
    out.println("    this(\"$prefix\");")
    out.println("  }")
    out.println()

    // FIELDS
    definitions.forEach {
      it.printJavaFields(out)
    }

    // PROCESSOPTION
    out.println()
    out.println("  public boolean processOption(int code, Getopt g) {")
    out.println("    switch (code) {")
    definitions.forEach {
      it.printJavaParseArgument(out)
    }
    out.println("    default:")
    out.println("      return super.processOption(code, g);")
    out.println("    }")
    out.println("  }")


    // GETOPTIONS
    out.println()
    out.println("  public String[] getOptions() {")
    out.println("    String[]	parent = super.getOptions();")
    out.println("    String[]	total = new String[parent.length + " + definitions.size + "];")
    out.println("    System.arraycopy(parent, 0, total, 0, parent.length);")

    definitions.forEachIndexed { index, definition ->
      out.print("    total[parent.length + $index] = ")
      definition.printJavaUsage(out)
      out.println(";")
    }

    out.println("    ")
    out.println("    return total;")
    out.println("  }")

    // GETSHORTOPTIONS
    out.println("\n")
    out.println("  public String getShortOptions() {")
    out.print("    return \"")
    definitions.forEach {
      it.printShortOption(out)

    }
    out.println("\" + super.getShortOptions();")
    out.println("  }")

    // VERSION
    out.println("\n")
    out.println("  public void version() {")
    out.print("    System.out.println(")
    out.print(if (version == null) "" else "\"" + version + "\"")
    out.println(");")
    out.println("  }")

    // USAGE
    out.println("\n")
    out.println("  public void usage() {")
    if (usage != null) {
      out.print("    System.err.println(")
      out.print("\"" + usage + "\"")
      out.println(");")
    }
    out.println("  }")

    // GETLONGOPTIONS
    out.println()
    out.println("  public LongOpt[] getLongOptions() {")
    out.println("    LongOpt[]	parent = super.getLongOptions();")
    out.println("    LongOpt[]	total = new LongOpt[parent.length + LONGOPTS.length];")
    out.println("    ")
    out.println("    System.arraycopy(parent, 0, total, 0, parent.length);")
    out.println("    System.arraycopy(LONGOPTS, 0, total, parent.length, LONGOPTS.length);")
    out.println("    ")
    out.println("    return total;")
    out.println("  }")

    // LONGOPTS
    out.println()
    out.println("  private static final LongOpt[] LONGOPTS = {")
    definitions.forEachIndexed { index, definition ->
      if (index != 0) { out.println(",") }
      definition.printJavaLongOpts(out)
    }
    out.println()
    out.println("  };")

    out.println("}")
  }
}
