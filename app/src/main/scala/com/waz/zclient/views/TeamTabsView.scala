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
import android.support.v7.widget.{LinearLayoutManager, RecyclerView}
import android.util.AttributeSet
import android.view.View.OnClickListener
import android.view.{View, ViewGroup}
import com.waz.ZLog
import com.waz.ZLog._
import com.waz.model._
import com.waz.threading.Threading
import com.waz.utils.events.{EventContext, EventStream, Signal}
import com.waz.zclient.controllers.{TeamData, TeamsAndUserController}
import com.waz.zclient.{Injectable, Injector, ViewHelper}

class TeamTabsView(val context: Context, val attrs: AttributeSet, val defStyleAttr: Int) extends RecyclerView(context, attrs, defStyleAttr) with ViewHelper {
  def this(context: Context, attrs: AttributeSet) = this(context, attrs, 0)
  def this(context: Context) = this(context, null)

  val onTabClick = EventStream[Either[UserData, TeamData]]()

  setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false){
    override def canScrollHorizontally = false
    override def canScrollVertically = false
  })
  setOverScrollMode(View.OVER_SCROLL_NEVER)
  private val adapter = new TeamTabsAdapter(context)
  setAdapter(adapter)
}

class TeamTabViewHolder(view: TeamTabButton) extends RecyclerView.ViewHolder(view){
  def bind(data: Either[UserData, TeamData], selected: Boolean): Unit = {
    view.setTag(data)
    data match {
      case Left(userData) => view.setUserData(userData, selected)
      case Right(teamData) => view.setTeamData(teamData, selected)
    }
  }
}

class TeamTabsAdapter(context: Context)(implicit injector: Injector, eventContext: EventContext) extends RecyclerView.Adapter[TeamTabViewHolder] with Injectable {
  private implicit val tag: LogTag = logTagFor[TeamTabsAdapter]

  val controller = inject[TeamsAndUserController]

  val onItemClick = EventStream[Either[UserData, TeamData]]()

  onItemClick{ controller.currentTeamOrUser ! _ }

  Signal(controller.self, controller.teams, controller.currentTeamOrUser).on(Threading.Ui){ _ =>
    notifyDataSetChanged()
  }

  override def getItemCount = 1 + controller.teams.currentValue.fold(0)(_.size)

  override def onBindViewHolder(holder: TeamTabViewHolder, position: Int) = {
    getItem(position) match {
      case Some(data) =>
        val selected = controller.currentTeamOrUser.currentValue.contains(data)
        holder.bind(data, selected)
      case _ =>
        ZLog.error("Invalid get item index")
    }
  }

  override def onCreateViewHolder(parent: ViewGroup, viewType: Int) = {
    val view = new TeamTabButton(context)
    view.setOnClickListener(new OnClickListener {
      override def onClick(v: View) = {
        onItemClick ! v.getTag.asInstanceOf[Either[UserData, TeamData]]
      }
    })
    parent.addView(view)
    new TeamTabViewHolder(view)
  }

  def getItem(position: Int): Option[Either[UserData, TeamData]] = {
    position match {
      case 0 =>
        controller.self.currentValue.map(Left(_))
      case index =>
        controller.teams.currentValue.flatMap(_.lift(index - 1)).map(Right(_))
    }
  }
}
