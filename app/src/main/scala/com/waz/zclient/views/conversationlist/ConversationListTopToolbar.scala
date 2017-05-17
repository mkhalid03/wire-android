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

import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.OnLayoutChangeListener
import android.widget.FrameLayout
import com.waz.ZLog
import com.waz.threading.Threading
import com.waz.zclient.controllers.TeamsAndUserController
import com.waz.zclient.drawables.ListSeparatorDrawable
import com.waz.zclient.ui.text.{GlyphTextView, TypefaceTextView}
import com.waz.zclient.ui.views.CircleView
import com.waz.zclient.utils.ContextUtils._
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

  val controller = inject[TeamsAndUserController]

  val bottomBorder = ViewUtils.getView(this, R.id.conversation_list__border).asInstanceOf[View]
  val glyphButton = ViewUtils.getView(this, R.id.conversation_list_settings).asInstanceOf[GlyphTextView]
  val title = ViewUtils.getView(this, R.id.conversation_list_title).asInstanceOf[TypefaceTextView]
  val settingsIndicator = ViewUtils.getView(this, R.id.conversation_list_settings_indicator).asInstanceOf[CircleView]
  val tabsContainer = ViewUtils.getView(this, R.id.team_tabs_container).asInstanceOf[TeamTabsView]

  val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
  var scrolledToTop = true

  val separatorDrawable = new ListSeparatorDrawable(getColor(R.color.white_24))
  bottomBorder.setBackground(separatorDrawable)
  valueAnimator.end()

  controller.teams.on(Threading.Ui) { teams =>
    tabsContainer.setVisible(teams.nonEmpty)
    title.setVisible(teams.isEmpty)
  }

  tabsContainer.addOnLayoutChangeListener(new OnLayoutChangeListener {
    override def onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int): Unit = {
      val maxValue = v.getWidth.toFloat / getWidth.toFloat
      valueAnimator.setFloatValues(0f, maxValue)
      if (scrolledToTop) {
        separatorDrawable.setClip(maxValue)
      } else {
        separatorDrawable.setClip(0f)
      }
    }
  })

  valueAnimator.addUpdateListener(new AnimatorUpdateListener {
    override def onAnimationUpdate(animation: ValueAnimator) = {
      val value = animation.getAnimatedValue.asInstanceOf[Float]
      separatorDrawable.setClip(value)
    }
  })

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
    if (this.scrolledToTop == scrolledToTop) {
      return
    }
    this.scrolledToTop = scrolledToTop
    if (!scrolledToTop) {
      (0 until tabsContainer.getChildCount).map(tabsContainer.getChildAt).foreach{ child =>
        child.asInstanceOf[TeamTabButton].animateCollapse()
      }
      animateBorderCollapse()
    } else {
      (0 until tabsContainer.getChildCount).map(tabsContainer.getChildAt).foreach{ child =>
        child.asInstanceOf[TeamTabButton].animateExpand()
      }
      animateBorderExpand()
    }
  }

  def animateBorderExpand(): Unit = {
    valueAnimator.start()
  }

  def animateBorderCollapse(): Unit = {
    valueAnimator.reverse()
  }
}
