package com.github.hexx.play.cont.simple

case class Cont[R, A](run: (A => R) => R) {
  def map[B](f: A => B): Cont[R, B] =
    Cont(k => run(a => k(f(a))))

  def flatMap[B](f: A => Cont[R, B]): Cont[R, B] =
    Cont(k => run(a => f(a).run(k)))

  def withFilter(f: A => Boolean): Cont[R, A] =
    Cont(k => run(a => if (f(a)) k(a) else throw new NoSuchElementException("Cont must not fail to filter.")))
}
