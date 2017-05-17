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
package com.waz.zclient.controllers

import android.content.Context
import com.waz.model.{Id, Uid, UserData}
import com.waz.service.ZMessaging
import com.waz.threading.Threading
import com.waz.utils.events.{EventContext, Signal}
import com.waz.zclient.{Injectable, Injector}

//TODO: Remove when the SE is ready
case class TeamId(str: String) {
  override def toString = str
}

object TeamId extends (String => TeamId) {

  def apply(): TeamId = Id.random()

  implicit object Id extends Id[TeamId] {
    override def random(): TeamId = TeamId(Uid().toString)
    override def decode(str: String): TeamId = TeamId(str)
  }
}

case class TeamData(id:   TeamId,
                    name: String)
//TODO: ----------------------------

class TeamsAndUserController(implicit injector: Injector, context: Context, ec: EventContext) extends Injectable {
  import Threading.Implicits.Ui
  val zms = inject[Signal[ZMessaging]]
  val teams = Signal(Seq(TeamData(TeamId(), "ωire"))) //TODO: STUB
  val self = for {
    z <- zms
    self <- z.usersStorage.signal(z.selfUserId)
  } yield self

  val currentTeamOrUser = Signal[Either[UserData, TeamData]]()
  self.head.map{s => currentTeamOrUser ! Left(s)} //TODO: initial value
}
