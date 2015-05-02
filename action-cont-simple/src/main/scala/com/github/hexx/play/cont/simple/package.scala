package com.github.hexx.play.cont

import play.api.mvc.Result
import scala.concurrent.Future

package object simple {
  type ActionCont[A] = Cont[Future[Result], A]
}
