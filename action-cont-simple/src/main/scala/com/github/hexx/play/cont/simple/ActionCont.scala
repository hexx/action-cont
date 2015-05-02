package com.github.hexx.play.cont.simple

import play.api.mvc.Result
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

object ActionCont {
  def apply[A](f: (A => Future[Result]) => Future[Result]): ActionCont[A] =
    Cont(f)

  def fromFuture[A](future: => Future[A])(implicit ec: ExecutionContext): ActionCont[A] =
    Cont(future.flatMap)

  def successful[A](a: A)(implicit ec: ExecutionContext): ActionCont[A] =
    fromFuture(Future.successful(a))

  def failed[A](throwable: Throwable)(implicit ec: ExecutionContext): ActionCont[A] =
    fromFuture(Future.failed(throwable))

  def recover[A](actionCont: ActionCont[A])(pf: PartialFunction[Throwable, Future[Result]])
    (implicit executor: ExecutionContext): ActionCont[A] =
    ActionCont(f => actionCont.run(f).recoverWith(pf))
}
