package com.github.hexx.play.cont.simple

import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Request
import play.api.mvc.Result
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

// 説明用のログイン処理
// 説明用なので実装されていないところが多いです
// より実践的な例は play2-auth-cont-sample を参考にしてください
object LoginController extends Controller {
  case class AuthParam(name: String, password: String)

  val authParamForm = Form(
    mapping(
      "name" -> text,
      "password" -> text
    )(AuthParam.apply)(_ => None)
  )

  case class User(id: Int, name: String)

  def authParamCont(request: Request[AnyContent]): ActionCont[AuthParam] =
    ActionCont((f: AuthParam => Future[Result]) =>
      authParamForm.bindFromRequest()(request).fold(
        error => Future.successful(BadRequest),
        authParam => f(authParam)
      )
    )

  def corsCont: Request[AnyContent] => ActionCont[Unit] = ???

  def loginCont: AuthParam => ActionCont[User] = ???

  def combinedCont(request: Request[AnyContent]): ActionCont[User] =
    for {
      _ <- corsCont(request)
      authParam <- authParamCont(request)
      user <- loginCont(authParam)
    } yield user

  def login = Action.async { request =>

    val cont: ActionCont[Result] = for {
      user <- combinedCont(request)
    } yield Ok(Json.obj("id" -> user.id))

    cont.run(Future.successful)
  }

  class FormErrorException extends Throwable

  class UserNotFoundException extends Throwable

  def loginFlow = Action.async { request =>
    implicit val ec: ExecutionContext = play.api.libs.concurrent.Execution.defaultContext
    FlowCont(
      request     = request,
      wholeCont   = corsCont,
      normalCont  = (_: Unit) => authParamCont(request),
      handlerCont = loginCont(_: AuthParam).map(user => Ok(Json.obj("id" -> user.id))),
      errorCont   = (_: Unit) => ((_: Throwable) match {
        case e: FormErrorException     => BadRequest
        case e: UserNotFoundException  => NotFound
        case _                         => InternalServerError
      }).andThen(ActionCont.successful)
    ).run(Future.successful)
  }
}
