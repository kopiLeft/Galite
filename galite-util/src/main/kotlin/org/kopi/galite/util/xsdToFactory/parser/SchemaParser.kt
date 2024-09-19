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

package org.kopi.galite.util.xsdToFactory.parser

import java.io.File
import java.net.URL
import java.util.*

import org.apache.xmlbeans.*
import org.apache.xmlbeans.impl.config.BindingConfigImpl
import org.apache.xmlbeans.impl.schema.SchemaTypeLoaderImpl
import org.apache.xmlbeans.impl.schema.SchemaTypeSystemCompiler
import org.apache.xmlbeans.impl.schema.StscState
import org.apache.xmlbeans.impl.xb.substwsdl.DefinitionsDocument
import org.apache.xmlbeans.impl.xb.xmlconfig.ConfigDocument
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument

import org.kopi.galite.util.xsdToFactory.utils.XmlErrorWatcher

class SchemaParser {
	fun parse(xsdFiles: Array<File>?,
		        wsdlFiles: Array<File>?,
		        urlFiles: Array<URL>?,
		        configFiles: Array<File>?,
		        cpResourceLoader: ResourceLoader?,
		        outerErrorListener: MutableCollection<XmlError>?,
		        baseDir: File?,
		        classpath: Array<File>?): SchemaTypeSystem {
		val errorListener = XmlErrorWatcher(outerErrorListener)
		val state = StscState.start()
		val loader = XmlBeans.typeLoaderForClassLoader(SchemaDocument::class.java.classLoader)
		val scontentlist = mutableListOf<SchemaDocument.Schema>()

		state.setErrorListener(errorListener)

		fun parseFile(files: Array<File>?, fileType: String) {
			files?.forEach { file ->
				try {
					val options = XmlOptions().apply {
						setLoadLineNumbers()
						setLoadMessageDigest()
					}
					val doc = loader.parse(file, null, options)

					when (doc) {
						is SchemaDocument -> addSchema(file.toString(), doc, errorListener, scontentlist)
						is DefinitionsDocument -> addWsdlSchemas(file.toString(), doc, errorListener, scontentlist)
						else -> StscState.addError(
							errorListener,
							XmlErrorCodes.INVALID_DOCUMENT_TYPE,
							arrayOf<Any>(file, fileType),
							doc
						)
					}
				} catch (e: XmlException) {
					errorListener.add(e.error)
				} catch (e: Exception) {
					StscState.addError(
						errorListener,
						XmlErrorCodes.CANNOT_LOAD_FILE,
						arrayOf<Any?>("xsd", file, e.message),
						file
					)
				}
			}
		}

		parseFile(xsdFiles, "schema")
		parseFile(wsdlFiles, "wsdl")
		parseFile(urlFiles.toFileArray(), "wsdl or schema")

		val cdoclist = configFiles?.mapNotNull { file ->
			try {
				val options = XmlOptions().apply {
					setLoadLineNumbers()
					setLoadSubstituteNamespaces(MAP_COMPATIBILITY_CONFIG_URIS)
				}
				val doc = loader.parse(file, null, options)

				if (doc is ConfigDocument && doc.validate(XmlOptions().setErrorListener(errorListener))) {
					StscState.addInfo(errorListener, "Loading config file $file")
					doc.config
				} else null
			} catch (e: XmlException) {
				errorListener.add(e.error)
				null
			} catch (e: Exception) {
				StscState.addError(
					errorListener,
					XmlErrorCodes.CANNOT_LOAD_FILE,
					arrayOf<Any?>("xsd config", file, e.message),
					file
				)
				null
			}
		} ?: emptyList()

		val opts = XmlOptions().apply {
			setCompileDownloadUrls()
			setCompileNoValidation()
		}

		val linkTo = SchemaTypeLoaderImpl.build(null, cpResourceLoader, null)
		val params = SchemaTypeSystemCompiler.Parameters().apply {
			schemas = scontentlist.toTypedArray()
			config = BindingConfigImpl.forConfigDocuments(cdoclist.toTypedArray(), null, classpath)
			this.linkTo = linkTo
			options = opts
			setErrorListener(errorListener)
			isJavaize = true
			baseDir?.let { baseURI = it.toURI() }
		}

		return SchemaTypeSystemCompiler.compile(params)
	}

