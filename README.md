# ActionCont

継続モナドを使ってPlay FrameworkのActionを組み立てるためのライブラリです。

これは説明のために書かれたライブラリであり、今後メンテナンスしていく予定はありません。

## ライセンス

public domain

## 概要

個人的な考えですが、継続モナドはWebアプリケーションのコントローラーを書くのに非常に適したものだと考えています。

おおまかなイメージを話しますと、継続モナドはコールバック関数を受け取り、その前後に処理を挟むことができます。
この動作をWebアプリケーションのコントローラーで考えてみますと、リクエストを受け取りレスポンスを返す関数を受け取り、その前後に処理を挟むことができるということになります。
これはコントローラを構成する部品を作る上で便利な性質になります。
たとえばJava EEのServlet Filterはまさにそういう動作をする仕組みです。
それに加えて、継続モナドはモナドなのでScalaのfor構文を使い、自由に組み立てることができます。
そう言われると、なんとなく継続モナドが便利な予感がしてきたでしょうか。

今回比較のために、がくぞ(@gakuzzzz)さんの [t2v/play2-auth](https://github.com/t2v/play2-auth) のサンプルのActionの合成部分を継続モナドを使って再実装させていただきました。
play2-authのサンプルには、がくぞさん自身が作られた [t2v/stackable-controller](https://github.com/t2v/stackable-controller) を使ったActionの合成と、Play標準のActionBuilderを使ったActionの合成のサンプルが書かれています。
今回の継続モナドを使った手法と見比べていただければと思います。

以下、簡単にプロジェクトの説明をさせていただきます。
また後日、詳しいブログ記事などを書く予定ですので、軽く雰囲気だけを掴んでください。

## action-cont

継続モナドの中心部分が入っているプロジェクトです。
今回 `ContT[Future, Result, A]` を `ActionCont[A]` と名付けました。

```scala
type ActionCont[A] = ContT[Future, Result, A]
```

Scalazの `ContT` が使われていますが、今回のやり方ではもっと簡単な継続モナドでもかまいません。

```scala
case class Cont[R, A](run: (A => R) => R) {
  def map[B](f: A => B): Cont[R, B] = Cont(k => run(a => k(f(a))))
  def flatMap[B](f: A => Cont[R, B]): Cont[R, B] = Cont(k => run(a => f(a).run(k)))
}
  
type ActionCont[A] = Cont[Future[Result], A]
```

Scalazではなく、以上のような簡単なコードでも同じように動作します。

## action-cont-lib

既存のライブラリを使って `ActionCont` の形の部品を作ったライブラリにする予定なのですが、今のところplay2-auth関連のものしかありません。

- `AsyncAuthCont` はplay2-authの `AsyncAuth` に対応しています。
- `AuthElementCont` はplay2-authの `AuthElement` に対応しています。

どちらも後ろにContを付けただけです。

## play2-auth-cont-sample

play2-authのsampleに対応しています。と言っても全部が再実装されているわけではなく、以下のものだけが継続モナドを使った実装になっています。

- [play2-auth-cont-sample/app/controllers/cont/PjaxCont.scala](https://github.com/hexx/action-cont/blob/master/play2-auth-cont-sample/app/controllers/cont/PjaxCont.scala)
- [play2-auth-cont-sample/app/controllers/cont/TokenValidateElementCont.scala](https://github.com/hexx/action-cont/blob/master/play2-auth-cont-sample/app/controllers/cont/TokenValidateElementCont.scala)
- [play2-auth-cont-sample/app/controllers/cont/MessageCont.scala](https://github.com/hexx/action-cont/blob/master/play2-auth-cont-sample/app/controllers/cont/MessageCont.scala)
- [play2-auth-cont-sample/app/controllers/standard/Messages.scala](https://github.com/hexx/action-cont/blob/master/play2-auth-cont-sample/app/controllers/standard/Messages.scala)
- [play2-auth-cont-sample/app/controllers/csrf/PreventingCsrfSample.scala](https://github.com/hexx/action-cont/blob/master/play2-auth-cont-sample/app/controllers/csrf/PreventingCsrfSample.scala)

この他の部分はStackable ControllerやActionBuilderで作られているので見比べてみてください。
