package com.github.hexx.play.cont

import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.Request
import play.api.mvc.Result
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scalaz._
import scalaz.contrib.std.scalaFuture._

object ActionCont extends IndexedContsTInstances with IndexedContsTFunctions {
  def apply[A](f: (A => Future[Result]) => Future[Result]): ActionCont[A] =
    ContT(f)

  def fromFuture[A](future: => Future[A])(implicit ec: ExecutionContext): ActionCont[A] =
    ActionCont(future.flatMap)

  def successful[A](a: A)(implicit ec: ExecutionContext): ActionCont[A] =
    fromFuture(Future.successful(a))

  def failed[A](throwable: Throwable)(implicit ec: ExecutionContext): ActionCont[A] =
    fromFuture(Future.failed(throwable))

  def run(f: Request[AnyContent] => ActionCont[Result])(implicit ec: ExecutionContext): Action[AnyContent] =
    Action.async(f(_).run_)
}
