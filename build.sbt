val commonSettings = Seq(
  scalaVersion := "2.10.5",
  scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlint", "-language:_"),
  resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
)

lazy val root = (project in file(".")).aggregate(
  actionCont,
  actionContSimple,
  actionContLib,
  play2AuthContSample
)

lazy val actionCont = (project in file("action-cont")).settings(
  commonSettings ++ Seq(
    name := "action-cont",
    organization := "com.github.hexx",
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play"               % play.core.PlayVersion.current % "provided",
      "org.scalaz"        %% "scalaz-core"        % "7.0.7",
      "org.typelevel"     %% "scalaz-contrib-210" % "0.1.5",
      "com.typesafe.play" %% "play-test"          % play.core.PlayVersion.current % "test",
      "org.scalatest"     %% "scalatest"          % "2.2.4"                       % "test"
    )
  ):_*
)

lazy val actionContSimple = (project in file("action-cont-simple")).settings(
  commonSettings ++ Seq(
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play" % play.core.PlayVersion.current % "provided"
    )
  ):_*
)

lazy val actionContLib = (project in file("action-cont-lib")).settings(
  commonSettings ++ Seq(
    libraryDependencies ++= Seq(
      "jp.t2v" %% "play2-auth" % "0.13.2"
    )
  ):_*
).dependsOn(actionCont)

lazy val play2AuthContSample = (project in file("play2-auth-cont-sample")).settings(
  commonSettings ++ Seq(
    libraryDependencies ++= Seq(
      jdbc,
      "org.mindrot"           % "jbcrypt"                           % "0.3m",
      "org.scalikejdbc"      %% "scalikejdbc"                       % "2.2.6",
      "org.scalikejdbc"      %% "scalikejdbc-config"                % "2.2.6",
      "org.scalikejdbc"      %% "scalikejdbc-syntax-support-macro"  % "2.2.6",
      "org.scalikejdbc"      %% "scalikejdbc-test"                  % "2.2.6"   % "test",
      "org.scalikejdbc"      %% "scalikejdbc-play-plugin"           % "2.3.6",
      "com.github.tototoshi" %% "play-flyway"                       % "1.2.1",
      "jp.t2v"               %% "play2-auth-test" % "0.13.2"        % "test"
    ),
    TwirlKeys.templateImports += "jp.t2v.lab.play2.auth.sample._"
  )
).enablePlugins(play.PlayScala).dependsOn(actionContLib)
