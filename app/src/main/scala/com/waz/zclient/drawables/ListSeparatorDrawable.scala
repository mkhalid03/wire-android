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

class ListSeparatorDrawable(color: Int) extends Drawable {

  val paint = new Paint()
  paint.setColor(color)
  var clipValue = 0f

  override def draw(canvas: Canvas) = {
    canvas.drawRect(rightRect(), paint)
    canvas.drawRect(leftRect(), paint)
  }

  override def setColorFilter(colorFilter: ColorFilter) = paint.setColorFilter(colorFilter)

  override def setAlpha(alpha: Int) = paint.setAlpha(alpha)

  override def getOpacity = paint.getAlpha

  def setClip(value: Float): Unit = {
    clipValue = value
    invalidateSelf()
  }

  private def rightRect(): RectF = {
    val dx = getBounds.width() * clipValue * 0.5f
    new RectF(getBounds.centerX() + dx, getBounds.top, getBounds.right, getBounds.bottom)
  }

  private def leftRect(): RectF = {
    val dx = getBounds.width() * clipValue * 0.5f
    new RectF(getBounds.left, getBounds.top, getBounds.centerX() - dx, getBounds.bottom)
  }
}
