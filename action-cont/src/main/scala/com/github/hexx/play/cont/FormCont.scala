package com.github.hexx.play.cont

import play.api.data.Form
import play.api.mvc.Result
import play.api.mvc.Request
import scala.concurrent.Future
import scalaz.ContT

case class FormErrorException[A](
  message: String = null,
  cause: Throwable = null,
  form: Form[A]
) extends Exception(message, cause)

object FormCont {
  def apply[A](form: Form[A], request: Request[_]): ActionCont[A] =
    ContT(form.bindFromRequest()(request).fold(form => Future.failed(FormErrorException(form = form)), _))

  def hasErrors[A](form: Form[A], request: Request[_])(hasErrors: Form[A] => Future[Result]): ActionCont[A] =
    ContT(form.bindFromRequest()(request).fold(hasErrors, _))
}
