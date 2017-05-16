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
package com.waz.zclient.views.conversationlist

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.waz.ZLog
import com.waz.threading.Threading
import com.waz.zclient.controllers.TeamAndUsersController
import com.waz.zclient.ui.text.{GlyphTextView, TypefaceTextView}
import com.waz.zclient.ui.views.CircleView
import com.waz.zclient.utils.{RichView, ViewUtils}
import com.waz.zclient.views.{TeamTabButton, TeamTabsView}
import com.waz.zclient.{R, ViewHelper}

object ConversationListTopToolbar {
  trait Callback {
    def settingsClicked(): Unit
  }
}

class ConversationListTopToolbar(val context: Context, val attrs: AttributeSet, val defStyleAttr: Int) extends FrameLayout(context, attrs, defStyleAttr) with ViewHelper {
  def this(context: Context, attrs: AttributeSet) = this(context, attrs, 0)
  def this(context: Context) = this(context, null)

  private implicit val logTag = ZLog.logTagFor[ConversationListTopToolbar]

  inflate(R.layout.view_conv_list_top)

  val controller = inject[TeamAndUsersController]

  val bottomBorder = ViewUtils.getView(this, R.id.conversation_list__border).asInstanceOf[View]
  val glyphButton = ViewUtils.getView(this, R.id.conversation_list_settings).asInstanceOf[GlyphTextView]
  val title = ViewUtils.getView(this, R.id.conversation_list_title).asInstanceOf[TypefaceTextView]
  val settingsIndicator = ViewUtils.getView(this, R.id.conversation_list_settings_indicator).asInstanceOf[CircleView]
  val tabsContainer = ViewUtils.getView(this, R.id.team_tabs_container).asInstanceOf[TeamTabsView]

  controller.teams.on(Threading.Ui) { teams =>
    tabsContainer.setVisible(teams.nonEmpty)
    title.setVisible(teams.isEmpty)
  }

  def setClose(): Unit = {
    glyphButton.setText(R.string.glyph__close)
    settingsIndicator.setVisible(false)
  }

  def setSettings(): Unit = {
    glyphButton.setText(R.string.glyph__settings)
  }

  def setIndicatorVisible(visible: Boolean): Unit = {
    settingsIndicator.setVisible(visible)
  }

  def setIndicatorColor(color: Int): Unit = {
    settingsIndicator.setAccentColor(color)
  }

  def setScrolledToTop(scrolledToTop: Boolean): Unit = {
    bottomBorder.setVisible(!scrolledToTop)
  }

  val threshold = 0
  var prevValue = 0

  def setScrollingValue(value: Int): Unit = {
    if (value > threshold && prevValue == threshold) {
      (0 until tabsContainer.getChildCount).map(tabsContainer.getChildAt).foreach{ child =>
        child.asInstanceOf[TeamTabButton].animateCollapse()
      }
    } else if (value == threshold && prevValue > threshold){
      (0 until tabsContainer.getChildCount).map(tabsContainer.getChildAt).foreach{ child =>
        child.asInstanceOf[TeamTabButton].animateExpand()
      }
    }
    prevValue = value
  }
}
