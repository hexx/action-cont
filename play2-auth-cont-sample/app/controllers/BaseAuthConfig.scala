package controllers

import jp.t2v.lab.play2.auth.AuthConfig
import jp.t2v.lab.play2.auth.sample.{Role, Account}
import jp.t2v.lab.play2.auth.sample.Role._
import play.api.mvc.RequestHeader
import play.api.mvc.Results._

import scala.concurrent.{Future, ExecutionContext}
import scala.reflect._
import play.Logger

trait BaseAuthConfig  extends AuthConfig {

  type Id = Int
  type User = Account
  type Authority = Role

  val idTag: ClassTag[Id] = classTag[Id]
  val sessionTimeoutInSeconds = 3600

  def resolveUser(id: Id)(implicit ctx: ExecutionContext) = Future.successful(Account.findById(id))
  def authorizationFailed(request: RequestHeader)(implicit ctx: ExecutionContext) = throw new AssertionError("don't use")
  override def authorizationFailed(request: RequestHeader, user: User, authority: Option[Authority])(implicit ctx: ExecutionContext) = {
    Logger.info(s"authorizationFailed. userId: ${user.id}, userName: ${user.name}, authority: $authority")
    Future.successful(Forbidden("no permission"))
  }
  def authorize(user: User, authority: Authority)(implicit ctx: ExecutionContext) = Future.successful((user.role, authority) match {
    case (Administrator, _) => true
    case (NormalUser, NormalUser) => true
    case _ => false
  })

}
