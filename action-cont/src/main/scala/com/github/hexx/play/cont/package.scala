package com.github.hexx.play

import play.api.mvc.Result
import scala.concurrent.Future
import scalaz.ContT

package object cont {
  type ActionCont[A] = ContT[Future, Result, A]

  implicit class ActionContWithFilter[A](val actionCont: ActionCont[A]) extends AnyVal {
    def withFilter(f: A => Boolean): ActionCont[A] =
      ActionCont(k =>
        actionCont.run(a =>
          if (f(a)) {
            k(a)
          } else {
            throw new NoSuchElementException("ActionCont must not fail to filter.")
          }
        )
      )
  }
}
