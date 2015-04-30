package com.github.hexx.play.cont.play2auth

import com.github.hexx.play.cont.ActionCont
import jp.t2v.lab.play2.auth.AuthConfig
import play.api.mvc.RequestHeader

import scala.concurrent.ExecutionContext

trait AsyncAuthCont extends AuthConfig {
  def authorizedCont(authority: Authority)(implicit request: RequestHeader, ec: ExecutionContext): ActionCont[User] =
    ActionCont(f =>
      restoreUserCont.run {
        case None => authenticationFailed(request)
        case Some(user) =>
          authorize(user, authority).flatMap {
            case true => f(user)
            case _ => authorizationFailed(request, user, Some(authority))
          }
      }
    )

  private[play2auth] def restoreUserCont(implicit request: RequestHeader, ec: ExecutionContext): ActionCont[Option[User]] =
    ActionCont(f =>
      (for {
        token <- tokenAccessor.extract(request)
      } yield for {
        Some(userId) <- idContainer.get(token)
        Some(user)   <- resolveUser(userId)
        _            <- idContainer.prolongTimeout(token, sessionTimeoutInSeconds)
        result       <- f(Option(user))
      } yield tokenAccessor.put(token)(result)
      ) getOrElse f(Option.empty)
    )
}
