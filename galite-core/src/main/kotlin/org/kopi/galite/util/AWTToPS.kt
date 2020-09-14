/*
 * Copyright (c) 1990-2016 kopiRight Managed Solutions GmbH
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
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * $Id: AWTToPS.java 34997 2016-12-01 09:51:43Z hacheni $
 */
package org.kopi.galite.util


import java.awt.Graphics
import java.io.OutputStream
import java.io.PrintStream

/**
 * A class to paint in a postscript file instead of screen
 */
class AWTToPS : Graphics() {


  constructor(stream: PrintStream, clone:Boolean) {
    this.stream = stream;
    if (!clone) {
      emitProlog();
    }
  }

  constructor(stream: OutputStream, clone: Boolean) {
    this(PrintStream(stream), clone)
  }

  fun AWTToPS(o: OutputStream?) {
    this(o, false)
  }

  /**
   *
   */
  protected fun emitProlog() {
    stream.println("%!PS-Adobe-2.0")
  }
  private val stream: PrintStream }