	/**
	 * Add schema to the `scontentlist` list
	 * @param name The schema name of the schama
	 * @param schemadoc The `SchemaDocument` instance
	 * @param errorListener The error listener watcher
	 * @param scontentlist The schema content list
	 */
	private fun addSchema(name: String,
		                    schemadoc: SchemaDocument,
		                    errorListener: XmlErrorWatcher,
		                    scontentlist: MutableList<SchemaDocument.Schema>) {
		StscState.addInfo(errorListener, "Loading schema file $name")
		val opts = XmlOptions().setErrorListener(errorListener)

		if (schemadoc.validate(opts)) {
			scontentlist.add(schemadoc.schema)
		}
	}

	/**
	 * Add a wsdl schema to the schema content list
	 * @param name The name of the wdsl schema
	 * @param wsdldoc The `DefinitionsDocument` instance
	 * @param errorListener The error listener watcher
	 * @param scontentlist The schema content list
	 */
	private fun addWsdlSchemas(name: String,
		                         wsdldoc: DefinitionsDocument,
		                         errorListener: XmlErrorWatcher,
		                         scontentlist: MutableList<SchemaDocument.Schema>) {
		if (wsdlContainsEncoded(wsdldoc)) {
			StscState.addWarning(errorListener,
				                   "The WSDL $name uses SOAP encoding. SOAP encoding is not compatible with literal XML Schema.",
				                   XmlErrorCodes.GENERIC_ERROR,
				                   wsdldoc)
		}

		StscState.addInfo(errorListener, "Loading wsdl file $name")
		val opts = XmlOptions().setErrorListener(errorListener)
		val types = wsdldoc.definitions.typesArray
		var count = 0

		for (j in types.indices) {
			val schemas = types[j].selectPath("declare namespace xs=\"http://www.w3.org/2001/XMLSchema\" xs:schema")

			if (schemas.size == 0) {
				StscState.addWarning(errorListener,
					                   "The WSDL $name did not have any schema documents in namespace 'http://www.w3.org/2001/XMLSchema'",
					                   XmlErrorCodes.GENERIC_ERROR,
					                   wsdldoc)
				continue
			}

			for (k in schemas.indices) {
				if (schemas[k] is SchemaDocument.Schema && schemas[k].validate(opts)) {
					count++
					scontentlist.add(schemas[k] as SchemaDocument.Schema)
				}
			}
		}

		StscState.addInfo(errorListener, "Processing $count schema(s) in $name")
	}

	/**
	 * Search for any <soap:body use="encoded"></soap:body> etc.
	 * @param wsdldoc The wsdl schema document
	 * @return True if the wsdl contains an encoded tag
	 */
	private fun wsdlContainsEncoded(wsdldoc: XmlObject): Boolean {
		val useAttrs = wsdldoc.selectPath("declare namespace soap='http://schemas.xmlsoap.org/wsdl/soap/' " +
				".//soap:body/@use|.//soap:header/@use|.//soap:fault/@use")
		for (i in useAttrs.indices) {
			if (("encoded" == (useAttrs[i] as SimpleValue).stringValue)) {
				return true
			}
		}

		return false
	}

	fun Array<URL>?.toFileArray(): Array<File>? {
		return this?.map { url -> File(url.toURI()) }?.toTypedArray()
	}

	companion object {
		private val CONFIG_URI = "http://xml.apache.org/xmlbeans/2004/02/xbean/config"
		private val COMPATIBILITY_CONFIG_URI = "http://www.bea.com/2002/09/xbean/config"
		private val MAP_COMPATIBILITY_CONFIG_URIS: MutableMap<String?, String?>

		fun SchemaType.getDigits(type: Int): Int? {
			return this.getFacet(type)?.stringValue?.toIntOrNull()
		}

		init {
			MAP_COMPATIBILITY_CONFIG_URIS = HashMap()
			MAP_COMPATIBILITY_CONFIG_URIS[COMPATIBILITY_CONFIG_URI] = CONFIG_URI
		}
	}
}