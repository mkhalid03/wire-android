/**
 * Wire
 * Copyright (C) 2017 Wire Swiss GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.waz.zclient.drawables

import android.graphics._
import android.graphics.drawable.Drawable
import TeamIconDrawable._

object TeamIconDrawable {
  val TeamCorners = 6
  val UserCorners = 0
}

class TeamIconDrawable extends Drawable {
  var text = ""
  var corners = UserCorners

  val borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG)
  borderPaint.setColor(Color.RED)
  borderPaint.setStyle(Paint.Style.STROKE)
  borderPaint.setStrokeJoin(Paint.Join.ROUND)
  borderPaint.setStrokeCap(Paint.Cap.ROUND)
  borderPaint.setDither(true)
  borderPaint.setPathEffect(new CornerPathEffect(8f))

  val innerPaint = new Paint(Paint.ANTI_ALIAS_FLAG)
  innerPaint.setColor(Color.WHITE)
  innerPaint.setStyle(Paint.Style.FILL)
  innerPaint.setStrokeJoin(Paint.Join.ROUND)
  innerPaint.setStrokeCap(Paint.Cap.ROUND)
  innerPaint.setDither(true)
  innerPaint.setPathEffect(new CornerPathEffect(8f))

  val textPaint = new Paint(Paint.ANTI_ALIAS_FLAG)
  textPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR))
  textPaint.setTextAlign(Paint.Align.CENTER)
  textPaint.setColor(Color.TRANSPARENT)
  textPaint.setAntiAlias(true)

  val innerPath = new Path()
  val borderPath = new Path()

  override def draw(canvas: Canvas) = {
    canvas.drawPath(innerPath, innerPaint)
    canvas.drawPath(borderPath, borderPaint)
    val textY = getBounds.centerY - ((textPaint.descent + textPaint.ascent) / 2f)
    val textX = getBounds.centerX
    canvas.drawText(text, textX, textY, textPaint)
  }

  override def setColorFilter(colorFilter: ColorFilter) = {
    borderPaint.setColorFilter(colorFilter)
    innerPaint.setColorFilter(colorFilter)
  }

  override def setAlpha(alpha: Int) = {
    borderPaint.setAlpha(alpha)
    innerPaint.setAlpha(alpha)
  }

  override def getOpacity = {
    borderPaint.getAlpha
    borderPaint.getAlpha
  }

  private def drawPolygon(path: Path, radius: Float, corners: Int): Unit = {
    path.reset()
    if (corners == 0) {
      path.addCircle(0, 0, radius, Path.Direction.CW)
      return
    }
    val angle = 2 * Math.PI / corners
    val phase = angle / 2
    (0 until corners).foreach{ i =>
      val x = radius * Math.cos(angle * i + phase)
      val y = radius * Math.sin(angle * i + phase)
      if (i == 0) {
        path.moveTo(x.toFloat, y.toFloat)
      } else {
        path.lineTo(x.toFloat, y.toFloat)
      }
    }
    path.close()
  }

  override def onBoundsChange(bounds: Rect) = {
    val diam = diameter(bounds) - diameter(bounds) * 0.075f
    val textSize = diam / 2.5f
    val borderWidth = diam * 0.075f
    val borderOffset = borderWidth * 3

    drawPolygon(innerPath, (diam - borderOffset) / 2, corners)
    drawPolygon(borderPath, diam / 2, corners)

    textPaint.setTextSize(textSize)
    borderPaint.setStrokeWidth(borderWidth)

    val hexMatrix = new Matrix()
    hexMatrix.setTranslate(bounds.centerX(), bounds.centerY())
    borderPath.transform(hexMatrix)
    innerPath.transform(hexMatrix)
  }

  override def getIntrinsicHeight = super.getIntrinsicHeight

  override def getIntrinsicWidth = super.getIntrinsicWidth

  private def diameter(bounds: Rect = getBounds): Int = Math.min(bounds.width, bounds.height)

  def setInfo(text: String, borderColor:Int, corners: Int): Unit = {
    this.text = text
    borderPaint.setColor(borderColor)
    this.corners = corners
    invalidateSelf()
  }

  def setText(text:String): Unit = {
    this.text = text
    invalidateSelf()
  }

  def setBorderColor(color: Int): Unit ={
    borderPaint.setColor(color)
    invalidateSelf()
  }

  def setCorners(corners: Int): Unit = {
    this.corners = corners
    invalidateSelf()
  }
}
