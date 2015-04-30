package controllers.standard

import com.github.hexx.play.cont.ActionCont
import controllers.cont.MessageCont
import jp.t2v.lab.play2.auth.sample.Role._
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.twirl.api.Html
import scala.concurrent.ExecutionContext
import views.html

trait Messages extends Controller with MessageCont with AuthConfigImpl {
  implicit val ec: ExecutionContext = play.api.libs.concurrent.Execution.defaultContext

  def action(authority: Authority, templateToHtml: Template => Html)
    (implicit ec: ExecutionContext): Action[AnyContent] =
    ActionCont.run(request => messageCont(authority, html.standard.fullTemplate.apply, templateToHtml)(request, ec))

  def main = action(NormalUser, html.message.main("message main")(_))

  def list = action(NormalUser, html.message.list("all messages")(_))

  def detail(id: Int) = action(NormalUser, html.message.detail("messages detail " + id)(_))

  def write = action(Administrator, html.message.write("write message")(_))
}

object Messages extends Messages
