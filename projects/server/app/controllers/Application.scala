package controllers

import play.api._
import play.api.mvc._
import views.Application._

object Application extends Controller {
  
  def index = Action {
    Ok(html.index())
  }

}