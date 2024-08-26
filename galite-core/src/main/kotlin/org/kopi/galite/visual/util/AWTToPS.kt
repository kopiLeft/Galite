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

package org.kopi.galite.visual.util

import java.awt.Color
import java.awt.Font
import java.awt.FontMetrics
import java.awt.Graphics
import java.awt.Image
import java.awt.Polygon
import java.awt.Rectangle
import java.awt.Shape
import java.awt.Toolkit
import java.awt.image.ImageObserver
import java.awt.image.PixelGrabber
import java.io.OutputStream
import java.io.PrintStream
import java.text.AttributedCharacterIterator

import kotlin.experimental.or

import org.kopi.galite.util.base.InconsistencyException

/**
 * A class to paint in a postscript file instead of screen
 */
class AWTToPS(private val stream: PrintStream, clone: Boolean) : Graphics() {

  private val toolkit = Toolkit.getDefaultToolkit()
  private var oldSize = -1
  private var oldName = "XXXX"
  private var lastRed = -1
  private var lastGreen = -1
  private var lastBlue = -1
  private var clr = Color.black
  private var backClr = Color.white
  private var font = Font("Helvetica", Font.PLAIN, 12)
  private var clippingRect = Rectangle(0, 0, PAGEWIDTH, PAGEHEIGHT)
  private var transColor = 0

  init {
    if (!clone) {
      emitProlog()
    }
  }

  constructor(stream: OutputStream, clone: Boolean) : this(PrintStream(stream), clone)

  constructor(stream: OutputStream) : this(stream, false)

  fun setBoundingBox(x: Int, y: Int, width: Int, height: Int) {
    stream.println("%%BoundingBox: $x $y $width $height")
  }

  fun setScale(x: Double, y: Double) {
    emitScale(x, y)
  }

  /**
   * Creates a new AWTToPS Object that is a copy of the original AWTToPS Object.
   */
  override fun create(): Graphics {
    val graphics = AWTToPS(stream, true)
    graphics.font = font
    graphics.clippingRect = clippingRect
    graphics.clr = clr
    graphics.transColor = transColor
    return graphics
  }

  /**
   * Creates a new Graphics Object with the specified parameters,
   * based on the original
   * Graphics Object.
   * This method translates the specified parameters, x and y, to
   * the proper origin coordinates and then clips the Graphics Object to the
   * area.
   * @param x the x coordinate
   * @param y the y coordinate
   * @param width the width of the area
   * @param height the height of the area
   * @see translate
   */
  override fun create(x: Int, y: Int, width: Int, height: Int): Graphics {
    val graphics = create()
    return graphics.apply {
      translate(x, y)
      clipRect(0, 0, width, height)
    }
  }

  /**
   * Translates the specified parameters into the origin of
   * the graphics context. All subsequent
   * operations on this graphics context will be relative to this origin.
   * @param x the x coordinate
   * @param y the y coordinate
   * @see scale
   */
  override fun translate(x: Int, y: Int) {
    emitTranslate(x, -y)
  }

  /**
   * Scales the graphics context. All subsequent operations on this
   * graphics context will be affected.
   * @param sx the scaled x coordinate
   * @param sy the scaled y coordinate
   * @see translate
   */
  fun scale(sx: Float, sy: Float) {
    emitScale(sx.toDouble(), sy.toDouble())
  }

  /**
   * Gets the current color.
   * @see setColor
   */
  override fun getColor(): Color = clr

  /**
   * Gets the current color.
   * @see setColor
   */
  fun setBackground(c: Color) {
    backClr = c
  }

  fun setTransparentColor(trans: Color) {
    transColor = (trans.red shl 16) + (trans.green shl 8) + trans.blue
  }

  /**
   * Sets the current color to the specified color. All subsequent graphics operations
   * will use this specified color.
   * @param c the color to be set
   * @see Color
   *
   * @see getColor
   */
  override fun setColor(c: Color?) {
    if (c != null) {
      clr = c
      if (clr.red != lastRed
              || clr.green != lastGreen
              || clr.blue != lastBlue) {
        with(stream) {
          print(clr.red.also { lastRed = it } / 255.0)
          print(" ")
          print(clr.green.also { lastGreen = it } / 255.0)
          print(" ")
          print(clr.blue.also { lastBlue = it } / 255.0)
          println(" setrgbcolor")
        }
      }
    }
  }

  /**
   * Sets the default paint mode to overwrite the destination with the
   * current color. PstreamtScript has only paint mode.
   */
  override fun setPaintMode() {}

  /**
   * Sets the paint mode to alternate between the current color
   * and the new specified color. PstreamtScript does not support XOR mode.
   * @param c1 the second color
   */
  override fun setXORMode(c1: Color) {
    // not used in ps
  }

