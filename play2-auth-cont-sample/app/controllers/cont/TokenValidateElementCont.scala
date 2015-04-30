package controllers.cont

import com.github.hexx.play.cont.ActionCont
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import play.api.mvc.Request
import play.api.data._
import play.api.data.Forms._
import play.api.mvc.Results.BadRequest
import scala.util.Random
import java.security.SecureRandom
import controllers.stack.PreventingCsrfToken

trait TokenValidateElementCont {
  private[this] val PreventingCsrfTokenSessionKey = "preventingCsrfToken"

  private[this] val tokenForm = Form(PreventingCsrfToken.FormKey -> text)

  private[this] val random = new Random(new SecureRandom)

  private[this] val table = ('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9') ++ "^`~:/?,.{[}}|+_()*^%$#@!"

  private[this] def generateToken: PreventingCsrfToken = PreventingCsrfToken {
    Iterator.continually(random.nextInt(table.size)).map(table).take(32).mkString
  }

  private[this] def validateToken(request: Request[_]): Boolean = (for {
    tokenInForm    <- tokenForm.bindFromRequest()(request).value
    tokenInSession <- request.session.get(PreventingCsrfTokenSessionKey)
  } yield tokenInForm == tokenInSession) getOrElse false

  def tokenValidateElementCont[A](ignoreTokenValidation: Boolean)(implicit request: Request[A], ec: ExecutionContext): ActionCont[PreventingCsrfToken] =
    ActionCont(f =>
      if (ignoreTokenValidation || validateToken(request)) {
        val newToken = generateToken
        f(newToken).map(_.withSession(PreventingCsrfTokenSessionKey -> newToken.value))
      } else {
        Future.successful(BadRequest("Invalid preventing CSRF token"))
      }
    )

}
