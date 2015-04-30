package controllers.cont

import com.github.hexx.play.cont.ActionCont
import com.github.hexx.play.cont.play2auth.AuthElementCont
import jp.t2v.lab.play2.auth.AuthConfig
import play.api.mvc.Request
import play.api.mvc.Result
import play.api.mvc.Results.Ok
import play.twirl.api.Html
import scala.concurrent.ExecutionContext

trait MessageCont extends AuthElementCont with AuthConfig {
  type Template = controllers.cont.PjaxCont.Template

  def messageCont[A](authority: Authority, fullTemplate: User => Template, templateToHtml: Template => Html)
      (implicit request: Request[A], ec: ExecutionContext): ActionCont[Result] =
    for {
      user <- authElementCont(authority)
      template <- PjaxCont(fullTemplate(user))
    } yield Ok(templateToHtml(template))
}
