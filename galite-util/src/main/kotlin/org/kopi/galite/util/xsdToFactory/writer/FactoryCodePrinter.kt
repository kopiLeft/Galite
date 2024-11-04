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

package org.kopi.galite.util.xsdToFactory.writer

import java.io.IOException
import java.io.Writer
import java.math.BigInteger
import java.nio.charset.Charset
import java.util.*

import kotlin.math.absoluteValue

import org.apache.xmlbeans.SchemaParticle
import org.apache.xmlbeans.SchemaProperty
import org.apache.xmlbeans.SchemaType
import org.apache.xmlbeans.impl.common.NameUtil

import org.kopi.galite.util.base.Utils
import org.kopi.galite.util.base.Utils.Companion.nonKotlinKeyword
import org.kopi.galite.util.base.Utils.Companion.singleOrEmpty
import org.kopi.galite.util.xsdToFactory.utils.Constants
import org.kopi.galite.util.xsdToFactory.utils.Factory
import org.kopi.galite.util.xsdToFactory.parser.SchemaParser.Companion.getDigits

class FactoryCodePrinter: Constants {
  private var writer: Writer? = null

  /**
   * Prints the factory code.
   * @param factory The Factory object to print.
   * @param writer The Writer object to write the generated code.
   * @param getAbstract If true, includes abstract types.
   * @throws IOException If an I/O error occurs.
   */
  @Throws(IOException::class)
  fun print(factory: Factory,
            writer: Writer?,
            getAbstract: Boolean) {
    this.writer = writer

    extractClasseAttributes(factory, getAbstract)
    printTopComment(factory.name!!, factory.isPrintHeader!!)
    printPackage(factory.packageName!!)
    printImports(factory.packageName!!)
    printFactory(factory)
  }

  /**
   * Extracts class attributes from the factory schema.
   * @param factory The Factory object of the schema.
   * @param getAbstract If true, includes abstract types.
   */
  fun extractClasseAttributes(factory: Factory, getAbstract: Boolean) {
    val schemaTypes: Array<SchemaType> = factory.content

    for (schemaType in schemaTypes) {
      if (!getAbstract && schemaType.isAbstract) {
        continue
      }
      val properties = getAllSeenProperties(schemaType)

      if (properties.isNotEmpty()) {
        val classeFactory = ClassFactory(className = schemaType.shortJavaName,
                                         returnType = schemaType.fullJavaName.split(".").last().replace('$', '.'),
                                         hasChoiceBloc = hasChoiceBloc(schemaType),
                                         firstLigneComment = getFirstLigneComment(schemaType),
                                         javaPackage = schemaType.fullJavaName.replace('$', '.'))

        properties.forEach { propertie ->
          val attributeName = NameUtil.lowerCamelCase(propertie.javaPropertyName).nonKotlinKeyword()
          val comment = propertie.name.localPart + (if (propertie.isAttribute) " attribute"
            else " element" + (if (propertie.extendsJavaArray()) " Array" else ""))

          val attribute = Attribute(name = attributeName,
                                    type = getKotlinTypeForProperty(propertie.type, factory.packageName!!),
                                    isList = propertie.extendsJavaArray(),
                                    defaultValue = propertie.defaultValue?.stringValue ?: "null",
                                    isElement = !propertie.isAttribute,
                                    isReserved = Utils.isKotlinReservedWord(NameUtil.lowerCamelCase(propertie.javaPropertyName)),
                                    isCalendarAttribute = propertie.javaTypeCode == SchemaProperty.JAVA_CALENDAR,
                                    Required = if (!propertie.isAttribute) propertie.minOccurs != BigInteger.ZERO
                                      else !propertie.extendsJavaOption(),
                                    commentName = "// $comment",
                                    simpleType = if (propertie.type.isSimpleType && !propertie.type.fullJavaName.startsWith("org.apache.xmlbeans"))
                                      propertie.type.fullJavaName.split(".").last().replace("$", ".") else "",
                                    hasStringEnumValues = propertie.type.isSimpleType && propertie.type.hasStringEnumValues())

          if (attribute.hasStringEnumValues) {
            val basePackagePath = if (!propertie.type.fullJavaName.startsWith("org.apache.xmlbeans"))
              propertie.type.fullJavaName.substringBeforeLast(".")
            else Constants.EMPTY
            val missedPath = "$basePackagePath.${attribute.simpleType}"
            if (basePackagePath != Constants.EMPTY && !missedPath.startsWith(classeFactory.javaPackage))
              importFactory.add(missedPath)
          }
          if (attribute.type == "BigDecimal" && attribute.defaultValue != "null")
            attribute.defaultValue = getDefaultBigDecimal(attribute.defaultValue)
          classeFactory.attributes.add(attribute)
        }
        classesFactory.add(classeFactory)
        importFactory.add(classeFactory.javaPackage)
        if (classeFactory.hasChoiceBloc) importFactory.addAll(
          listOf("org.apache.xmlbeans.XmlObject",
          classeFactory.javaPackage.substringBeforeLast('.')))
      }
    }
  }

