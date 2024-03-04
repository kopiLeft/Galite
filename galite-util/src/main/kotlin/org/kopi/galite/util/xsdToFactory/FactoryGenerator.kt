/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
 * Copyright (c) 1990-2022 kopiRight Managed Solutions GmbH, Wien AT
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

import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.input.SAXBuilder
import org.kopi.galite.util.base.Utils

object FactoryGenerator {

  /**
   * Determines the namespace and the Java package name based on the provided targetNamespace or the xsdConfigFile.
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
        val namespaceElement = rootNode.getChild("namespace", document.rootElement.namespace)

        if (namespaceElement.getAttributeValue("uri") == targetNamespace) {
          nameSpace = targetNamespace
          javaPackageName = namespaceElement.getChildText("package", rootNode.namespace)
        }
      }
    } else {
      val uri = targetNamespace.split("//")[1]
      val parts = uri.split("/", limit = 2)
      val firstPart = parts[0].split(".").reversed()

      nameSpace = targetNamespace
      javaPackageName = firstPart.joinToString(".") + "." + parts[1].split("/").joinToString(".")
    }
  }

  /**
   * Recursively traverses a list of XML elements.
   *
   * @param attributeList     The list of XML elements to traverse.
   */
  private fun getAttributes(listAttributes: List<Element>) {
    listAttributes.forEach { attribut ->
      val list = attribut.children

      if (list.isNotEmpty()) {
        getAttributes(list)
      } else {
        if (attribut.getAttributeValue("name") != null)
          attributes.add(attribut)
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

    stringBuilderFactory.append("  /**\n" +
                                "   * An XML $typeName(@$nameSpace).\n" +
                                "   *\n" +
                                "   * This is a complex type.\n" +
                                "   *\n")
    attributes.forEach {
      val attributeParent = it.parentElement
      val attributeNameCC = Utils.convertSnakeCaseToCamelCase(it.getAttributeValue("name")) +
        if (attributeParent.getAttributeValue("maxOccurs") == "unbounded" || it.getAttributeValue("maxOccurs") == "unbounded") "Array" else ""

      stringBuilderFactory.append("   * @param $attributeNameCC\n")
    }
    stringBuilderFactory.append("   * @return A new `$javaPackageName.$className` XML instance\n" +
                                "   */\n")
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

      if (attribute.name == "element")
        importFactory.add("$javaPackageName.$attributeTypeXSD")
      val attributeParent = attribute.parentElement

      if (attributeParent.name == "choice")
        choiceAttributes.add(attribute)
      val specificAttributeName = attributeNameCC +
        if (attributeParent.getAttributeValue("maxOccurs") == "unbounded" || attribute.getAttributeValue("maxOccurs") == "unbounded" ) "Array" else ""
      val defaultValue = attribute.getAttributeValue("default")
      val attributeComment =
        "$attributeName ${attribute.name}" + if (attributeParent.getAttributeValue("maxOccurs") == "unbounded" || attribute.getAttributeValue("maxOccurs") == "unbounded") " Array" else ""
      val attributeType = when (attributeTypeXSD) {
        "string" -> "String"
        "decimal" -> {
          importFactory.add("java.math.BigDecimal")
          "BigDecimal"
        }
        "byte" -> "Byte"
        "boolean" -> "Boolean"
        "date" -> {
          importFactory.addAll(listOf("java.time.LocalDate", "com.progmag.pdv.core.base.Utils.Companion.toCalendar"))
          calendarAttributes.add(attributeNameCC)
          "LocalDate"
        }
        "time" -> {
          importFactory.addAll(listOf("java.time.LocalTime", "com.progmag.pdv.core.base.Utils.Companion.toCalendar"))
          calendarAttributes.add(attributeNameCC)
          "LocalTime"
        }
        "datetime" -> {
          importFactory.addAll(
            listOf(
              "java.time.LocalDateTime",
              "com.progmag.pdv.core.base.Utils.Companion.toCalendar"
            )
          )
          calendarAttributes.add(attributeNameCC)
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
          if (attributeParent.getAttributeValue("maxOccurs") == "unbounded" || attribute.getAttributeValue("maxOccurs") == "unbounded")
            "Array<$attributeTypeXSD>"
          else
            attributeTypeXSD
        }
      }
      val attributeUse = if ((attribute.getAttributeValue("use") != null && attribute.getAttributeValue("use") == "optional")||(attribute.getAttributeValue("minOccurs") == "0"))
        "? = ${if (defaultValue != null && attributeType == "BigDecimal") "BigDecimal${if (defaultValue == "0") ".ZERO" else if (defaultValue == "1") ".ONE" else "($defaultValue)"}" else defaultValue ?: "null"}"
      else ""

      if (index == attributes.size - 1) {
        stringBuilderFactory.append("${if (attributes.size == 1) "" else " ".repeat(indentationLength)}$specificAttributeName: $attributeType$attributeUse)  // $attributeName attribute\n" +
            "${" ".repeat(indentation)}: $className\n" +
            "${" ".repeat(indentation)}{\n")
      } else if (index == 0) {
        stringBuilderFactory.append("$specificAttributeName: $attributeType$attributeUse,  // $attributeComment\n")
      } else {
        stringBuilderFactory.append("${" ".repeat(indentationLength)}$specificAttributeName: $attributeType$attributeUse,  // $attributeComment\n")
      }
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

    stringBuilderFactory.append("${" ".repeat(indentation * 2)}val new$name = $nameclasse.Factory.newInstance()\n\n")
    attributes.forEach { attribute ->
      val attributeNameCC = Utils.convertSnakeCaseToCamelCase(attribute.getAttributeValue("name"))
      val parentAttribute = attribute.parentElement
      val arrayAttributeName = attributeNameCC +
          if (parentAttribute.getAttributeValue("maxOccurs") == "unbounded" || attribute.getAttributeValue("maxOccurs") == "unbounded") "Array" else ""
      val attributeUse = attribute.getAttributeValue("use")
      val calendarAttribute = if (attributeNameCC in calendarAttributes) ".toCalendar()" else ""

      if ((attribute.getAttributeValue("use") != null && attributeUse == "optional") || attribute.getAttributeValue("minOccurs") == "0") {
        stringBuilderFactory.append("${" ".repeat(indentation * 2)}$arrayAttributeName?.let { new$name.$arrayAttributeName = $arrayAttributeName$calendarAttribute }\n")
      } else {
        stringBuilderFactory.append("${" ".repeat(indentation * 2)}new$name.$arrayAttributeName = $arrayAttributeName$calendarAttribute\n")
      }
    }
    stringBuilderFactory.append("\n${" ".repeat(indentation * 2)}return new$name\n" +
                               "${" ".repeat(indentation)}}\n\n")
  }

  /**
   * Adds import bloc to the beginning of the generated factory.
   */
  private fun addImportBloc() {
    if (importFactory.isNotEmpty()) {
      val sortedPackages = importFactory.distinct()
        .sortedWith(compareBy({ it.startsWith("com") }, { it.startsWith("org") }, { it }))
      val groupedPackages = sortedPackages.groupBy { it.substringBeforeLast(".") }
      var string = ""

      groupedPackages.forEach { (_, subPackages) ->
        string += "import " + subPackages.joinToString("\nimport ") + "\n\n"
      }

      stringBuilderFactory.insert(0, string)
    }

    if (importDocumentFactory.isNotEmpty()) {
      val sortedPackages = importDocumentFactory.distinct().sorted()
      var string = ""

      sortedPackages.forEach {
        string += "import $it\n"
      }

      stringBuilderDocumentFactory.insert(0, string + "\n")
    }
  }

  /**
   * Adds specific element processing to the elements with choice as parent element.
   * Includes the creation of a create function, an add function, and a size function.
   *
   * @param typeName The name of the type.
   */
  private fun addSpecificElementProcessing(typeName: String) {
    importFactory.addAll(listOf("org.apache.xmlbeans.XmlObject", "$javaPackageName.${typeName}Document"))
    val typeNameCC = Utils.convertSnakeCaseToCamelCase(typeName)
    val className = "${typeName}Document.$typeName"
    val parameter = "${typeNameCC}s"
    val fonctionComment = "  /**\n" +
                          "   * An XML $typeName ($nameSpace).\n" +
                          "   *\n" +
                          "   * This is a complex type.\n" +
                          "   *\n" +
                          "   * @param $parameter\n" +
                          "   * @return A new `$javaPackageName.$typeName` XML instance.\n" +
                          "   */\n"

    stringBuilderFactory.append(fonctionComment)

    val createFonction = buildString {
      append("${" ".repeat(indentation)}fun create${typeName}($parameter: Array<XmlObject>): $className {\n")
      append("${" ".repeat(indentation * 2)}val new$typeName = $className.Factory.newInstance()\n\n")
      append("${" ".repeat(indentation * 2)}$parameter.forEach { ${typeNameCC.decapitalize()} ->\n")
      append("${" ".repeat(indentation * 3)}when($typeName) {\n")
      choiceAttributes.forEach { attribute ->
        val attributeNameCC = Utils.convertSnakeCaseToCamelCase(attribute.getAttributeValue("name"))

        append(
          "${" ".repeat(indentation * 4)}is ${String.format("%-20s", attributeNameCC)}-> new$typeName.ajouter($attributeNameCC = $typeNameCC)\n"
        )
      }
      append("${" ".repeat(indentation * 3)}}\n")
      append("${" ".repeat(indentation * 2)}}\n")
      append("\n${" ".repeat(indentation * 2)}return new$typeName\n")
      append("${" ".repeat(indentation)}}\n\n")
    }

    stringBuilderFactory.append(createFonction)

    val addFonction = buildString {
      val fonctionDeclaration = "${" ".repeat(indentation)}fun $className.ajouter("
      append(fonctionDeclaration)
      choiceAttributes.forEachIndexed { index, attribut ->
        val nomAttributCC = Utils.convertSnakeCaseToCamelCase(attribut.getAttributeValue("name"))

        append("${" ".repeat(if (index == 0) 0 else indentation + fonctionDeclaration.length)}$nomAttributCC: ${nomAttributCC.capitalize()}? = null${if (index == choiceAttributes.size - 1) ")" else ","}\n")
      }
      append("${" ".repeat(indentation)}{\n")
      choiceAttributes.forEach { attribute ->
        val attributeName = attribute.getAttributeValue("name")
        val attributeNameCC = Utils.convertSnakeCaseToCamelCase(attributeName)

        append("${" ".repeat(indentation * 2)}$attributeNameCC?.let { this.addNew$attributeName().set(it) }\n")
      }
      append("${" ".repeat(indentation)}}\n\n")
    }

    stringBuilderFactory.append(addFonction)

    val sizeFonction = buildString {
      append("${" ".repeat(indentation)}fun $className.size(): Int {\n")
      append("${" ".repeat(indentation * 2)}return ")
      choiceAttributes.forEachIndexed { index, attribute ->
        val attributeNameCC = Utils.convertSnakeCaseToCamelCase(attribute.getAttributeValue("name")) + "Array"

        append("${" ".repeat(if (index == 0) 0 else indentation * 2 + 7)}this.$attributeNameCC.size ${if (index == choiceAttributes.size - 1) "" else "+"}\n")
      }
      append("${" ".repeat(indentation)}}\n")
    }

    stringBuilderFactory.append(sizeFonction)
  }

  /**
   * Generates kotlin code of the generated DocumentFactory.
   */
  private fun addDocumentFactory(nomType: String) {
    val typeNameCC = Utils.convertSnakeCaseToCamelCase(nomType)
    val documentTypeName = nomType + "Document"

    importDocumentFactory.add("$javaPackageName.$documentTypeName")

    if (stringBuilderDocumentFactory.isEmpty()) {
      stringBuilderDocumentFactory.append("object ${factoryName}DocumentFactory {\n")
    }

    val fonctionComment = "  /**\n" +
                          "   * A document containing one $typeNameCC($nameSpace) element.\n" +
                          "   *\n" +
                          "   * This is a complex type.\n" +
                          "   *\n" +
                          "   * @param $typeNameCC\n" +
                          "   * @return A new `$javaPackageName.$documentTypeName` XML instance.\n" +
                          "   */\n"

    stringBuilderDocumentFactory.append(fonctionComment)

    val fonctionBody = buildString {
      append("${" ".repeat(indentation)}fun create$documentTypeName($typeNameCC: $documentTypeName.$nomType): $documentTypeName  // $typeNameCC element\n")
      append("${" ".repeat(indentation)}{\n")
      append("${" ".repeat(indentation * 2)}val new$documentTypeName = $documentTypeName.Factory.newInstance()\n\n")
      append("${" ".repeat(indentation * 2)}new$documentTypeName = $typeNameCC\n\n")
      append("${" ".repeat(indentation * 2)}return new$documentTypeName\n")
      append("${" ".repeat(indentation)}}\n")
    }

    stringBuilderDocumentFactory.append(fonctionBody)
  }

  /**
   * Generates kotlin code of the generated factory.
   */
  fun generateKotlinCode(xsdFile: File) {
    val builder = SAXBuilder()
    val document: Document = builder.build(xsdFile)
    val rootNode = document.rootElement

    getNameSpace(rootNode.getAttributeValue("targetNamespace"))
    val types: HashMap<String, List<Element>> = hashMapOf(
      "complexType" to rootNode.getChildren("complexType", rootNode.namespace),
      "element" to rootNode.getChildren("element", rootNode.namespace)
    )

    stringBuilderFactory.append("object ${factoryName}Factory {\n\n")
    for (type in types) {
      for (complexType in type.value) {
        val typeName = complexType.getAttributeValue("name").capitalize()
        val listAttributes: List<Element> = complexType.children

        getAttributes(listAttributes)
        if (attributes.isNotEmpty()) {
          addComment(typeName, type.key)
          val fonctionDeclaration = "${" ".repeat(indentation)}fun create${typeName}("

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
    stringBuilderFactory.append("}")
    if (stringBuilderDocumentFactory.isNotEmpty()) stringBuilderDocumentFactory.append("}")
    addImportBloc()
    stringBuilderFactory.insert(0, "package $packageName\n\n")
    stringBuilderFactory.insert(0, factoryComment)
    stringBuilderFactory.insert(0, entete)
    if (stringBuilderDocumentFactory.isNotEmpty()) {
      stringBuilderDocumentFactory.insert(0, "package $packageName\n\n")
      stringBuilderDocumentFactory.insert(0, factoryComment)
      stringBuilderDocumentFactory.insert(0, entete)
    }
  }
}

fun main(args: Array<String>) {
  val factoryOptions = FactoryOptions()

  if (factoryOptions.parseCommand(args)) {
    factoryName = factoryOptions.nom!!
    factoryComment = "/*\n" +
                     " * Factory name: $factoryName\n" +
                     " *\n" +
                     " * Automatically generated - do not modify.\n" +
                     " */\n\n"
    packageName = factoryOptions.fpackage!!
    xsdConfigFiles = factoryOptions.nonOptions.filter { it?.endsWith(".xsdconfig") == true }
    factoryOptions.nonOptions.filter { it?.endsWith(".xsd") == true }.forEach { file ->
      val nomFichierFactory = "${factoryName}Factory.kt"
      val nomDossierParent = factoryOptions.directory ?: (factoryOptions.source + "/" + factoryOptions.fpackage!!.replace(".", "/"))

      FactoryGenerator.generateKotlinCode(File(file))

      File(nomDossierParent, nomFichierFactory).writeText(stringBuilderFactory.toString())
      if (stringBuilderDocumentFactory.isNotEmpty()) {
        val nomFichierDocFactory = "${factoryName}DocumentFactory.kt"

        File(nomDossierParent, nomFichierDocFactory).writeText(stringBuilderDocumentFactory.toString())
      }
    }
    println("Géneration terminée avec succès.")
  }
}

const val entete = "// ----------------------------------------------------------------------\n" +
                   "// Copyright (c) 2013-2024 kopiLeft Services SARL, Tunisie\n" +
                   "// Copyright (c) 2018-2024 ProGmag SAS, France\n" +
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

const val indentation = 2