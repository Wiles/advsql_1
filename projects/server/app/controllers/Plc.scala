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
import java.sql.Timestamp
import org.sh.plc.server.repo.ListTimelyReport
import java.util.LinkedHashMap

object Plc extends Controller {

  /**
   * Index page
   */
  def index = Action {
    val plcs = PlcServices.listPlcs()
    val plcStatuses = PlcServices.listPlcStatuses()
    val dateFormat = new SimpleDateFormat()

    Ok(html.Plc.index(plcs, plcStatuses, dateFormat))
  }

  /**
   * Total Consumption form
   */
  val consumptionForm = Form[(String, String)](
    of(
      "start" -> nonEmptyText,
      "end" -> nonEmptyText
    )
  )

  /**
   * Total Consumption report
   */
  def totalConsumption(start: String, end: String, report: Int) = Action {

    def process(): Tuple2[List[LinkedHashMap[String, Any]], String]  = {
	    try {
	      val formatter = TotalConsumptionModel.Default.dateFormatter
	      val startDate = formatter.parse(start)
	      val endDate = formatter.parse(end)
	
	      val types = Array(
	          ListTimelyReport.DAILY,
	          ListTimelyReport.HOURLY,
	          ListTimelyReport.MONTHLY,
	          ListTimelyReport.TOTAL
	      )
	      
	      var reportType = ListTimelyReport.TOTAL
	      val validReport = report >= 0 && report < types.length
	      if (validReport) {
	        reportType = types(report)
	      }
	      
	      val items = PlcServices.listTimelyConsumption(reportType,
	          new Timestamp(startDate.getTime()), new Timestamp(endDate.getTime()))
	      
	      (items, "")
	    } catch {
	      case e: Exception =>
	        e.printStackTrace()
	        (List[LinkedHashMap[String, Any]](), "Please select a start and end date")
	    }
    }

    val tuple = process()
    val items = tuple._1
    val errors = tuple._2

    Ok(html.Plc.totalConsumption(start, end, report, items, errors))
  }

  /**
   * Settings form
   */
  val settingsForm: Form[SettingModel] = Form(
    // Defines a mapping that will handle Contact values
    mapping(
      "email" -> nonEmptyText,
      "hourlyEnergyThreshold" -> number
    )(SettingModel.apply)(SettingModel.unapply)
  )

  /**
   * Settings page
   */
  def settings = Action {

    import org.sh.plc.server.model.SettingModel._

    val email = PlcServices.getSetting(SettingModel.Key.email, "<example>@conestogac.on.ca")

    // Get the hourly threshold or use a default value based on failure
    val hourlyThreshold = catching(classOf[NumberFormatException]).opt(
      PlcServices.getSetting(
        Key.hourlyEnergyThreshold,
        Default.hourlyEnergyThreshold.toString
      ).toInt
    ).getOrElse(Default.hourlyEnergyThreshold)

    val settingModel = SettingModel(email, hourlyThreshold)

    Ok(html.Plc.settings(settingsForm.fill(settingModel)))
  }

  /**
   * Settings submission processing
   */
  def submitSettings = Action { implicit request =>
    settingsForm.bindFromRequest.fold(
      errors => {
        BadRequest(html.Plc.settings(errors))
      },
      settingsModel => {
        val email = settingsModel.email
        val hourlyThreshold = settingsModel.hourlyEnergyThreshold

        PlcServices.putSetting(SettingModel.Key.email, email)
        PlcServices.putSetting(SettingModel.Key.hourlyEnergyThreshold, hourlyThreshold.toString)

        Ok(html.Plc.settings(settingsForm.fill(settingsModel)))
      }
    )
  }
}