  /**
   * Converts a default value to a corresponding BigDecimal constant.
   *
   * @param default The default value as a string.
   * @return The BigDecimal representation of the default value.
   */
  private fun getDefaultBigDecimal(default: String): String {
    return when (default) {
      "0" -> "BigDecimal.ZERO"
      "1" -> "BigDecimal.ONE"
      else -> "BigDecimal($default)"
    }
  }

  /**
   * Checks if the provided schema type contains a choice block.
   *
   * @param schemaType The schema type to check.
   * @return True if the schema type contains a choice block, false otherwise.
   */
  private fun hasChoiceBloc(schemaType: SchemaType): Boolean {
    return containsChoice(schemaType.contentModel)
  }

  /**
   * Recursively checks if a schema particle contains a choice block.
   *
   * @param particle The schema particle to check.
   * @return True if the particle or any of its children contains a choice block, false otherwise.
   */
  private fun containsChoice(particle: SchemaParticle?): Boolean {
    particle ?: return false
    if (particle.particleType == SchemaParticle.CHOICE) {
      return true
    }
    particle.particleChildren?.forEach { child ->
      if (containsChoice(child)) {
        return true
      }
    }

    return false
  }

  /**
   *
   * Add imports used by ToCalendar function.
   */
  private fun addToCalendarImports() {
    importFactory.addAll(listOf("java.util.Calendar",
                                "java.util.GregorianCalendar",
                                "java.time.temporal.Temporal",
                                "java.time.LocalDateTime",
                                "java.time.LocalTime",
                                "java.time.LocalDate"))
  }

  /**
   *
   * add ToCalendar function to the factory.
   */
  private fun addToCalendarFunction() {
    emit("  /**\n" +
        "   *\n" +
        "   * Convert Temporal to Calendar.\n" +
        "   */\n" +
        "   @Throws(kotlin.Exception::class)\n" +
        "   private fun Temporal.toCalendar(): Calendar {\n" +
        "     val calendar = GregorianCalendar()\n" +
        "\n" +
        "     calendar.clear()\n" +
        "     when (this) {\n" +
        "       is LocalDate     -> {\n" +
        "         calendar[Calendar.YEAR] = this.year\n" +
        "         calendar[Calendar.MONTH] = this.monthValue.minus(1)\n" +
        "         calendar[Calendar.DAY_OF_MONTH] = this.dayOfMonth\n" +
        "       }\n" +
        "       is LocalTime     -> {\n" +
        "         calendar[Calendar.HOUR_OF_DAY] = this.hour\n" +
        "         calendar[Calendar.MINUTE] = this.minute\n" +
        "         calendar[Calendar.SECOND] = this.second\n" +
        "       }\n" +
        "       is LocalDateTime -> {\n" +
        "         calendar[Calendar.YEAR] = this.year\n" +
        "         calendar[Calendar.MONTH] = this.monthValue.minus(1)\n" +
        "         calendar[Calendar.DAY_OF_MONTH] = this.dayOfMonth\n" +
        "         calendar[Calendar.HOUR_OF_DAY] = this.hour\n" +
        "         calendar[Calendar.MINUTE] = this.minute\n" +
        "         calendar[Calendar.SECOND] = this.second\n" +
        "       }\n" +
        "       else             -> {\n" +
        "         // Ne rien faires\n" +
        "       }\n" +
        "     }\n" +
        "\n" +
        "     return calendar\n" +
        "   }", true)
  }

