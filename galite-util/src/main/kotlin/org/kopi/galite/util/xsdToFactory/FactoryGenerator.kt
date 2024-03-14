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

package org.kopi.galite.util.xsdToFactory

import java.io.File
import java.time.Year

import kotlin.math.absoluteValue

import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.input.SAXBuilder
import org.kopi.galite.util.base.Utils

object FactoryGenerator {

  @JvmStatic
  fun main(args: Array<String>) {
    if (factoryOptions.parseCommand(args)) {
      generateFactories()
      println("Process ended successfully.")
    }
  }

  /**
   * Initialise variables
   */
  fun initialise() {
    factoryName = factoryOptions.name!!
    factoryComment = "/*\n" +
                     " * Factory name: $factoryName\n" +
                     " *\n" +
                     " * Automatically generated - do not modify.\n" +
                     " */\n\n"
    packageName = factoryOptions.fpackage!!
    xsdConfigFiles = factoryOptions.nonOptions.filter { it?.endsWith(".xsdconfig") == true }
  }


  /**
   * Get the namespace and the Java package name based on the provided targetNamespace or the xsdConfigFile.
   *
   * @param targetNamespace The target namespace to use if xsdConfigFile is null or blank.
   */
  private fun getNameSpace(targetNamespace: String) {
    javaPackageName = ""
    if (xsdConfigFiles.isNotEmpty()) {
      xsdConfigFiles.forEach { xsdFile ->
        val file = xsdFile?.let { File(it) }
        val builder = SAXBuilder()
        val document: Document = builder.build(file)
        val rootNode = document.rootElement
        val namespaceElement = rootNode.getChild("namespace", rootNode.namespace)

        if (namespaceElement.getAttributeValue("uri") == targetNamespace) {
          nameSpace = targetNamespace
          javaPackageName = namespaceElement.getChildText("package", rootNode.namespace)
        }
      }
    } else {
      val (_, uri) = targetNamespace.split("//", limit = 2)
      val (firstPart, secondPart) = uri.split("/", limit = 2)

      nameSpace = targetNamespace
      javaPackageName = firstPart.split(".").reversed().joinToString(".") +
          "." + secondPart.split("/").joinToString(".")
    }
  }

  /**
   * Recursively traverses a list of XML elements and get its final children.
   *
   * @param listAttributes     The list of XML elements to traverse.
   */
  private fun getAttributes(listAttributes: List<Element>) {
    listAttributes.forEach { attribute ->
      val list = attribute.children

      if (list.isNotEmpty()) {
        getAttributes(list)
      } else {
        if (attribute.getAttributeValue("name") != null)
          attributes.add(attribute)
      }
    }
  }

  /**
   * Adds a comment to a fonction based on a list of attributes.
   *
   * @param typeName The name of the type.
   * @param type The type of the parent element ("element" or "complexType").
   */
  private fun addComment(typeName: String, type: String) {
    val className = if (type == "element") "${typeName}Document.$typeName" else typeName

    stringBuilderFactory.append(
      "  /**\n" +
          "   * An XML $typeName(@$nameSpace).\n" +
          "   *\n" +
          "   * This is a complex type.\n" +
          "   *\n"
    )
    attributes.forEach {
      val attributeParent = it.parentElement
      val attributeNameCC = Utils.convertSnakeCaseToCamelCase(it.getAttributeValue("name")) +
          if (attributeParent.getAttributeValue("maxOccurs") == "unbounded" || it.getAttributeValue("maxOccurs") == "unbounded") "Array" else ""

      stringBuilderFactory.append("   * @param $attributeNameCC\n")
    }
    stringBuilderFactory.append(
      "   * @return A new `$javaPackageName.$className` XML instance\n" +
          "   */\n"
    )
  }