  /**
   * Gets the current font.
   * @see setFont
   */
  override fun getFont(): Font {
    return font
  }

  /**
   * Sets the font for all subsequent text-drawing operations.
   *
   * @param font the specified font
   *
   * @see Font
   * @see getFont
   * @see drawString
   * @see drawBytes
   * @see drawChars
   */
  override fun setFont(font: Font?) {
    if (font != null) {
      this.font = font
      val javaName = font.name.lowercase()
      val javaStyle = font.style
      var psName: String
      when {
        javaName == "symbol" -> psName = "Symbol"
        javaName.startsWith("times") -> {
          psName = "Times-"
          when (javaStyle) {
            Font.PLAIN -> psName += "Roman"
            Font.BOLD -> psName += "Bold"
            Font.ITALIC -> psName += "Italic"
            Font.ITALIC + Font.BOLD -> psName += "BoldItalic"
          }
        }
        else -> {
          psName = if (javaName == "helvetica") "Helvetica" else "Courier"
          when (javaStyle) {
            Font.PLAIN -> {
            }
            Font.BOLD -> psName += "-Bold"
            Font.ITALIC -> psName += "-Oblique"
            Font.ITALIC + Font.BOLD -> psName += "-BoldOblique"
          }
        }
      }

      if (psName != oldName || font.size != oldSize) {
        oldSize = font.size
        oldName = psName
        with(stream) {
          println("/$psName findfont")
          print(font.size)
          println(" scalefont setfont")
        }
      }
    }
  }

  /**
   * Gets the current font metrics.
   * @see getFont
   */
  override fun getFontMetrics(): FontMetrics = getFontMetrics(getFont())

  /**
   * Gets the current font metrics for the specified font.
   *
   * @param font the specified font
   *
   * @see getFont
   * @see getFontMetrics
   */
  override fun getFontMetrics(font: Font): FontMetrics = toolkit.getFontMetrics(font)

  /**
   * Clips to a rectangle. The resulting clipping area is the
   * intersection of the current clipping area and the specified
   * rectangle. Graphic operations have no effect outside of the
   * clipping area.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   *
   * @see getClipRect
   */
  override fun clipRect(x: Int, y: Int, width: Int, height: Int) {
    val z = swapCoord(y)
    clippingRect = Rectangle(x, z, width, height)
    stream.println("initclip")
    emitMoveto(x, z)
    emitLineto(x + width, z)
    emitLineto(x + width, z - height)
    emitLineto(x, z - height)
    stream.println("closepath eoclip newpath")
  }

  /**
   * Copies an area of the screen.
   *
   * @param x the x-coordinate of the source
   * @param y the y-coordinate of the source
   * @param width the width
   * @param height the height
   * @param dx the horizontal distance
   * @param dy the vertical distance
   * Note: copyArea not supported by PstreamtScript
   */
  override fun copyArea(x: Int, y: Int, width: Int, height: Int, dx: Int, dy: Int) {
    throw InconsistencyException("copyArea not supported")
  }

  /**
   * Draws a line between the coordinates (x1, y1) and (x2, y2). The line is drawn
   * below and to the left of the logical coordinates.
   *
   * @param x1 the first point's x coordinate
   * @param y1 the first point's y coordinate
   * @param x2 the second point's x coordinate
   * @param y2 the second point's y coordinate
   */
  override fun drawLine(x1: Int, y1: Int, x2: Int, y2: Int) {
    val z1 = swapCoord(y1)
    val z2 = swapCoord(y2)
    emitMoveto(x1, z1)
    emitLineto(x2, z2)
    stream.println("stroke")
  }

  /**
   * Fills the specified rectangle with the current color.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   * @see drawRect
   *
   * @see clearRect
   */
  override fun fillRect(x: Int, y: Int, width: Int, height: Int) {
    doRect(x, y, width, height, true)
  }

  /**
   * Draws the outline of the specified rectangle using the current color.
   * Use drawRect(x, y, width-1, height-1) to draw the outline inside the specified
   * rectangle.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   * @see fillRect
   *
   * @see clearRect
   */
  override fun drawRect(x: Int, y: Int, width: Int, height: Int) {
    doRect(x, y, width, height, false)
  }

  /**
   * Clears the specified rectangle by filling it with the current background color
   * of the current drawing surface.
   * Which drawing surface it selects depends on how the graphics context
   * was created.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   * @see fillRect
   *
   * @see drawRect
   */
  override fun clearRect(x: Int, y: Int, width: Int, height: Int) {
    stream.println("gsave")
    val c = color
    color = backClr
    doRect(x, y, width, height, true)
    color = c
    stream.println("grestore")
  }

