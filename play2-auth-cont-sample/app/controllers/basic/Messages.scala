package controllers.basic

import controllers.stack.Pjax
import jp.t2v.lab.play2.auth.AuthElement
import play.api.mvc.Controller
import views.html
import jp.t2v.lab.play2.auth.sample.Role._
import play.twirl.api.Html

trait Messages extends Controller with AuthElement with AuthConfigImpl {

  def main = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    val title = "message main"
    Ok(html.message.main(title))
  }

  def list = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    val title = "all messages"
    Ok(html.message.list(title))
  }

  def detail(id: Int) = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    val title = "messages detail "
    Ok(html.message.detail(title + id))
  }

  def write = StackAction(AuthorityKey -> Administrator) { implicit request =>
    val title = "write message"
    Ok(html.message.write(title))
  }

  protected implicit def template(implicit user: User): String => Html => Html = html.basic.fullTemplate(user)

}
object Messages extends Messages
