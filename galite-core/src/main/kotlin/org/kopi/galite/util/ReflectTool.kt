/*
 * Copyright (c) 2013-2020 kopiLeft Services SARL, Tunis TN
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
package org.kopi.galite.util

import org.jetbrains.exposed.sql.Table
import org.kopi.galite.common.Actor
import org.kopi.galite.common.Menu
import org.kopi.galite.form.dsl.Form
import org.kopi.galite.form.dsl.FormBlock
import org.kopi.galite.form.dsl.FormBlockIndex
import org.kopi.galite.form.dsl.FormField
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmErasure

/**
 * Class of reflection tools. It contains methods which access
 * elements defined using Galite DSL like formBlocks' fields,
 * indices, actors, etc.. inorder to get information about
 * these components like their names
 */
object ReflectTool {

  /**
   * A function that returns a blockField element
   *
   * @param        formBlock                the formBlock to access
   * @param        index                    the index of the element requested
   *
   * @return       the blockField element of position index
   */
  fun blockFieldAt(formBlock: FormBlock, index: Int): String {
    return elementOfFormBlock(formBlock, FormField::class, index)
  }

  /**
   * A function that returns a blockIndex element
   *
   * @param        formBlock                the formBlock to access
   * @param        index                    the index of the element requested
   *
   * @return       the blockIndex element of position index
   */
  fun blockIndexAt(formBlock: FormBlock, index: Int): String {
    return elementOfFormBlock(formBlock, FormBlockIndex::class, index)
  }

  /**
   * A function that returns a blockActor element
   *
   * @param        form                     the form to access
   * @param        index                    the index of the element requested
   *
   * @return       the formActor element of position index
   */
  fun formActorAt(form: Form, index: Int): String {
    return elementOfForm(form, Actor::class, index)
  }

  /**
   * A function that returns a blockActor element
   *
   * @param        form                     the form to access
   * @param        index                    the index of the element requested
   *
   * @return       the formMenu element of position index
   */
  fun formMenuAt(form: Form, index: Int): String {
    return elementOfForm(form, Menu::class, index)
  }

  /**
   * A function that returns a blockTable element
   *
   * @param        formBlock                the formBlock to access
   * @param        index                    the index of the element requested
   *
   * @return       the blockTable element of position index
   */
  fun blockTableAt(formBlock: FormBlock, index: Int): String {
    return elementOfFormBlock(formBlock, Table::class, index)
  }

  /**
   * A function that returns the element of a particular subclass
   *
   * @param        formBlock                the formBlock to access
   * @param        kClass                   the type of the element requested
   * @param        index                    the index of the element requested
   *
   * @return       the element of position index
   *
   */
  fun elementOfFormBlock(formBlock: FormBlock, kClass: KClass<*>, index: Int): String {
    val formBlockClass = formBlock::class
    val list= formBlockClass.memberProperties.filter {
      it.returnType.jvmErasure.isSubclassOf(kClass)
    }.map { it.name }
    return list[index]
  }

  /**
   * A function that returns the element of a particular subclass
   *
   * @param        form                     the form to access
   * @param        kClass                   the type of the element requested
   * @param        index                    the index of the element requested
   *
   * @return       the element of position index
   *
   */
  fun elementOfForm(form: Form, kClass: KClass<*>, index: Int): String {
    val formClass = form::class
    val list =formClass.memberProperties.filter{
     it.returnType.jvmErasure.isSubclassOf(kClass)
    }.map { it.name }
    return list[index]
  }
}