  /**
   * Draws an outlined rounded corner rectangle using the current color.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   * @param arcWidth the diameter of the arc
   * @param arcHeight the radius of the arc
   * @see fillRoundRect
   */
  override fun drawRoundRect(x: Int, y: Int, width: Int, height: Int, arcWidth: Int, arcHeight: Int) {
    doRoundRect(x, y, width, height, arcWidth, arcHeight, false)
  }

  /**
   * Draws a rounded rectangle filled in with the current color.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   * @param arcWidth the diameter of the arc
   * @param arcHeight the radius of the arc
   * @see drawRoundRect
   */
  override fun fillRoundRect(x: Int, y: Int, width: Int, height: Int, arcWidth: Int, arcHeight: Int) {
    doRoundRect(x, y, width, height, arcWidth, arcHeight, true)
  }

  /**
   * Draws a highlighted 3-D rectangle.
   * @param x the x coordinate
   * @param y the y coordinate
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   * @param raised a boolean that states whether the rectangle is raised or not
   */
  override fun draw3DRect(x: Int, y: Int, width: Int, height: Int, raised: Boolean) {
    val c = color
    val brighter = c.brighter()
    val darker = c.darker()
    color = if (raised) brighter else darker
    drawLine(x, y, x, y + height)
    drawLine(x + 1, y, x + width - 1, y)
    color = if (raised) darker else brighter
    drawLine(x + 1, y + height, x + width, y + height)
    drawLine(x + width, y, x + width, y + height)
    color = c
  }

  /**
   * Paints a highlighted 3-D rectangle using the current color.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   * @param raised a boolean that states whether the rectangle is raised or not
   */
  override fun fill3DRect(x: Int, y: Int, width: Int, height: Int, raised: Boolean) {
    val c = color
    val brighter = c.brighter()
    val darker = c.darker()
    if (!raised) {
      color = darker
    }
    fillRect(x + 1, y + 1, width - 2, height - 2)
    color = if (raised) brighter else darker
    drawLine(x, y, x, y + height - 1)
    drawLine(x + 1, y, x + width - 2, y)
    color = if (raised) darker else brighter
    drawLine(x + 1, y + height - 1, x + width - 1, y + height - 1)
    drawLine(x + width - 1, y, x + width - 1, y + height - 1)
    color = c
  }

  /**
   * Draws an oval inside the specified rectangle using the current color.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   * @see fillOval
   */
  override fun drawOval(x: Int, y: Int, width: Int, height: Int) {
    doArc(x, y, width, height, 0, 360, false)
  }

  /**
   * Fills an oval inside the specified rectangle using the current color.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   * @see drawOval
   */
  override fun fillOval(x: Int, y: Int, width: Int, height: Int) {
    doArc(x, y, width, height, 0, 360, true)
  }

  /**
   * Draws an arc bounded by the specified rectangle from startAngle to
   * endAngle. 0 degrees is at the 3-o'clock pstreamition.Pstreamitive arc
   * angles indicate counter-clockwise rotations, negative arc angles are
   * drawn clockwise.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   * @param startAngle the beginning angle
   * @param arcAngle the angle of the arc (relative to startAngle).
   * @see fillArc
   */
  override fun drawArc(x: Int, y: Int, width: Int, height: Int, startAngle: Int, arcAngle: Int) {
    doArc(x, y, width, height, startAngle, arcAngle, false)
  }

  /**
   * Fills an arc using the current color. This generates a pie shape.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   * @param width the width of the arc
   * @param height the height of the arc
   * @param startAngle the beginning angle
   * @param arcAngle the angle of the arc (relative to startAngle).
   * @see drawArc
   */
  override fun fillArc(x: Int, y: Int, width: Int, height: Int, startAngle: Int, arcAngle: Int) {
    doArc(x, y, width, height, startAngle, arcAngle, true)
  }

  /**
   * Draws a polygon defined by an array of x points and y points.
   *
   * @param xPoints an array of x points
   * @param yPoints an array of y points
   * @param nPoints the total number of points
   * @see fillPolygon
   */
  override fun drawPolygon(xPoints: IntArray, yPoints: IntArray, nPoints: Int) {
    doPolygon(xPoints, yPoints, nPoints, false)
  }

  /**
   * Draws a polygon defined by the specified point.
   * @param p the specified polygon
   * @see fillPolygon
   */
  override fun drawPolygon(p: Polygon) {
    doPolygon(p.xpoints, p.ypoints, p.npoints, false)
  }

  /**
   * Fills a polygon with the current color.
   * @param xPoints an array of x points
   * @param yPoints an array of y points
   * @param nPoints the total number of points
   * @see drawPolygon
   */
  override fun fillPolygon(xPoints: IntArray, yPoints: IntArray, nPoints: Int) {
    doPolygon(xPoints, yPoints, nPoints, true)
  }