  /**
   * Get the attribute's type from its xsd type.
   *
   * @param xsdType         The xsd type.
   * @param attributeName   The attribute's name.
   * @param conditionArray  The condition specifing if the attribute is an array.
   */
  private fun getAttributeType(xsdType: String, attributeName: String, conditionArray: Boolean): String {
    return when (xsdType) {
      "string" -> "String"
      "decimal" -> {
        importFactory.add("java.math.BigDecimal")
        "BigDecimal"
      }

      "byte" -> "Byte"
      "boolean" -> "Boolean"
      "date" -> {
        importFactory.addAll(listOf("java.time.LocalDate", "com.progmag.pdv.core.base.Utils.Companion.toCalendar"))
        calendarAttributes.add(attributeName)
        "LocalDate"
      }

      "time" -> {
        importFactory.addAll(listOf("java.time.LocalTime", "com.progmag.pdv.core.base.Utils.Companion.toCalendar"))
        calendarAttributes.add(attributeName)
        "LocalTime"
      }

      "datetime" -> {
        importFactory.addAll(
          listOf(
            "java.time.LocalDateTime",
            "com.progmag.pdv.core.base.Utils.Companion.toCalendar"
          )
        )
        calendarAttributes.add(attributeName)
        "LocalDateTime"
      }

      "duration" -> {
        importFactory.add("java.time.Duration")
        "Duration"
      }

      "double" -> "Double"
      "float" -> "Float"
      "int" -> "Int"
      "short" -> "Short"
      "long" -> "Long"
      "hexBinary", "base64Binary" -> "ByteArray"
      else -> {
        if (conditionArray)
          "Array<$xsdType>"
        else
          xsdType
      }
    }
  }

  /**
   * Get the attribute's default value.
   *
   * @param attribute         The xsd type.
   * @param attributeType     The attribute's type.
   * @param conditionArray    The attribute's defaultValue.
   */
  private fun getAttributeDefaultValue(attribute: Element, attributeType: String, defaultValue: String?): String {
    return if ((attribute.getAttributeValue("use") != null && attribute.getAttributeValue("use") == "optional") || (attribute.getAttributeValue(
        "minOccurs"
      ) == "0")
    )
      "? = ${if (defaultValue != null && attributeType == "BigDecimal") "BigDecimal${if (defaultValue == "0") ".ZERO" else if (defaultValue == "1") ".ONE" else "($defaultValue)"}" else defaultValue ?: "null"}"
    else ""
  }

  /**
   * Adds parameters to a fonction based on a list of attributes.
   * Updates the importFactory and calendarAttributes lists based on the attribute types.
   *
   * @param indentationLength   The number of spaces for indentation.
   * @param typeName            The name of the type.
   * @param type                The type of the parent element ("element" or "complexType").
   */
  private fun addParameters(indentationLength: Int, typeName: String, type: String) {
    attributes.forEachIndexed { index, attribute ->
      val className = if (type == "element") "${typeName}Document.$typeName" else typeName
      val attributeName = attribute.getAttributeValue("name")
      val attributeNameCC = Utils.convertSnakeCaseToCamelCase(attributeName)
      val attributeTypeXSD = attribute.getAttributeValue("type").split(":")[1]
      val attributeParent = attribute.parentElement
      val conditionArrayAttribute =
        attributeParent.getAttributeValue("maxOccurs") == "unbounded" || attribute.getAttributeValue("maxOccurs") == "unbounded"
      val specificAttributeName = attributeNameCC + if (conditionArrayAttribute) "Array" else ""
      val defaultValue = attribute.getAttributeValue("default")
      val attributeComment = "$attributeName ${attribute.name}" + if (conditionArrayAttribute) " Array" else ""
      val attributeType = getAttributeType(attributeTypeXSD, attributeNameCC, conditionArrayAttribute)
      val attributeDefaultValue = getAttributeDefaultValue(attribute, attributeType, defaultValue)

      if (index == attributes.size - 1) {
        stringBuilderFactory.append(
          "${if (attributes.size == 1) "" else " ".repeat(indentationLength)}$specificAttributeName: $attributeType$attributeDefaultValue)  // $attributeName attribute\n" +
              "${indentation(1)}: $className\n" +
              "${indentation(1)}{\n"
        )
      } else if (index == 0) {
        stringBuilderFactory.append("$specificAttributeName: $attributeType$attributeDefaultValue,  // $attributeComment\n")
      } else {
        stringBuilderFactory.append("${" ".repeat(indentationLength)}$specificAttributeName: $attributeType$attributeDefaultValue,  // $attributeComment\n")
      }
      //if the attribute is an element, add the specific import package to the importFactory list
      if (attribute.name == "element")
        importFactory.add("$javaPackageName.$attributeTypeXSD")
      //if the attribute's parent is a choice, add it to the choiceAttributes list
      if (attributeParent.name == "choice")
        choiceAttributes.add(attribute)
    }
  }

