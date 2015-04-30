package controllers.csrf

import com.github.hexx.play.cont._
import com.github.hexx.play.cont.play2auth.AuthElementCont
import controllers.stack.PreventingCsrfToken
import controllers.cont.TokenValidateElementCont
import jp.t2v.lab.play2.auth.sample.Role._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Controller
import play.api.mvc.AnyContent
import play.api.mvc.Request
import scala.concurrent.ExecutionContext

trait PreventingCsrfSample extends Controller with AuthElementCont with TokenValidateElementCont with AuthConfigImpl {
  implicit val ec: ExecutionContext = play.api.libs.concurrent.Execution.defaultContext

  def checkCont(authority: Authority, ignoreTokenValidation: Boolean)
    (implicit request: Request[AnyContent], ec: ExecutionContext): ActionCont[(User, PreventingCsrfToken)] =
    for {
      user <- authElementCont(NormalUser)
      token <- tokenValidateElementCont(ignoreTokenValidation)
    } yield (user, token)

  def formWithToken = ActionCont.run(implicit request =>
    for (
      (user, token) <- checkCont(NormalUser, ignoreTokenValidation = true)
    ) yield Ok(views.html.csrf.formWithToken()(user, token))
  )

  def formWithoutToken = ActionCont.run(implicit request =>
    for (
      (user, _) <- checkCont(NormalUser, ignoreTokenValidation = true)
    ) yield Ok(views.html.csrf.formWithoutToken()(user))
  )

  val form = Form { single("message" -> text) }

  def submitTarget = ActionCont.run(implicit request =>
    for {
      _ <- checkCont(NormalUser, ignoreTokenValidation = false)
      message <- FormCont.hasErrors(form, request)(_ => throw new Exception)
    } yield Ok(message).as("text/plain")
  )

}
object PreventingCsrfSample extends PreventingCsrfSample