  /**
   * Fills the specified polygon with the current color.
   * @param p the polygon
   * @see drawPolygon
   */
  override fun fillPolygon(p: Polygon) {
    doPolygon(p.xpoints, p.ypoints, p.npoints, true)
  }

  /**
   * Draws the specified String using the current font and color.
   * The x, y pstreamition is the starting point of the baseline of the String.
   *
   * @param str the String to be drawn
   * @param x the x coordinate
   * @param y the y coordinate
   * @see drawChars
   *
   * @see drawBytes
   */
  override fun drawString(str: String, x: Int, y: Int) {
    val z = swapCoord(y)
    emitMoveto(x, z)
    with(stream) {
      print("(")
      print(str)
      println(") show stroke")
    }
  }

  /**
   * !!! coco 20/12/00 : this function has been added to compile with JDK 1.3
   * Draws the text given by the specified iterator, using this graphics context's
   * current color. The iterator has to specify a font for each character.
   * The baseline of the first character is at position (x, y) in this graphics
   * context's coordinate system.
   *
   * @param iterator the iterator whose text is to be drawn
   * @param x the x coordinate
   * @param y the y coordinate
   */
  override fun drawString(iterator: AttributedCharacterIterator,
                          x: Int,
                          y: Int) {
    System.err.println("the function drawString in kopi/galite/visual/report/AWTToPS")
  }

  /**
   * Draws the specified characters using the current font and color.
   *
   * @param data the array of characters to be drawn
   * @param offset the start offset in the data
   * @param length the number of characters to be drawn
   * @param x the x coordinate
   * @param y the y coordinate
   *
   * @see drawString
   * @see drawBytes
   */
  override fun drawChars(data: CharArray, offset: Int, length: Int, x: Int, y: Int) {
    drawString(String(data, offset, length), x, y)
  }

  /**
   * Draws the specified bytes using the current font and color.
   *
   * @param data the data to be drawn
   * @param offset the start offset in the data
   * @param length the number of bytes that are drawn
   * @param x the x coordinate
   * @param y the y coordinate
   *
   * @see drawString
   * @see drawChars
   */
  override fun drawBytes(data: ByteArray, offset: Int, length: Int, x: Int, y: Int) {
    drawString(String(data, offset, length), x, y)
  }

  /**
   * Draws the specified image at the specified coordinate (x, y). If the image is
   * incomplete the image observer will be notified later.
   *
   * @param img the specified image to be drawn
   * @param width the image width
   * @param height the image height
   *
   * @see Image
   * @see ImageObserver
   */
  fun drawImage(img: Image, width: Int, height: Int): Boolean {
    return drawImage(img, 0, swapCoord(0) - height, width, height, null)
  }

  /**
   * Draws the specified image at the specified coordinate (x, y). If the image is
   * incomplete the image observer will be notified later.
   *
   * @param img the specified image to be drawn
   * @param x the x coordinate
   * @param y the y coordinate
   * @param observer notifies if the image is complete or not
   *
   * @see Image
   * @see ImageObserver
   */
  override fun drawImage(img: Image, x: Int, y: Int, observer: ImageObserver): Boolean {
    return doImage(img, x, y, 0, 0, observer, null)
  }

  /**
   * Draws the specified image inside the specified rectangle. The image is
   * scaled if necessary. If the image is incomplete the image observer will be
   * notified later.
   *
   * @param img the specified image to be drawn
   * @param x the x coordinate
   * @param y the y coordinate
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   * @param observer notifies if the image is complete or not
   *
   * @see Image
   * @see ImageObserver
   */
  override fun drawImage(img: Image, x: Int, y: Int, width: Int, height: Int, observer: ImageObserver?): Boolean {
    return doImage(img, x, y, width, height, observer, null)
  }

  /**
   * Draws the specified image at the specified coordinate (x, y). If the image is
   * incomplete the image observer will be notified later.
   *
   * @param img the specified image to be drawn
   * @param x the x coordinate
   * @param y the y coordinate
   * @param bgcolor the background color
   * @param observer notifies if the image is complete or not
   *
   * @see Image
   * @see ImageObserver
   */
  override fun drawImage(img: Image, x: Int, y: Int, bgcolor: Color, observer: ImageObserver): Boolean {
    return doImage(img, x, y, 0, 0, observer, bgcolor)
  }

  /**
   * Draws the specified image inside the specified rectangle. The image is
   * scaled if necessary. If the image is incomplete the image observer will be
   * notified later.
   *
   * @param img the specified image to be drawn
   * @param x the x coordinate
   * @param y the y coordinate
   * @param width the width of the rectangle
   * @param height the height of the rectangle
   * @param bgcolor the background color
   * @param observer notifies if the image is complete or not
   *
   * @see Image
   * @see ImageObserver
   */
  override fun drawImage(img: Image,
                         x: Int,
                         y: Int,
                         width: Int,
                         height: Int,
                         bgcolor: Color,
                         observer: ImageObserver): Boolean {
    return doImage(img, x, y, width, height, observer, bgcolor)
  }