  /**
   * Adds a comment to the create fonction.
   */
  private fun addCommentFunction(classFactory: ClassFactory) {
    emit("  /**", true)
    emit(classFactory.firstLigneComment, true)
    emit("   *", true)
    emit("   * This is a complex type.", true)
    emit("   *", true)
    classFactory.attributes.forEach{ emit("   * @param ${it.name}", true) }
    emit("   * @return A new `${classFactory.javaPackage}` XML instance", true)
    emit("   */", true)
  }

  /**
   * Adds the body of the crate fonction.
   */
  private fun addBodyFunction(classFactory: ClassFactory) {
    val missedPath = if(!importFactory.contains(classFactory.javaPackage)) classFactory.javaPackage.substringBeforeLast(".") + "." else ""
    val functionDeclaration = "${indentation(1)}fun create${classFactory.className}("

    emit(functionDeclaration, false)
    classFactory.attributes.forEachIndexed { index, attribute ->
      val indent = " ".repeat(if (index == 0) 0 else functionDeclaration.length)
      val name = attribute.name + (if (attribute.isList) "Array" else "")
      val type = (if (attribute.isList) "Array<" else "") +
        attribute.type +
        (if (attribute.isList) ">" else "") +
        (if (!attribute.Required) {
          if (!"String".equals(attribute.type) || attribute.defaultValue == "null")
            "? = ${attribute.defaultValue}" else "? = \"${attribute.defaultValue}\""
        } else "") +
        (if (index == classFactory.attributes.size-1) ")" else ",")

      emit("$indent$name: $type  ${attribute.commentName}", true)
    }
    emit("${indentation(2)}: $missedPath${classFactory.className.capitalize()}", true)
    emit("${indentation(1)}{", true)
    emit("${indentation(2)}val new${classFactory.className}: $missedPath${classFactory.className} = $missedPath${classFactory.className}.Factory.newInstance()\n", true)
    classFactory.attributes.forEach{ attribute ->
      val parameterName = attribute.name + if (attribute.isList) "Array" else ""
      val calendar = if (attribute.isCalendarAttribute) ".toCalendar()" else ""
      val array = if (attribute.isList && attribute.type == "Int") ".toIntArray()" else ""
      val value = if (attribute.hasStringEnumValues && attribute.simpleType.isNotEmpty()) attribute.simpleType + ".Enum.forString($parameterName$calendar$array)" else "$parameterName$calendar$array"
      val attributeName = when {
        attribute.isReserved && !attribute.isList -> "`${parameterName.substring(1)}`"
        attribute.isReserved && attribute.isList -> parameterName.substring(1)
        else -> parameterName
      }
      if(attribute.Required) {
        emit("${indentation(2)}new${classFactory.className}.$attributeName = $value", true)
      } else {
        if(!"String".equals(attribute.type) || attribute.isList) {
          emit("${indentation(2)}$parameterName?.let { new${classFactory.className}.$attributeName = $value }", true)
        } else {
          emit("${indentation(2)}if (!$parameterName.isNullOrBlank()) {", true)
          emit("${indentation(3)}new${classFactory.className}.$attributeName = $value", true)
          emit("${indentation(2)}}", true)
        }
      }
    }
    emit("\n${indentation(2)}return new${classFactory.className}", true)
    emit("${indentation(1)}}\n", true)
  }

  /**
   * Adds import bloc to the beginning of the generated factory.
   */
  private fun printImports(packageName: String) {
    addToCalendarImports()
    if (importFactory.isNotEmpty()) {
      val groupedPackages = importFactory.distinct()
        .sortedWith(compareBy({ it.startsWith("com") }, { it.startsWith("org") }, { it }))
        .groupBy { it.substringBeforeLast(".") }
      val classNames = mutableSetOf<String>()

      importFactory.clear()
      groupedPackages.forEach { (_, subPackages) ->
        subPackages.forEach { importPath ->
          val className = importPath.split(".").last()

          if (classNames.contains(className)) {
            val currentImportForClass = importFactory.singleOrEmpty { it.endsWith(className) }
            val matchingImport = if (importPath.startsWith(packageName)) importPath
                                 else if (currentImportForClass.startsWith(packageName)) currentImportForClass
                                 else Constants.EMPTY

            if (currentImportForClass != matchingImport || matchingImport.isEmpty()) {
              importFactory.removeIf { it == currentImportForClass }
              importFactory.add(matchingImport)
            }
          } else {
            classNames.add(className)
            importFactory.add(importPath)
          }
        }
      }
      val string = importFactory.joinToString("\nimport ", prefix = "import ", postfix = "\n")
      emit(string, true)
    }
  }

