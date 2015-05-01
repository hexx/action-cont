package com.github.hexx.play.cont

import play.api.mvc.AnyContent
import play.api.mvc.Request
import play.api.mvc.Result
import scala.concurrent.ExecutionContext
import scalaz.contrib.std.scalaFuture._

object FlowCont {
  def apply[WholeRequestContext, NormalRequestContext](
    request: Request[AnyContent],
    wholeCont: Request[AnyContent] => ActionCont[WholeRequestContext],
    normalCont: WholeRequestContext => ActionCont[NormalRequestContext],
    handlerCont: NormalRequestContext => ActionCont[Result],
    exceptionalCont: PartialFunction[Throwable, WholeRequestContext => ActionCont[Result]])
    (implicit executionContext: ExecutionContext): ActionCont[Result] = {

    for {
      wholeRequestContext <- wholeCont(request)
      wholeResult <- ActionCont.recover(
        for {
          normalRequestContext <- normalCont(wholeRequestContext)
          result <- handlerCont(normalRequestContext)
        } yield result) {
          case e => exceptionalCont(e)(wholeRequestContext).run_
        }
    } yield wholeResult
  }
}
