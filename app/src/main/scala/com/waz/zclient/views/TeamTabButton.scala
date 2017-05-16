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
package com.waz.zclient.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.{FrameLayout, ImageView}
import com.waz.api.impl.{AccentColor, AccentColors}
import com.waz.model.{TeamData, UserData}
import com.waz.utils.NameParts
import com.waz.zclient.drawables.TeamIconDrawable
import com.waz.zclient.ui.text.TypefaceTextView
import com.waz.zclient.ui.views.CircleView
import com.waz.zclient.utils.ViewUtils
import com.waz.zclient.{R, ViewHelper}

class TeamTabButton(val context: Context, val attrs: AttributeSet, val defStyleAttr: Int) extends FrameLayout(context, attrs, defStyleAttr) with ViewHelper {
  def this(context: Context, attrs: AttributeSet) = this(context, attrs, 0)
  def this(context: Context) = this(context, null)

  inflate(R.layout.view_team_tab)

  val icon = ViewUtils.getView(this, R.id.team_icon).asInstanceOf[ImageView]
  val name = ViewUtils.getView(this, R.id.team_name).asInstanceOf[TypefaceTextView]
  val unreadIndicator = ViewUtils.getView(this, R.id.unread_indicator).asInstanceOf[CircleView]

  val drawable = new TeamIconDrawable
  drawable.setText("")
  drawable.setBorderColor(Color.TRANSPARENT)
  icon.setImageDrawable(drawable)
  setLayerType(View.LAYER_TYPE_SOFTWARE, null)

  def setTeamData(teamData: TeamData, selected: Boolean): Unit = {
    val color = if (selected) AccentColors.defaultColor.getColor() else Color.TRANSPARENT
    drawable.setInfo(NameParts.maybeInitial(teamData.name).getOrElse(""), color, TeamIconDrawable.TeamCorners)
    name.setText(teamData.name)
    unreadIndicator.setAccentColor(AccentColors.defaultColor.getColor())
    setAlpha(if (selected) 1.0f else 0.5f)
  }

  def setUserData(userData: UserData, selected:Boolean): Unit = {
    val color = if (selected) AccentColor(userData.accent).getColor() else Color.TRANSPARENT
    drawable.setInfo(NameParts.maybeInitial(userData.displayName).getOrElse(""), color, TeamIconDrawable.UserCorners)
    name.setText(userData.displayName)
    unreadIndicator.setAccentColor(AccentColor(userData.accent).getColor())
    setAlpha(if (selected) 1.0f else 0.5f)
  }

  def animateExpand(): Unit ={
    name.animate().translationY(0f).start()
    icon.animate().translationY(0f).alpha(1f).start()
    unreadIndicator.animate().translationX(0f).translationY(0).start()
  }

  //TODO: Animation values?
  def animateCollapse(): Unit ={
    name.animate().translationY(-55f)
    icon.animate().translationY(-55f).alpha(0f).start()
    unreadIndicator.animate().translationX(-30f).translationY(5).start()
  }
}