  /**
   * Adds the specific create function's body.
   */
  private fun addSpecificCreateFunction(classFactory: ClassFactory) {
    emit("  /**", true)
    emit(classFactory.firstLigneComment, true)
    emit("   *", true)
    emit("   * This is a complex type.", true)
    emit("   *", true)
    emit("   * @param ${classFactory.className.decapitalize()}s", true)
    emit("   * @return A new `${classFactory.javaPackage}` XML instance", true)
    emit("   */", true)
    emit("${indentation(1)}fun create${classFactory.className}(${classFactory.className.decapitalize()}s: Array<XmlObject>): ${classFactory.className}Document.${classFactory.className} {", true)
    emit("${indentation(2)}val new${classFactory.className} = ${classFactory.className}Document.${classFactory.className}.Factory.newInstance()\n", true)
    emit("${indentation(2)}${classFactory.className.decapitalize()}s.forEach { ${classFactory.className.decapitalize()} ->", true)
    emit("${indentation(3)}when(${classFactory.className.decapitalize()}) {", true)
    classFactory.attributes.forEach { attribute ->
      emit("${indentation(4)}is ${attribute.type}${" ".repeat((40 - attribute.type.length).absoluteValue)}-> new${classFactory.className}.add(${attribute.name} = ${classFactory.className.decapitalize()})", true)
    }
    emit("${indentation(3)}}", true)
    emit("${indentation(2)}}\n", true)
    emit("${indentation(2)}return new${classFactory.className}", true)
    emit("${indentation(1)}}\n", true)
  }

  /**
   * Adds the specific add function's body.
   */
  private fun addSpecificAddFunction(classFactory: ClassFactory) {
    val fonctionDeclaration = "${indentation(1)}fun ${classFactory.className}Document.${classFactory.className}.add("

    emit(fonctionDeclaration, false)
    classFactory.attributes.forEachIndexed { index, attribute ->
      val indent = " ".repeat(if (index == 0) 0 else fonctionDeclaration.length)
      val end = if (index == classFactory.attributes.size - 1) ")" else ","

      emit("$indent${attribute.name}: ${attribute.type}? = null$end", true)
    }
    emit("${indentation(1)}{", true)
    classFactory.attributes.forEach { attribute ->
      emit("${indentation(2)}${attribute.name}?.let { this.addNew${attribute.type}().set(it) }", true)
    }
    emit("${indentation(1)}}\n", true)
  }

  /**
   * Adds the specific size function's body.
   */
  private fun addSpecificSizeFunction(classFactory: ClassFactory) {
    emit("${indentation(1)}fun ${classFactory.className}Document.${classFactory.className}.size(): Int {", true)
    emit("${indentation(2)}return ", false)
    classFactory.attributes.forEachIndexed { index, attribute ->
      val indent = " ".repeat(if (index == 0) 0 else Constants.INDENTATION * 2 + 7)
      val end = if (index == classFactory.attributes.size - 1) "" else " +"

      emit("${indent}this.${attribute.name}Array.size$end", true)
    }
    emit("${indentation(1)}}", true)
  }

