package controllers

import java.text._

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import views._

import org.sh.plc.server.model._
import org.sh.plc.server.services._
import scala.util.control.Exception._

object SettingsController extends Controller with PlcServiceComponent {
  /**
   * Settings form
   * 
   * TODO: Make this a POST message, because it could break the program on very long email addresses
   * depending on some browsers.
   */
  val settingsForm: Form[SettingModel] = Form(
    // Defines a mapping that will handle Contact values
    mapping(
      "email" -> nonEmptyText,
      "hourlyEnergyThreshold" -> number)(SettingModel.apply)(SettingModel.unapply))

  /**
   * Settings page
   */
  def settings = Action {

    import org.sh.plc.server.model.SettingModel._

    val email = plcService.getSetting(SettingModel.Key.email, "<example>@conestogac.on.ca")

    // Get the hourly threshold or use a default value based on failure
    val hourlyThreshold = catching(classOf[NumberFormatException]).opt(
      plcService.getSetting(
        Key.hourlyEnergyThreshold,
        Default.hourlyEnergyThreshold.toString).toInt).getOrElse(Default.hourlyEnergyThreshold)

    val settingModel = SettingModel(email, hourlyThreshold)

    Ok(html.Settings.settings(settingsForm.fill(settingModel)))
  }

  /**
   * Settings submission processing
   */
  def submitSettings = Action { implicit request =>
    settingsForm.bindFromRequest.fold(
      errors => {
        BadRequest(html.Settings.settings(errors))
      },
      settingsModel => {
        val email = settingsModel.email
        val hourlyThreshold = settingsModel.hourlyEnergyThreshold

        plcService.putSetting(SettingModel.Key.email, email)
        plcService.putSetting(SettingModel.Key.hourlyEnergyThreshold, hourlyThreshold.toString)

        Ok(html.Settings.settings(settingsForm.fill(settingsModel)))
      })
  }
  
  /**
   * Delete every Plc event
   */
  def deletePlcEvents = Action {
    plcService.deletePlcEvents()
    Redirect(routes.SettingsController.settings())
  }
}