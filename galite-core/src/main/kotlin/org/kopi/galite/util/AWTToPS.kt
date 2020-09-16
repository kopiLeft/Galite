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


import java.awt.*
import java.awt.image.ImageObserver
import java.io.OutputStream
import java.io.PrintStream
import java.text.AttributedCharacterIterator

/**
 * A class to paint in a postscript file instead of screen
 */
class AWTToPS : Graphics() {
  override fun getClipBounds(): Rectangle {
    TODO("Not yet implemented")
  }

  override fun drawPolyline(xPoints: IntArray?, yPoints: IntArray?, nPoints: Int) {
    TODO("Not yet implemented")
  }

  override fun drawLine(x1: Int, y1: Int, x2: Int, y2: Int) {
    TODO("Not yet implemented")
  }

  override fun copyArea(x: Int, y: Int, width: Int, height: Int, dx: Int, dy: Int) {
    TODO("Not yet implemented")
  }

  override fun create(): Graphics {
    TODO("Not yet implemented")
  }

  override fun fillArc(x: Int, y: Int, width: Int, height: Int, startAngle: Int, arcAngle: Int) {
    TODO("Not yet implemented")
  }

  override fun drawOval(x: Int, y: Int, width: Int, height: Int) {
    TODO("Not yet implemented")
  }

  override fun drawString(str: String, x: Int, y: Int) {
    TODO("Not yet implemented")
  }

  override fun drawString(iterator: AttributedCharacterIterator?, x: Int, y: Int) {
    TODO("Not yet implemented")
  }

  override fun clipRect(x: Int, y: Int, width: Int, height: Int) {
    TODO("Not yet implemented")
  }

  override fun clearRect(x: Int, y: Int, width: Int, height: Int) {
    TODO("Not yet implemented")
  }

  override fun drawPolygon(xPoints: IntArray?, yPoints: IntArray?, nPoints: Int) {
    TODO("Not yet implemented")
  }

  override fun setPaintMode() {
    TODO("Not yet implemented")
  }

  override fun getColor(): Color {
    TODO("Not yet implemented")
  }

  override fun fillRect(x: Int, y: Int, width: Int, height: Int) {
    TODO("Not yet implemented")
  }

  override fun drawRoundRect(x: Int, y: Int, width: Int, height: Int, arcWidth: Int, arcHeight: Int) {
    TODO("Not yet implemented")
  }

  override fun drawImage(img: Image?, x: Int, y: Int, observer: ImageObserver?): Boolean {
    TODO("Not yet implemented")
  }

  override fun drawImage(img: Image?, x: Int, y: Int, width: Int, height: Int, observer: ImageObserver?): Boolean {
    TODO("Not yet implemented")
  }

  override fun drawImage(img: Image?, x: Int, y: Int, bgcolor: Color?, observer: ImageObserver?): Boolean {
    TODO("Not yet implemented")
  }

  override fun drawImage(img: Image?, x: Int, y: Int, width: Int, height: Int, bgcolor: Color?, observer: ImageObserver?): Boolean {
    TODO("Not yet implemented")
  }

  override fun drawImage(img: Image?, dx1: Int, dy1: Int, dx2: Int, dy2: Int, sx1: Int, sy1: Int, sx2: Int, sy2: Int, observer: ImageObserver?): Boolean {
    TODO("Not yet implemented")
  }

  override fun drawImage(img: Image?, dx1: Int, dy1: Int, dx2: Int, dy2: Int, sx1: Int, sy1: Int, sx2: Int, sy2: Int, bgcolor: Color?, observer: ImageObserver?): Boolean {
    TODO("Not yet implemented")
  }

  override fun getFontMetrics(f: Font?): FontMetrics {
    TODO("Not yet implemented")
  }

  override fun setXORMode(c1: Color?) {
    TODO("Not yet implemented")
  }

  override fun translate(x: Int, y: Int) {
    TODO("Not yet implemented")
  }

  override fun fillPolygon(xPoints: IntArray?, yPoints: IntArray?, nPoints: Int) {
    TODO("Not yet implemented")
  }

  override fun setFont(font: Font?) {
    TODO("Not yet implemented")
  }

  override fun setColor(c: Color?) {
    TODO("Not yet implemented")
  }

  override fun getFont(): Font {
    TODO("Not yet implemented")
  }

  override fun fillOval(x: Int, y: Int, width: Int, height: Int) {
    TODO("Not yet implemented")
  }

  override fun getClip(): Shape {
    TODO("Not yet implemented")
  }

  override fun fillRoundRect(x: Int, y: Int, width: Int, height: Int, arcWidth: Int, arcHeight: Int) {
    TODO("Not yet implemented")
  }

  override fun drawArc(x: Int, y: Int, width: Int, height: Int, startAngle: Int, arcAngle: Int) {
    TODO("Not yet implemented")
  }

  override fun dispose() {
    TODO("Not yet implemented")
  }

  override fun setClip(x: Int, y: Int, width: Int, height: Int) {
    TODO("Not yet implemented")
  }

  override fun setClip(clip: Shape?) {
    TODO("Not yet implemented")
  }
}