  /**
   * Get the kotlin type of an xmlType entered as a string.
   */
  private fun getKotlinTypeForProperty(type: SchemaType, rootPackageBase : String): String {
    val xmlType = if (!type.isSimpleType || type.fullJavaName.startsWith("org.apache.xmlbeans"))
      type.fullJavaName.split(".").last().replace("$", ".")
    else type.baseType.name.localPart

    // the base path of the attribute
    val relativePackageBase = if (!type.isSimpleType || !type.fullJavaName.startsWith("org.apache.xmlbeans"))
      type.fullJavaName.substringBeforeLast(".")
    else  Constants.EMPTY

    return when (xmlType) {
      "XmlDate", "date" -> {
        importFactory.add("java.time.LocalDate")
        "LocalDate"
      }
      "XmlByte", "byte" -> "Byte"
      "XmlHexBinary", "hexBinary" -> "ByteArray"
      "XmlTime", "time" -> {
        importFactory.add("java.time.LocalTime")
        "LocalTime"
      }
      "XmlDateTime", "dateTime" -> {
        importFactory.add("java.time.LocalDateTime")
        "LocalDateTime"
      }
      "XmlDuration", "duration" -> {
        importFactory.add("java.time.Duration")
        "Duration"
      }
      "XmlDecimal" -> {
        importFactory.add("java.math.BigDecimal")
        "BigDecimal"
      }
      "decimal" -> {
        val totalDigits = type.getDigits(SchemaType.FACET_TOTAL_DIGITS)
        val fractionDigits = type.getDigits(SchemaType.FACET_FRACTION_DIGITS)

        if (totalDigits == null && fractionDigits == null) {
          importFactory.add("java.math.BigDecimal")
          "BigDecimal"
        } else if (fractionDigits == 0) {
          when {
            totalDigits == null -> {
              importFactory.add("java.math.BigDecimal")
              "BigDecimal"
            }
            totalDigits <= 9 -> {
              "Int"
            }
            totalDigits <= 18 -> {
              "Long"
            }
            else -> {
              importFactory.add("java.math.BigInteger")
              "BigInteger"
            }
          }
        } else {
          importFactory.add("java.math.BigDecimal")
          "BigDecimal"
        }
      }
      "XmlInt", "int" -> "Int"
      "XmlString", "string" -> "String"
      "XmlBoolean", "boolean" -> "Boolean"
      "XmlLong", "long" -> "Long"
      "XmlShort", "short" -> "Short"
      "XmlDouble", "double" -> "Double"
      else -> {
        if (relativePackageBase != rootPackageBase) {
          "$relativePackageBase.$xmlType"
        }
        else xmlType
      }
    }
  }

  /**
   * Prints the factory by creating static methods.
   */
  private fun printFactory(factory: Factory) {
      startFactory(factory.fullName)
      classesFactory.forEach {
        addCommentFunction(it)
        addBodyFunction(it)
        if (it.hasChoiceBloc) {
          addSpecificCreateFunction(it)
          addSpecificAddFunction(it)
          addSpecificSizeFunction(it)
        }
      }
      addToCalendarFunction()
      emit("}", false)
      classesFactory.clear()
      importFactory.clear()
    }

  /**
   * Retrieves all derived properties of a schema type, including those inherited from base types
   * with the same name.
   *
   * @param schemaType The schema type to retrieve derived properties from.
   * @return An array of unique derived properties.
   */
  private fun getDerivedProperties(schemaType: SchemaType): Array<SchemaProperty> {
    val name = schemaType.name ?: return schemaType.derivedProperties

    if (name != schemaType.baseType.name) return schemaType.derivedProperties

    val propsByName = schemaType.derivedProperties.associateByTo(mutableMapOf(), { it.name }, { it })

    var baseType = schemaType.baseType
    while (baseType != null && name == baseType.name) {
      baseType.derivedProperties.forEach { prop ->
        propsByName.putIfAbsent(prop.name, prop)
      }
      baseType = baseType.baseType
    }
    return propsByName.values.toTypedArray()
  }

  /**
   * Retrieves all properties that are visible on a schema type, including those inherited
   * from base types.
   *
   * @param schemaType The schema type to retrieve properties from.
   * @return An array of all visible properties.
   */
  private fun getAllSeenProperties(schemaType: SchemaType): Array<SchemaProperty> {
    val properties = mutableListOf<SchemaProperty>()
    var sType = schemaType

    do {
      properties.addAll(getDerivedProperties(sType))
      sType = sType.baseType
    } while (sType.baseType != null)

    return properties.toTypedArray()
  }

  /**
   * Prints the top comments of the generated factory.
   */
  private fun printTopComment(factoryName: String, printHeader: Boolean) {
    if (printHeader) {
      emit(Constants.HEADER, true)
    }

    emit("/*", true)
    emit(" * Factory name: $factoryName", true)
    emit(" *", true)
    emit(" * Automatically generated - do not modify.", true)
    emit(" */", true)
    emit("", true)
  }

