package com.github.hexx.play.cont.simple

import play.api.mvc.AnyContent
import play.api.mvc.Request
import play.api.mvc.Result
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

object FlowCont {
  def apply[WholeRequestContext, NormalRequestContext](
    request: Request[AnyContent],
    wholeCont: Request[AnyContent] => ActionCont[WholeRequestContext],
    normalCont: WholeRequestContext => ActionCont[NormalRequestContext],
    handlerCont: NormalRequestContext => ActionCont[Result],
    errorCont: WholeRequestContext => Throwable => ActionCont[Result])
    (implicit executionContext: ExecutionContext): ActionCont[Result] = {

    for {
      // 正常系と異常系共通で適用される処理
      wholeRequestContext <- wholeCont(request)
      wholeResult <- ActionCont.recover(
        for {
          // 正常系だけで適用される処理
          normalRequestContext <- normalCont(wholeRequestContext)
          // コントローラーの処理本体
          result <- handlerCont(normalRequestContext)
        } yield result) {
          // 異常系の処理
          case e => errorCont(wholeRequestContext)(e).run(Future.successful)
        }
    } yield wholeResult
  }
}