  /**
   * Disposes of this graphics context.  The Graphics context cannot be used after
   * being disposed of.
   *
   * @see finalize
   */
  override fun dispose() {
    stream.flush()
  }

  /**
   * Disposes of this graphics context once it is no longer referenced.
   *
   * @see dispose
   */
  @Deprecated("Overrides deprecated member")
  override fun finalize() {
    dispose()
  }

  override fun toString(): String = javaClass.name + "[font=" + getFont() + ",color=" + color + "]"

  protected fun swapCoord(pos: Int): Int = PAGEHEIGHT - pos

  protected fun emitProlog() {
    stream.println("%!PS-Adobe-2.0")
  }

  protected fun emitColorImageProlog(xdim: Int) {
    stream.print("/pix ")
    stream.print(xdim * 3)
    stream.println(" string def")

    stream.println("% define space for color conversions")
    stream.print("/grays ")
    stream.print(xdim)
    stream.println(" string def  % space for gray scale line")
    stream.println("/npixls 0 def")
    stream.println("/rgbindx 0 def")

    stream.println("% define 'colorimage' if it isn't defined")
    stream.println("%   ('colortogray' and 'mergeprocs' come from xwd2ps")
    stream.println("%     via xgrab)")
    stream.println("/colorimage where   % do we know about 'colorimage'?")
    stream.println("{ pop }           % yes: pop off the 'dict' returned")
    stream.println("{                 % no:  define one")
    stream.println("/colortogray {  % define an RGB->I function")
    stream.println("/rgbdata exch store    % call input 'rgbdata'")
    stream.println("rgbdata length 3 idiv")
    stream.println("/npixls exch store")
    stream.println("/rgbindx 0 store")
    stream.println("0 1 npixls 1 sub {")
    stream.println("grays exch")
    stream.println("rgbdata rgbindx       get 20 mul    % Red")
    stream.println("rgbdata rgbindx 1 add get 32 mul    % Green")
    stream.println("rgbdata rgbindx 2 add get 12 mul    % Blue")
    stream.println("add add 64 idiv      % I = .5G + .31R + .18B")
    stream.println("put")
    stream.println("/rgbindx rgbindx 3 add store")
    stream.println("} for")
    stream.println("grays 0 npixls getinterval")
    stream.println("} bind def")
    stream.println("")
    stream.println("% Utility procedure for colorimage operator.")
    stream.println("% This procedure takes two procedures off the")
    stream.println("% stack and merges them into a single procedure.")
    stream.println("")
    stream.println("/mergeprocs { % def")
    stream.println("dup length")
    stream.println("3 -1 roll")
    stream.println("dup")
    stream.println("length")
    stream.println("dup")
    stream.println("5 1 roll")
    stream.println("3 -1 roll")
    stream.println("add")
    stream.println("array cvx")
    stream.println("dup")
    stream.println("3 -1 roll")
    stream.println("0 exch")
    stream.println("putinterval")
    stream.println("dup")
    stream.println("4 2 roll")
    stream.println("putinterval")
    stream.println("} bind def")
    stream.println("")
    stream.println("/colorimage { % def")
    stream.println("pop pop     % remove 'false 3' operands")
    stream.println("{colortogray} mergeprocs")
    stream.println("image")
    stream.println("} bind def")
    stream.println("} ifelse          % end of 'false' case")
  }

  fun gsave() {
    stream.println("gsave")
  }

  fun grestore() {
    stream.println("grestore")
  }

  fun emitThis(s: String) {
    stream.println(s)
  }

  override fun getClipBounds(): Rectangle = clippingRect

  override fun setClip(a: Int, b: Int, c: Int, d: Int) {
    clipRect(a, b, c, d)
  }

  fun showPage() {
    stream.println("showpage")
  }

  override fun getClip(): Shape = clippingRect

  override fun setClip(s: Shape) {
    clippingRect = s as Rectangle
  }

  override fun drawPolyline(param1: IntArray, param2: IntArray, param3: Int) {
    throw NotImplementedError()
  }

  override fun drawImage(param1: Image,
                         param2: Int,
                         param3: Int,
                         param4: Int,
                         param5: Int,
                         param6: Int,
                         param7: Int,
                         param8: Int,
                         param9: Int,
                         param10: ImageObserver): Boolean {
    return drawImage(param1, param2, param3, param4, param5, param10)
  }