  /**
   * Prints the package of the generated factory.
   */
  private fun printPackage(pkg: String) {
    emit("package $pkg", true)
    emit("", true)
  }

  /**
   * Prints the top comments of the generated factory.
   */
  private fun startFactory(name: String) {
    emit("object $name {", true)
    emit("", true)
  }

  /**
   * Writes a string to the writer and optionally appending a newline character at the end.
   *
   * @param str The string to write.
   * @param newLine Whether to append a newline character at the end.
   */
  private fun emit(str: String, newLine: Boolean) {
    try {
      writer!!.write(str)
    } catch (cce: CharacterCodingException) {
      writer!!.write(makeSafe(str))
    }

    if (newLine) {
      writer!!.write("\n")
    }
  }

  /**
   * Get the first ligne comment of the fonctions.
   */
  private fun getFirstLigneComment(schemaType: SchemaType): String {
    var name = schemaType.name

    if (name == null) {
      if (schemaType.isDocumentType) {
        name = schemaType.documentElementName
      } else if (schemaType.isAttributeType) {
        name = schemaType.attributeTypeAttributeName
      } else if (schemaType.containerField != null) {
        name = schemaType.containerField.name
      }
    }

    val namespace = if (name?.namespaceURI != null) name.localPart + "(@" + name.namespaceURI + ")"
    else name.localPart

    return if (schemaType.isDocumentType) {
      "   * A document containing one $namespace element."
    } else if (schemaType.isAttributeType) {
      "   * A document containing one $namespace attribute."
    } else {
      "   * An XML $namespace."
    }
  }

  /**
   * Replaces characters in the input string that cannot be encoded in the default character set
   * with their Unicode escape sequence.
   *
   * @param str The input string to make safe.
   * @return A string with characters that cannot be encoded replaced by their Unicode escape sequence.
   */
  private fun makeSafe(str: String): String {
    val charset = Charset.defaultCharset() ?: throw IllegalStateException("Default character set is null!")
    val cEncoder = charset.newEncoder()
    val result = StringBuilder()

    for (char in str) {
      if (cEncoder.canEncode(char)) {
        result.append(char)
      } else {
        val hexValue = char.code.toString(16).padStart(4, '0')

        result.append("\\u").append(hexValue)
      }
    }
    return result.toString()
  }

  //  Variables
  val indentation = { repeat: Int -> " ".repeat(Constants.INDENTATION * repeat) }
  val classesFactory: MutableList<ClassFactory> = mutableListOf()
  val importFactory: MutableList<String> = mutableListOf()
}

/**
 * Represents an attribute of a class.
 *
 * @attribute name The name of the attribute.
 * @attribute type The type of the attribute.
 * @attribute isList Whether the attribute is a list.
 * @attribute defaultValue The default value of the attribute.
 * @attribute isElement Whether the attribute is an element.
 * @attribute isReserved Whether the attribute name is a reserved Kotlin word.
 * @attribute isCalendarAttribute Whether the attribute is a calendar attribute.
 * @attribute Required Whether the attribute is required.
 * @attribute commentName The comment name of the attribute.
 * @attribute simpleType The simple type of the attribute.
 * @attribute hasStringEnumValues Whether the attribute has string enumeration values or not.
 */
data class Attribute(var name: String,
                     var type: String,
                     var isList: Boolean,
                     var defaultValue: String,
                     var isElement: Boolean,
                     var isReserved: Boolean,
                     var isCalendarAttribute: Boolean,
                     val Required: Boolean,
                     var commentName: String = "",
                     val simpleType: String,
                     val hasStringEnumValues: Boolean)

/**
 * Represents a class factory.
 *
 * @attribute className The name of the class.
 * @attribute returnType The return type of the class.
 * @attribute attributes The list of attributes of the class.
 * @attribute hasChoiceBloc Whether the class has a choice bloc.
 * @attribute firstLigneComment The first line comment of the class.
 * @attribute javaPackage The Java package of the class.
 */
data class ClassFactory(var className: String = "",
                        var returnType: String = "",
                        val attributes: MutableList<Attribute> = mutableListOf(),
                        var hasChoiceBloc: Boolean = false,
                        var firstLigneComment: String = "",
                        var javaPackage: String = "")
