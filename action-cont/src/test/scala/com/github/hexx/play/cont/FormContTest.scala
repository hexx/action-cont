package com.github.hexx.play.cont

import org.scalatest.FunSpec
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.Request
import play.api.mvc.Result
import play.api.mvc.Results._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import scala.concurrent.Await
import scala.concurrent.duration._

class FormContSpec extends FunSpec {
  case class AuthenticationParameter(
    name: String,
    password: String
  )

  val authenticationParameterForm: Form[AuthenticationParameter] = Form(
    mapping(
      "name" -> text,
      "password" -> text
    )(AuthenticationParameter.apply)(_ => None)
  )

  def cont(request: Request[AnyContent]): ActionCont[Result] =
    for {
      a <- FormCont(authenticationParameterForm, request)
    } yield Ok(s"name: ${a.name}, password: ${a.password}")

  def contBadRequestIfError(request: Request[AnyContent]): ActionCont[Result] =
    for {
      a <- FormCont.hasErrors(authenticationParameterForm, request)(_ => Future.successful(BadRequest))
    } yield Ok(s"name: ${a.name}, password: ${a.password}")

  describe("FormCont") {
    it("result in Ok") {
      val action = ActionCont.run(cont)
      val request = FakeRequest("POST", "/").withFormUrlEncodedBody("name" -> "hexx", "password" -> "hogeika")
      val result = call(action, request)

      assert(status(result) === OK)
      assert(contentAsString(result) === "name: hexx, password: hogeika")
    }

    it("produce FormErrorException by an insufficient request") {
      val action = ActionCont.run(cont)
      val request = FakeRequest("POST", "/").withFormUrlEncodedBody("name" -> "hexx")
      val result = call(action, request)

      intercept[FormErrorException[AuthenticationParameter]] {
        status(result)
      }
    }

    it("result in BadRequest by an insufficient request") {
      val action = ActionCont.run(contBadRequestIfError)
      val request = FakeRequest("POST", "/").withFormUrlEncodedBody("name" -> "hexx")
      val result = call(action, request)

      assert(status(result) === BAD_REQUEST)
    }
  }
}