  override fun drawImage(param1: Image,
                         param2: Int,
                         param3: Int,
                         param4: Int,
                         param5: Int,
                         param6: Int,
                         param7: Int,
                         param8: Int,
                         param9: Int,
                         param10: Color,
                         param11: ImageObserver): Boolean {
    return drawImage(param1, param2, param3, param4, param5, param11)
  }

  // ----------------------------------------------------------------------
  // PRIVATE METHODS
  // ----------------------------------------------------------------------
  private fun doPolygon(xPoints: IntArray, yPoints: IntArray, nPoints: Int, fill: Boolean) {
    if (nPoints < 2) {
      return
    }
    val newYPoints = IntArray(nPoints)
    for (i in 0 until nPoints) {
      newYPoints[i] = swapCoord(yPoints[i])
    }
    emitMoveto(xPoints[0], newYPoints[0])

    // !!! should start at i = 1 ???
    for (i in 0 until nPoints) {
      emitLineto(xPoints[0], newYPoints[0])
    }
    stream.println(if (fill) "eofill" else "stroke")
  }

  private fun doRect(x: Int, y: Int, width: Int, height: Int, fill: Boolean) {
    val z = swapCoord(y)

    emitMoveto(x, z)
    emitLineto(x + width, z)
    emitLineto(x + width, z - height)
    emitLineto(x, z - height)
    emitLineto(x, z)
    stream.println(if (fill) "eofill" else "stroke")
  }

  private fun doRoundRect(x: Int, y: Int, width: Int, height: Int, arcWidth: Int, arcHeight: Int, fill: Boolean) {
    val z = swapCoord(y)

    emitMoveto(x + arcHeight, z)

    // top, left to right
    stream.print(x + width)
    stream.print(" ")
    stream.print(z)
    stream.print(" ")
    stream.print(x + width)
    stream.print(" ")
    stream.print(z - height)
    stream.print(" ")
    stream.print(arcHeight)
    stream.println(" arcto")
    stream.println("4 {pop} repeat")

    // right, top to bottom
    stream.print(x + width)
    stream.print(" ")
    stream.print(z - height)
    stream.print(" ")
    stream.print(x)
    stream.print(" ")
    stream.print(z - height)
    stream.print(" ")
    stream.print(arcHeight)
    stream.println(" arcto")
    stream.println("4 {pop} repeat")

    // top, left to right
    stream.print(x)
    stream.print(" ")
    stream.print(z - height)
    stream.print(" ")
    stream.print(x)
    stream.print(" ")
    stream.print(z)
    stream.print(" ")
    stream.print(arcHeight)
    stream.println(" arcto")
    stream.println("4 {pop} repeat")

    // left, top to bottom
    stream.print(x)
    stream.print(" ")
    stream.print(z)
    stream.print(" ")
    stream.print(x + width)
    stream.print(" ")
    stream.print(z)
    stream.print(" ")
    stream.print(arcHeight)
    stream.println(" arcto")
    stream.println("4 {pop} repeat")
    stream.println(if (fill) "eofill" else "stroke")
  }

  private fun doArc(x: Int, y: Int, width: Int, height: Int, startAngle: Int, arcAngle: Int, fill: Boolean) {
    val z = swapCoord(y)

    stream.println("gsave")

    // cx, cy is the center of the arc
    // translate the page to be centered there
    emitTranslate(x + width.toFloat() / 2.0, z - height.toFloat() / 2.0)
    emitScale(1.0, (height.toFloat() / width.toFloat()).toDouble())
    if (fill) {
      emitMoveto(0, 0)
    }

    // now draw the arc.
    val endAngle = startAngle + arcAngle.toFloat()
    stream.print("0 0 ")
    stream.print(width.toFloat() / 2.0)
    stream.print(" ")
    stream.print(startAngle)
    stream.print(" ")
    stream.print(endAngle)
    stream.println(" arc")
    if (fill) {
      stream.println("closepath eofill")
    } else {
      stream.println("stroke")
    }
    stream.println("grestore")
  }