  /**
   * Adds the body of a factory method.
   *
   * @param name     The name of the type.
   * @param type     The type of the parent element ("element" or "complexType").
   */
  private fun addBody(name: String, type: String) {
    val nameclasse = if (type == "element") "${name}Document.$name" else name

    stringBuilderFactory.append("${indentation(2)}val new$name = $nameclasse.Factory.newInstance()\n\n")
    attributes.forEach { attribute ->
      val attributeNameCC = Utils.convertSnakeCaseToCamelCase(attribute.getAttributeValue("name"))
      val parentAttribute = attribute.parentElement
      val arrayAttributeName = attributeNameCC +
          if (parentAttribute.getAttributeValue("maxOccurs") == "unbounded" || attribute.getAttributeValue("maxOccurs") == "unbounded") "Array" else ""
      val attributeUse = attribute.getAttributeValue("use")
      val calendarAttribute = if (attributeNameCC in calendarAttributes) ".toCalendar()" else ""

      if ((attribute.getAttributeValue("use") != null && attributeUse == "optional") || attribute.getAttributeValue("minOccurs") == "0") {
        stringBuilderFactory.append("${indentation(2)}$arrayAttributeName?.let { new$name.$arrayAttributeName = $arrayAttributeName$calendarAttribute }\n")
      } else {
        stringBuilderFactory.append("${indentation(2)}new$name.$arrayAttributeName = $arrayAttributeName$calendarAttribute\n")
      }
    }
    stringBuilderFactory.append(
      "\n${indentation(2)}return new$name\n" +
          "${indentation(1)}}\n\n"
    )
  }

  /**
   * Adds import bloc to the beginning of the generated factory.
   */
  private fun addImports() {
    if (importFactory.isNotEmpty()) {
      val groupedPackages = importFactory.distinct()
        .sortedWith(compareBy({ it.startsWith("com") }, { it.startsWith("org") }, { it }))
        .groupBy { it.substringBeforeLast(".") }
      var string = ""

      groupedPackages.forEach { (_, subPackages) ->
        string += "import " + subPackages.joinToString("\nimport ") + "\n\n"
      }

      stringBuilderFactory.insert(0, string)
    }
    if (importDocumentFactory.isNotEmpty()) {
      val string = importDocumentFactory.distinct().sorted().joinToString("\n") { "import $it" }

      stringBuilderDocumentFactory.insert(0, string + "\n\n")
    }
  }

  /**
   * Adds the function comment.
   */
  fun addSpecificComment(typeName: String, parameter: String) {
    val functionComment = "  /**\n" +
        "   * An XML $typeName ($nameSpace).\n" +
        "   *\n" +
        "   * This is a complex type.\n" +
        "   *\n" +
        "   * @param $parameter\n" +
        "   * @return A new `$javaPackageName.$typeName` XML instance.\n" +
        "   */\n"

    stringBuilderFactory.append(functionComment)
  }

  /**
   * Adds the create function's body.
   */
  fun addSpecificCreateFunction(
    typeName: String,
    typeNameCC: String,
    className: String,
    parameter: String
  ) {
    val createFunction = buildString {
      append("${indentation(1)}fun create${typeName}($parameter: Array<XmlObject>): $className {\n")
      append("${indentation(2)}val new$typeName = $className.Factory.newInstance()\n\n")
      append("${indentation(2)}$parameter.forEach { $typeNameCC ->\n")
      append("${indentation(3)}when($typeNameCC) {\n")
      choiceAttributes.forEach { attribute ->
        val attributeNameCC = Utils.convertSnakeCaseToCamelCase(attribute.getAttributeValue("name")).capitalize()

        append(
          "${indentation(4)}is $attributeNameCC${" ".repeat((40 - attributeNameCC.length).absoluteValue)}-> new$typeName.ajouter($attributeNameCC = $typeNameCC)\n"
        )
      }
      append("${indentation(3)}}\n")
      append("${indentation(2)}}\n")
      append("\n${indentation(2)}return new$typeName\n")
      append("${indentation(1)}}\n\n")
    }

    stringBuilderFactory.append(createFunction)
  }

