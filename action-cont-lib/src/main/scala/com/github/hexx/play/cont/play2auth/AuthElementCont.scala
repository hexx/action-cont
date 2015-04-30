package com.github.hexx.play.cont.play2auth

import com.github.hexx.play.cont.ActionCont
import play.api.mvc.RequestHeader

import scala.concurrent.ExecutionContext

trait AuthElementCont extends AsyncAuthCont {
  def authElementCont(authority: Authority)(implicit request: RequestHeader, ec: ExecutionContext): ActionCont[User] =
    authorizedCont(authority)

  def authElementCont(implicit request: RequestHeader, ec: ExecutionContext): ActionCont[User] =
    ActionCont(_ =>
      restoreUserCont.run {
        case Some(user) => authorizationFailed(request, user, None)
        case None => authenticationFailed(request)
      }
    )
}
