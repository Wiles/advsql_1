package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

class PlcController extends Controller {
  val loginForm = Form(
    tuple(
      "email" -> nonEmptyText,
      "password" -> text)
  )
  
  
  
  def settings = Action {
    Ok("Hello")
  }
}