/*
 * Copyright (c) 2013-2022 kopiLeft Services SARL, Tunis TN
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
package org.kopi.galite.visual.util

import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmErasure

import org.jetbrains.exposed.sql.Table
import org.kopi.galite.visual.Actor
import org.kopi.galite.visual.dsl.common.Menu
import org.kopi.galite.visual.dsl.form.Form
import org.kopi.galite.visual.dsl.form.Block
import org.kopi.galite.visual.dsl.form.FormBlockIndex
import org.kopi.galite.visual.dsl.form.FormField

/**
 * Class of reflection tools. It contains methods which access
 * elements defined using Galite DSL like blocks' fields,
 * indices, actors, etc.. inorder to get information about
 * these components like their names
 */
object ReflectTool {

  /**
   * A function that returns a blockField element
   *
   * @param        block                the form block to access
   * @param        index                    the index of the element requested
   *
   * @return       the blockField element of position index
   */
  fun blockFieldAt(block: Block, index: Int): String {
    return elementOfFormBlock(block, FormField::class, index)
  }

  /**
   * A function that returns a blockIndex element
   *
   * @param        block                the form block to access
   * @param        index                    the index of the element requested
   *
   * @return       the blockIndex element of position index
   */
  fun blockIndexAt(block: Block, index: Int): String {
    return elementOfFormBlock(block, FormBlockIndex::class, index)
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
   * @param        block                the form block to access
   * @param        index                    the index of the element requested
   *
   * @return       the blockTable element of position index
   */
  fun blockTableAt(block: Block, index: Int): String {
    return elementOfFormBlock(block, Table::class, index)
  }

  /**
   * A function that returns the element of a particular subclass
   *
   * @param        block                the form block to access
   * @param        kClass                   the type of the element requested
   * @param        index                    the index of the element requested
   *
   * @return       the element of position index
   *
   */
  private fun elementOfFormBlock(block: Block, kClass: KClass<*>, index: Int): String {
    val formBlockClass = block::class
    val list = formBlockClass.memberProperties.filter {
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
   */
  private fun elementOfForm(form: Form, kClass: KClass<*>, index: Int): String {
    val formClass = form::class
    val list = formClass.memberProperties.filter {
      it.returnType.jvmErasure.isSubclassOf(kClass)
    }.map { it.name }
    return list[index]
  }
}
