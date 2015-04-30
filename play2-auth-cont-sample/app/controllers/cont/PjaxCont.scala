package controllers.cont

import com.github.hexx.play.cont.ActionCont
import play.api.mvc.RequestHeader
import play.twirl.api.Html
import views.html
import scala.concurrent.ExecutionContext

object PjaxCont {
  type Template = String => Html => Html

  def apply(fullTemplate: Template)(implicit request: RequestHeader, ec: ExecutionContext): ActionCont[Template] =
    ActionCont.successful(if (request.headers.keys("X-Pjax")) html.pjaxTemplate.apply _ else fullTemplate)
}
