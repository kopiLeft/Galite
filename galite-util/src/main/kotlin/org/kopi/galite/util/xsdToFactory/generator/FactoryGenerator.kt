/*
 * Copyright (c) 2013-2024 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2024 kopiRight Managed Solutions GmbH, Wien AT
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

package org.kopi.galite.util.xsdToFactory.generator

import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.io.Writer
import java.net.URL
import java.nio.file.Paths
import java.util.*

import org.apache.xmlbeans.*
import org.apache.xmlbeans.impl.common.XmlErrorWatcher
import org.apache.xmlbeans.impl.schema.PathResourceLoader
import org.apache.xmlbeans.impl.schema.SchemaTypeSystemImpl
import org.apache.xmlbeans.impl.tool.CodeGenUtil

import org.kopi.galite.util.xsdToFactory.options.FactoryGeneratorOptions
import org.kopi.galite.util.xsdToFactory.parser.SchemaParser
import org.kopi.galite.util.xsdToFactory.writer.FactoryCodePrinter
import org.kopi.galite.util.xsdToFactory.utils.*

class FactoryGenerator {

  /**
   * Compiles a schema type system based on the provided parameters.
   *
   * @param params The parameters required for schema compilation, including source and class directories,
   *               classpath, base directory, XSD files, WSDL files, URL files, config files, and error listener.
   * @return The compiled SchemaTypeSystem if successful, or null if errors occur during compilation.
   */
  @Suppress("UNCHECKED_CAST")
  fun compile(params: Parameters): SchemaTypeSystem? {
    require(!(params.srcDir == null || params.classesDir == null)) {
      "src and class gen directories may not be null."
    }

    params.baseDir = params.baseDir ?: File(SystemProperties.getProperty("user.dir"))
    val cpResourceLoader = params.classpath?.let { PathResourceLoader(it) }
    val errorListener = XmlErrorWatcher(params.errorListener)
    params.resourceLoader = cpResourceLoader

    val start = System.currentTimeMillis()

    val system = SchemaParser().parse(params.xsdFiles,
                                      params.wsdlFiles,
                                      params.urlFiles,
                                      params.configFiles,
                                      cpResourceLoader,
                                      errorListener as MutableCollection<XmlError>,
                                      params.baseDir,
                                      params.classpath)

    if (errorListener.hasError()) {
      println("FAILED TO BUILD SCHEMA")
      return null
    }

    val finish = System.currentTimeMillis()
    println("Time to build schema type system: ${(finish - start) / 1000.0} seconds")

    return system
  }

  /**
   * Loads schema components based on the provided parameters.
   *
   * @param params The parameters required for schema compilation and component loading.
   * @return True if the schema components are successfully loaded, false otherwise.
   */
  fun loadSchemaComponents(params: Parameters): Boolean {
    val schemaTypeSystem = compile(params) ?: return false

    val documentTypes = schemaTypeSystem.documentTypes()
    val attributeTypes = schemaTypeSystem.attributeTypes()
    val globalTypes = schemaTypeSystem.globalTypes()

    globalDocuments.addAll(documentTypes)
    globalAttributes.addAll(attributeTypes)
    declaredTypes.addAll(globalTypes)

    // Load inner types for each document, attribute, and global type
    documentTypes.forEach { loadInnerTypes(it) }
    attributeTypes.forEach { loadInnerTypes(it) }
    globalTypes.forEach { loadInnerTypes(it) }

    return true
  }

  /**
   * Generates factory classes based on the provided parameters.
   *
   * @param params The parameters required for generating the factory classes, including
   *               source and class directories.
   * @throws IOException If an I/O error occurs during code generation.
   */
  @Throws(IOException::class)
  fun generateClasses(params: Parameters) {
    val start = System.currentTimeMillis()

    factories.forEach {
      printFactory(it, params.srcDir, params.classesDir)
      println("\u001B[32mFactory generated :  ${it.fullName}.kt")
    }

    val finish = System.currentTimeMillis()
    println("\u001B[35mTime to generate factory classe(s) code: " + ((finish - start).toDouble() / 1000.0) + " seconds")
  }

  /**
   * Lazily initializes and returns an array of Factory instances based on the declared types,
   * global documents, and global attributes collections.
   */
  private val factories: Array<Factory>
    get() {
      // Initialize a mutable list to hold the factories
      val factories = mutableListOf<Factory>()

      // Create a factory for declared types if there are any
      if (declaredTypes.isNotEmpty()) {
        val typesFactory = Factory(
          Constants.TYPES,
          options.name,
          options.fpackage,
          true,
          declaredTypes.toTypedArray()
        )
        factories.add(typesFactory)
      }

      // Create a factory for global documents if there are any
      if (globalDocuments.isNotEmpty()) {
        val docFactory = Factory(
          Constants.DOCUMENTS,
          options.name,
          options.fpackage,
          true,
          globalDocuments.toTypedArray()
        )
        factories.add(docFactory)
      }

      // Create a factory for global attributes if there are any
      if (globalAttributes.isNotEmpty()) {
        val attFactory = Factory(
          Constants.ATTRIBUTES,
          options.name,
          options.fpackage,
          true,
          globalAttributes.toTypedArray()
        )
        factories.add(attFactory)
      }

      return factories.toTypedArray()
    }

  @Throws(IOException::class)
  private fun printFactory(factory: Factory?, srcDir: File?, classesDir: File?) {
    val printer = FactoryCodePrinter()
    val writer: Writer
    val outputDirectory = classesDir ?: IOUtil.createSourceDir(srcDir!!.absolutePath, factory!!.packageName!!)

    val output: OutputStream = IOUtil.createFactoryStream(
      outputDirectory,
      factory!!.fullName,
      factory.fileExtension
    )
    writer = IOUtil.getFactoryWriter(output)
    printer.print(factory, writer, options.getAbstract!!)
    writer.close()
  }

  /**
   * Prints the code for the given factory to the specified source or classes directory.
   *
   * @param factory The factory for which to print the code.
   * @param srcDir The source directory where the code should be printed.
   * @param classesDir The classes directory where the code should be printed.
   * @throws IOException If an I/O error occurs during code printing.
   */
  private fun loadInnerTypes(sType: SchemaType) {
    var schemaType: SchemaType? = sType

    val redefinition = schemaType!!.name != null && schemaType.name == schemaType.baseType.name
    while (schemaType != null) {
      val anonTypes = schemaType.anonymousTypes

      anonTypes.forEach {
        if (it.isSkippedAnonymousType) {
          loadInnerTypes(it)
        } else {
          declaredTypes.add(it)
        }
      }
      if (!redefinition || (schemaType.derivationType != SchemaType.DT_EXTENSION && !schemaType.isSimpleType)) break

      schemaType = schemaType.baseType
    }
  }

  /**
   * Parse the command line
   * @param args Command line arguments
   * @return True if all necessary options are available
   */
  fun parseCommandLine(args: Array<String>): Boolean {
    if (options.parseCommandLine(args)) {
      if (options.fpackage == null) {
        System.err.println("no package mentioned for the factory")
        options.usage()
        options.printOptions()
        return false
      } else if (options.name == null) {
        System.err.println("no name mentioned for the factory")
        options.usage()
        options.printOptions()
        return false
      } else if (options.source == null) {
        System.err.println("no source directory mentioned for the factory")
        options.usage()
        options.printOptions()
      } else if (options.nonOptions.isEmpty()) {
        System.err.println("no schema files mentioned")
        options.usage()
        options.printOptions()
        return false
      }
    }

    return true
  }

  private var options: FactoryGeneratorOptions = FactoryGeneratorOptions()
  private var declaredTypes: MutableList<SchemaType> = ArrayList()
  private var globalDocuments: MutableList<SchemaType> = ArrayList()
  private var globalAttributes: MutableList<SchemaType> = ArrayList()

  companion object {
    /**
     * JVM ENTRY POINT
     *
     * @param args Program arguments
     * @throws IOException
     */
    @Throws(IOException::class)
    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    fun main(args: Array<String>) {
      val instance = FactoryGenerator()

      if (!instance.parseCommandLine(args)) return

      val classes = instance.options.directory?.let { File(it) } ?: IOUtil.createDir(IOUtil.createTempdir(), "classes")
      val src = instance.options.source?.let { File(it) } ?: IOUtil.createDir(classes, "src")

      val classpath = instance.options.classpath?.split(File.pathSeparator)?.map { File(it) }?.toTypedArray()
        ?: CodeGenUtil.systemClasspath()

      val opt = OptUtils(instance.options.nonOptions)
      val xsdFiles = opt.filesEndingWith(Constants.XSD_EXT)
      val wsdlFiles = opt.filesEndingWith(Constants.WSDL_EXT)
      val configFiles = opt.filesEndingWith(Constants.CONF_EXT)
      val urlFiles = opt.uRLs

      if (xsdFiles.isEmpty() && wsdlFiles.isEmpty()) {
        println("Could not find any xsd or wsdl files to process.")
        return
      }

      val baseURI = opt.baseDir?.toURI() ?: File("src/main/kotlin").toURI()
      val err = XmlErrorPrinter(false, baseURI)

      val params = Parameters(xsdFiles,
                              wsdlFiles,
                              configFiles,
                              urlFiles,
                              classpath,
                              src,
                              classes,
                              opt.baseDir,
                              err as MutableCollection<XmlError>?)

      if (instance.loadSchemaComponents(params)) {
        instance.generateClasses(params)
      }

      val schemaDir = Paths.get("${instance.options.directory ?: instance.options.source}/schemaorg_apache_xmlbeans")
      schemaDir.toFile().deleteRecursively()
    }
  }
}

/**
 * Represents the parameters required for schema compilation and component loading.
 *
 * @attribute xsdFiles An array of XSD files.
 * @attribute wsdlFiles An array of WSDL files.
 * @attribute configFiles An array of configuration files.
 * @attribute urlFiles An array of URL files.
 * @attribute classpath An array of classpath files.
 * @attribute srcDir The source directory.
 * @attribute classesDir The classes directory.
 * @attribute baseDir The base directory.
 * @attribute errorListener A mutable collection of XmlErrors for error handling.
 * @attribute resourceLoader The resource loader.
 */
data class Parameters(var xsdFiles: Array<File>?,
                      var wsdlFiles: Array<File>?,
                      var configFiles: Array<File>?,
                      var urlFiles: Array<URL>?,
                      var classpath: Array<File>?,
                      var srcDir: File?,
                      var classesDir: File?,
                      var baseDir: File?,
                      var errorListener: MutableCollection<XmlError>?,
                      var resourceLoader: ResourceLoader? = null)