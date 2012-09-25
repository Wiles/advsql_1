package controllers

import java.text._

import play.api._
import play.api.mvc._

import views._

import org.sh.plc.server.communicator._
import org.sh.plc.server.services._

object Application extends Controller with PlcServiceComponent {
  /**
   * Index page
   */
  def index = Action {
    val plcs = plcService.listPlcs()
    val plcStatuses = plcService.listPlcStatuses()
    val dateFormat = new SimpleDateFormat()

    Ok(html.Plc.index(plcs, plcStatuses, dateFormat))
  }
}