  fun doColorImage(img: Image, x: Int, y: Int, width: Int, height: Int,
                   observer: ImageObserver, bgcolor: Color?): Boolean {
    // This class fetches the pixels in its constructor.
    val pc = PixelConsumer(img)
    var i = 0

    while (i < 10000 && !pc.isComplete) {
      try {
        Thread.sleep(1)
      } catch (e: Throwable) {
        // ignore it
      }
      i++
    }
    val imgWidth = img.getWidth(observer)
    val imgHeight = img.getHeight(observer)
    val pix = IntArray(imgWidth * imgHeight)
    val pg = PixelGrabber(img, 0, 0, imgWidth, imgHeight, pix, 0, imgWidth)
    var result = false
    try {
      result = pg.grabPixels()
    } catch (ie: InterruptedException) {
      // nothing
    } finally {
      if (!result) {
        stream.println("%warning: error on image grab")
        System.err.println("warning: error on image grab: " + pg.status)
        return false
      }
    }
    gsave()
    stream.println("currentpoint translate")
    stream.println("% build a temporary dictionary")
    stream.println("20 dict begin")
    emitColorImageProlog(imgWidth)
    stream.println("% lower left corner")
    emitTranslate(x, y)
    stream.println("% size of image")
    emitScale(width, height)
    stream.print(imgWidth)
    stream.print(" ")
    stream.print(imgHeight)
    stream.println(" 8")
    stream.print("[")
    stream.print(imgWidth)
    stream.print(" 0 0 -")
    stream.print(imgHeight)
    stream.print(" 0 ")
    stream.print(0)
    stream.println("]")
    stream.println("{currentfile pix readhexstring pop}")
    stream.println("false 3 colorimage")
    stream.println("")
    var offset: Int
    val sb = CharArray(charsPerRow + 1)
    val bg = bgcolor?.rgb ?: -1
    for (k in 0 until imgHeight) {
      offset = 0
      for (j in 0 until imgWidth) {
        val coord = k * imgWidth + j
        var n = pix[coord]
        val alpha = n and -0x1000000
        if (alpha == 0) {
          n = bg
        }
        sb[offset++] = hd[n and 0xF00000 shr 20]
        sb[offset++] = hd[n and 0xF0000 shr 16]
        sb[offset++] = hd[n and 0xF000 shr 12]
        sb[offset++] = hd[n and 0xF00 shr 8]
        sb[offset++] = hd[n and 0xF0 shr 4]
        sb[offset++] = hd[n and 0xF]
        if (offset >= charsPerRow) {
          stream.println(String(sb, 0, offset))
          stream.println()
          offset = 0
        }
      }
      if (offset != 0) {
        stream.println(String(sb, 0, offset))
        stream.println()
      }
    }
    stream.println()
    stream.println("end")
    grestore()
    return true
  }

  fun doImage(img: Image, x: Int, y: Int, width: Int, height: Int, observer: ImageObserver?, bgcolor: Color?): Boolean {
    return doImage(PixelConsumer(img), x, y, width, height, observer, bgcolor)
  }

  fun doImage(pc: PixelConsumer,
              x: Int,
              y: Int,
              width: Int,
              height: Int,
              observer: ImageObserver?,
              bgcolor: Color?): Boolean {
    val z = swapCoord(y)
    var imageWidth = width
    var imageHeight = height
    stream.println("gsave")
    stream.println("20 dict begin")
    emitColorImageProlog(pc.dimensions!!.width)
    emitTranslate(x, z)

    // compute image size. First of all, if width or height is 0, image is 1:1.
    if (imageHeight == 0 || imageWidth == 0) {
      imageHeight = pc.dimensions!!.height
      imageWidth = pc.dimensions!!.width
    }
    emitScale(imageWidth, imageHeight)
    stream.print(pc.dimensions!!.width)
    stream.print(" ")
    stream.print(pc.dimensions!!.height)
    stream.println(" 8")
    stream.print("[")
    stream.print("0 ")
    stream.print(pc.dimensions!!.width)
    stream.print(" -" + pc.dimensions!!.height)
    stream.print(" 0 0 0")
    stream.println("]")
    stream.println("{currentfile pix readhexstring pop}")
    stream.println("false 3 colorimage")
    stream.println("")
    var offset: Int
    // array to hold a line of pixel data
    val sb = CharArray(charsPerRow + 1)
    for (i in 0 until pc.dimensions!!.height) {
      offset = 0
      if (bgcolor == null) {
        for (j in 0 until pc.dimensions!!.width) {
          val n = if (!pc.isTransparent(i, j)) transColor else pc.getPixelAt(i, j)
          sb[offset++] = hd[n and 0xF00000 shr 20]
          sb[offset++] = hd[n and 0xF0000 shr 16]
          sb[offset++] = hd[n and 0xF000 shr 12]
          sb[offset++] = hd[n and 0xF00 shr 8]
          sb[offset++] = hd[n and 0xF0 shr 4]
          sb[offset++] = hd[n and 0xF]
          if (offset >= charsPerRow) {
            stream.println(String(sb, 0, offset))
            offset = 0
          }
        }
      } else {
        for (j in 0 until pc.dimensions!!.width) {
          val bg = bgcolor.green shl 16 + bgcolor.blue shl 8 + bgcolor.red
          val fg = clr.green shl 16 + clr.blue shl 8 + clr.red
          val n = if (pc.getPixelAt(i, j) == 1) fg else bg
          sb[offset++] = hd[n and 0xF0]
          sb[offset++] = hd[n and 0xF]
          sb[offset++] = hd[n and 0xF000]
          sb[offset++] = hd[n and 0xF00]
          sb[offset++] = hd[n and 0xF00000]
          sb[offset++] = hd[n and 0xF0000]
          if (offset >= charsPerRow) {
            stream.println(String(sb, 0, offset))
            offset = 0
          }
        }
      }
      // print partial rows
      if (offset != 0) {
        stream.println(String(sb, 0, offset))
      }
    }
    stream.println("")
    stream.println("end")
    stream.println("grestore")
    return true
  }