  /**
   * Adds the add function's body.
   */
  fun addSpecificAddFunction(className: String) {
    val addFonction = buildString {
      val fonctionDeclaration = "${indentation(1)}fun $className.ajouter("
      append(fonctionDeclaration)
      choiceAttributes.forEachIndexed { index, attribut ->
        val nomAttributeCC = Utils.convertSnakeCaseToCamelCase(attribut.getAttributeValue("name"))

        append("${" ".repeat(if (index == 0) 0 else fonctionDeclaration.length)}$nomAttributeCC: ${nomAttributeCC.capitalize()}? = null${if (index == choiceAttributes.size - 1) ")" else ","}\n")
      }
      append("${indentation(1)}{\n")
      choiceAttributes.forEach { attribute ->
        val attributeName = attribute.getAttributeValue("name")
        val attributeNameCC = Utils.convertSnakeCaseToCamelCase(attributeName)

        append("${indentation(2)}$attributeNameCC?.let { this.addNew$attributeName().set(it) }\n")
      }
      append("${indentation(1)}}\n\n")
    }

    stringBuilderFactory.append(addFonction)
  }

  /**
   * Adds the size function's body.
   */
  fun addSpecificSizeFunction(className: String) {
    val sizeFonction = buildString {
      append("${indentation(1)}fun $className.size(): Int {\n")
      append("${indentation(2)}return ")
      choiceAttributes.forEachIndexed { index, attribute ->
        val attributeNameCC = Utils.convertSnakeCaseToCamelCase(attribute.getAttributeValue("name")) + "Array"

        append("${" ".repeat(if (index == 0) 0 else INDENTATION * 2 + 7)}this.$attributeNameCC.size ${if (index == choiceAttributes.size - 1) "" else "+"}\n")
      }
      append("${indentation(1)}}\n")
    }

    stringBuilderFactory.append(sizeFonction)
  }

  /**
   * Adds specific element processing to the elements with choice as parent.
   * Includes the creation of a create function, an add function, and a size function.
   *
   * @param typeName The name of the type.
   */
  private fun addSpecificElementProcessing(typeName: String) {
    importFactory.addAll(listOf("org.apache.xmlbeans.XmlObject", "$javaPackageName.${typeName}Document"))
    val typeNameCC = Utils.convertSnakeCaseToCamelCase(typeName).decapitalize()
    val className = "${typeName}Document.$typeName"
    val parameter = "${typeNameCC}s"

    addSpecificComment(typeName, parameter)
    addSpecificCreateFunction(typeName, typeNameCC, className, parameter)
    addSpecificAddFunction(className)
    addSpecificSizeFunction(className)
  }

  /**
   * Add the factory document's comment.
   */
  fun addDocumentFactoryComment(typeNameCC: String, documentTypeName: String) {
    val functionComment = "  /**\n" +
                          "   * A document containing one $typeNameCC($nameSpace) element.\n" +
                          "   *\n" +
                          "   * This is a complex type.\n" +
                          "   *\n" +
                          "   * @param $typeNameCC\n" +
                          "   * @return A new `$javaPackageName.$documentTypeName` XML instance.\n" +
                          "   */\n"

    stringBuilderDocumentFactory.append(functionComment)
  }

  /**
   * Add the factory document's function.
   */
  fun addDocumentFactoryFunction(typeNameCC: String, documentTypeName: String, nomType: String) {
    val functionBody = buildString {
      append("${indentation(1)}fun create$documentTypeName($typeNameCC: $documentTypeName.$nomType): $documentTypeName  // $typeNameCC element\n")
      append("${indentation(1)}{\n")
      append("${indentation(2)}val new$documentTypeName = $documentTypeName.Factory.newInstance()\n\n")
      append("${indentation(2)}new$documentTypeName = $typeNameCC\n\n")
      append("${indentation(2)}return new$documentTypeName\n")
      append("${indentation(1)}}\n")
    }

    stringBuilderDocumentFactory.append(functionBody)
  }

  /**
   * Generates kotlin code of the generated DocumentFactory.
   */
  private fun addDocumentFactory(nomType: String) {
    val typeNameCC = Utils.convertSnakeCaseToCamelCase(nomType)
    val documentTypeName = nomType + "Document"

    importDocumentFactory.add("$javaPackageName.$documentTypeName")
    if (stringBuilderDocumentFactory.isEmpty()) {
      stringBuilderDocumentFactory.append("object ${factoryName}DocumentFactory {\n\n")
    }
    addDocumentFactoryComment(typeNameCC, documentTypeName)
    addDocumentFactoryFunction(typeNameCC, documentTypeName, nomType)
  }

  /**
   * Add the functions' body of a factory.
   */
  fun addFunctions(rootNode: Element) {
    val types: HashMap<String, List<Element>> = hashMapOf(
      "complexType" to rootNode.getChildren("complexType", rootNode.namespace),
      "element" to rootNode.getChildren("element", rootNode.namespace)
    )

    for (type in types) {
      for (complexType in type.value) {
        val typeName = complexType.getAttributeValue("name").capitalize()
        val listAttributes: List<Element> = complexType.children

        getAttributes(listAttributes)
        if (attributes.isNotEmpty()) {
          addComment(typeName, type.key)
          val fonctionDeclaration = "${indentation(1)}fun create${typeName}("

          stringBuilderFactory.append(fonctionDeclaration)
          addParameters(fonctionDeclaration.length, typeName, type.key)
          addBody(typeName, type.key)
          if (type.key == "element") {
            addDocumentFactory(typeName)
            if (choiceAttributes.isNotEmpty())
              addSpecificElementProcessing(typeName)
          }
        }
        attributes.clear()
        choiceAttributes.clear()
      }
    }
  }

  /**
   * Generates kotlin code of the generated factory.
   */
  fun generateKotlinCode(xsdFile: File) {
    val builder = SAXBuilder()
    val document: Document = builder.build(xsdFile)
    val rootNode = document.rootElement

    getNameSpace(rootNode.getAttributeValue("targetNamespace"))
    stringBuilderFactory.append("object ${factoryName}Factory {\n\n")
    addFunctions(rootNode)
    stringBuilderFactory.append("}")
    if (stringBuilderDocumentFactory.isNotEmpty()) stringBuilderDocumentFactory.append("}")
    addImports()
    stringBuilderFactory.insert(0, "package $packageName\n\n")
    stringBuilderFactory.insert(0, factoryComment)
    stringBuilderFactory.insert(0, entete)
    if (stringBuilderDocumentFactory.isNotEmpty()) {
      stringBuilderDocumentFactory.insert(0, "package $packageName\n\n")
      stringBuilderDocumentFactory.insert(0, factoryComment)
      stringBuilderDocumentFactory.insert(0, entete)
    }
  }

  /**
   * Generate factories.
   */
  private fun generateFactories() {
    initialise()
    factoryOptions.nonOptions.filter { it?.endsWith(".xsd") == true }.forEach { file ->
      val fileFactoryName = "${factoryName}Factory.kt"
      val parentDirectoryName =
        factoryOptions.directory ?: (factoryOptions.source + "/" + factoryOptions.fpackage!!.replace(".", "/"))

      generateKotlinCode(File(file))

      File(parentDirectoryName, fileFactoryName).writeText(stringBuilderFactory.toString())
      if (stringBuilderDocumentFactory.isNotEmpty()) {
        val nomFichierDocFactory = "${factoryName}DocumentFactory.kt"

        File(parentDirectoryName, nomFichierDocFactory).writeText(stringBuilderDocumentFactory.toString())
      }
    }
  }

  //--------------------------------------------------------------------------------------------
  // GLOBAL
  //--------------------------------------------------------------------------------------------

  val indentation = { repeat: Int -> " ".repeat(INDENTATION * repeat) }
  val factoryOptions = FactoryOptions()
  val entete = "// ----------------------------------------------------------------------\n" +
               "// Copyright (c) 2013-${Year.now()} kopiLeft Services SARL, Tunisie\n" +
               "// Copyright (c) 2018-${Year.now()} ProGmag SAS, France\n" +
               "// ----------------------------------------------------------------------\n" +
               "// All rights reserved - tous droits réservés.\n" +
               "// ----------------------------------------------------------------------\n\n"
  val stringBuilderFactory = StringBuilder()
  val stringBuilderDocumentFactory = StringBuilder()
  val attributes: MutableList<Element> = mutableListOf()
  val choiceAttributes: MutableList<Element> = mutableListOf()
  val calendarAttributes: MutableList<String> = mutableListOf()
  val importFactory: MutableList<String> = mutableListOf()
  val importDocumentFactory: MutableList<String> = mutableListOf()
  var xsdConfigFiles: List<String?> = listOf()

  lateinit var factoryComment: String
  lateinit var packageName: String
  lateinit var javaPackageName: String
  lateinit var nameSpace: String
  lateinit var factoryName: String

  const val INDENTATION = 2
}