  fun doBWImage(img: Image,
                x: Int,
                y: Int,
                width: Int,
                height: Int,
                observer: ImageObserver,
                bgcolor: Color?): Boolean {
    // This class fetches the pixels in its constructor.
    var imageWidth = width
    var imageHeight = height
    val pc = PixelConsumer(img)
    run {
      var i = 0
      while (i < 10000 && !pc.isComplete) {
        try {
          Thread.sleep(1)
        } catch (e: Throwable) {
          // ignore it
        }
        i++
      }
    }
    if (pc.dimensions == null) {
      return false
    }
    stream.println("gsave")
    stream.println("currentpoint translate")
    stream.println("% BLACK AND WHITE")
    stream.println("% lower left corner")
    emitTranslate(x, y)

    // compute image size. First of all, if width or height is 0, image is 1:1.
    if (imageHeight == 0 || imageWidth == 0) {
      imageHeight = pc.dimensions!!.height
      imageWidth = pc.dimensions!!.width
    }
    stream.println("% size of image")
    emitScale(imageHeight, imageWidth)
    stream.print(pc.dimensions!!.width)
    stream.print(" ")
    stream.print(pc.dimensions!!.height)
    stream.println(" 1")
    stream.print("[")
    stream.print("0 ")
    stream.print(pc.dimensions!!.width)
    stream.print(" -" + pc.dimensions!!.height)
    stream.print(" 0 0 0")
    stream.println("]")
    stream.println("{currentfile " + ((pc.dimensions!!.width + 7) / 8).toString() + " string readhexstring pop}")
    stream.println(" image")

    // array to hold a line of pixel data
    val sb = CharArray(charsPerRow + 1)
    var b: Byte = 3
    var c: Byte = 0
    var offset = 0
    for (i in 0 until pc.dimensions!!.height) {
      for (j in 0 until pc.dimensions!!.width) {
        var n = pc.getPixelAt(i, j)
        n = if ((n and 0xFF) + (n and 0xFF00 shr 8) + (n and 0xFF0000 shr 16) > 300) 1 else 0
        c = c or (n shl b--.toInt()).toByte()
        if (b.toInt() == -1) {
          sb[offset++] = hd[c.toInt()]
          b = 3
          c = 0
        }
        if (offset >= charsPerRow) {
          val s = String(sb, 0, offset)
          stream.println(s)
          offset = 0
        }
      }
      // end of the line, where are we ?
      if (b.toInt() != 3) {
        sb[offset++] = hd[c.toInt()]
        b = 3
        c = 0
      }
      if (offset % 2 == 1) {
        sb[offset++] = '0'
      }
    }
    // print partial rows
    if (offset != 0) {
      val s = String(sb, 0, offset)
      stream.print(s)
    }

    stream.println("\ngrestore")
    return true
  }

  // ----------------------------------------------------------------------
  // PRIVATE METHODS
  // ----------------------------------------------------------------------
  private fun emitMoveto(x: Int, y: Int) {
    stream.print(x)
    stream.print(" ")
    stream.print(y)
    stream.println(" moveto")
  }

  private fun emitLineto(x: Int, y: Int) {
    stream.print(x)
    stream.print(" ")
    stream.print(y)
    stream.println(" lineto")
  }

  private fun emitTranslate(x: Int, y: Int) {
    stream.print(x)
    stream.print(" ")
    stream.print(y)
    stream.println(" translate")
  }

  private fun emitTranslate(x: Double, y: Double) {
    stream.print(x)
    stream.print(" ")
    stream.print(y)
    stream.println(" translate")
  }

  private fun emitScale(sx: Int, sy: Int) {
    stream.print(sx)
    stream.print(" ")
    stream.print(sy)
    stream.println(" scale")
  }

  private fun emitScale(sx: Double, sy: Double) {
    stream.print(sx)
    stream.print(" ")
    stream.print(sy)
    stream.println(" scale")
  }

  companion object {
    // ----------------------------------------------------------------------
    // DATA MEMBERS
    // ----------------------------------------------------------------------
    private const val PAGEHEIGHT = 1200
    private const val PAGEWIDTH = 1600
    private val hd = "0123456789ABCDEF".toCharArray()
    private const val charsPerRow = 12 * 6
  }
}
