package controllers

import java.text._
import java.util.LinkedHashMap
import java.sql.Timestamp
import java.io.ByteArrayOutputStream

import play.api._
import play.api.Play._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

import views._

import org.sh.plc.server.model._
import org.sh.plc.server.services._
import org.sh.plc.server.repo.ListTimelyReport

import org.apache.poi.xssf.usermodel.XSSFWorkbook

import scala.util.control.Exception._
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._

object ReportsController extends Controller with PlcServiceComponent {
  private object ReportsHelper {

    /**
     * Provide the raw data for a report
     */
    def raw(start: String, end: String, report: Int): Tuple2[List[LinkedHashMap[String, Any]], String] = {

      try {
        val formatter = TotalConsumptionModel.Default.dateFormatter
        val startDate = formatter.parse(start)
        val endDate = formatter.parse(end)

        if (startDate.after(endDate)) {
          return (List[LinkedHashMap[String, Any]](), "After field must be less than the Before field")
        }
        
        val types = Array(
          ListTimelyReport.DAILY,
          ListTimelyReport.HOURLY,
          ListTimelyReport.MONTHLY,
          ListTimelyReport.TOTAL)

        var reportType = ListTimelyReport.TOTAL
        val validReport = report >= 0 && report < types.length
        if (validReport) {
          reportType = types(report)
        }

        val items = plcService.listTimelyConsumption(reportType,
          new Timestamp(startDate.getTime()), new Timestamp(endDate.getTime()))

        (items, "")
      } catch {
        case e: ParseException =>
          (List[LinkedHashMap[String, Any]](), "Please select a start and end date")
        case e: Exception =>
          e.printStackTrace()
          (List[LinkedHashMap[String, Any]](), "Please select a start and end date")
      }
    }

    def createReport(data: List[LinkedHashMap[String, Any]]): Array[Byte] = {

      def headers(data: List[LinkedHashMap[String, Any]]): List[String] = {
        try {
          val headers = data(0).keySet().toList

          headers
        } catch {
          case e: IndexOutOfBoundsException =>
            List[String]()
        }
      }

      def rawData(data: List[LinkedHashMap[String, Any]]): List[List[String]] = {
        val rows = new ListBuffer[List[String]]()
        for (row <- data) {
          val cells = new ListBuffer[String]()
          for (cell <- row.values()) {
            cells += (if (cell == null) "" else cell.toString)
          }
          rows += cells.toList
        }

        rows.toList
      }

      val labels = headers(data)
      val cells = rawData(data)
      
      // Convert the data to an Excel sheet

      val wb = new XSSFWorkbook()
      val createHelper = wb.getCreationHelper()
      val sheet = wb.createSheet("Excel Export")

      var row = 0
      var cell = 0

      val headerRow = sheet.createRow(row)
      row += 1
      for (label <- labels) {
        val c = headerRow.createCell(cell)
        c.setCellValue(createHelper.createRichTextString(label))
        cell += 1
      }

      for (dataRow <- cells) {
        val cellRow = sheet.createRow(row)
        row += 1
        cell = 0
        for (dataCell <- dataRow) {
          val c = cellRow.createCell(cell)
          c.setCellValue(createHelper.createRichTextString(dataCell))
          cell += 1
        }
      }

      val bos = new ByteArrayOutputStream()
      try {
        wb.write(bos)
      } finally {
        bos.close()
      }

      bos.toByteArray()
    }

  }

  /**
   * Total Consumption form
   */
  val consumptionForm = Form[(String, String)](
    tuple(
      "start" -> nonEmptyText,
      "end" -> nonEmptyText))

  /**
   * Report generation page
   *
   *
   */
  def generateReport(start: String, end: String, report: Int) = Action {

    import ReportsHelper._

    val t = raw(start, end, report)
    val items = t._1
    val errors = t._2

    Ok(html.Reports.generateReport(start, end, report, items, errors))
  }

  /**
   *
   */
  def generateExcelReport(start: String, end: String, report: Int) = Action {
    import ReportsHelper._

    val bytes = createReport(raw(start, end, report)._1)

    Ok(bytes).withHeaders(
      CONTENT_DISPOSITION -> "attachment; filename=export.xlsx",
      CONTENT_TYPE -> "application/octet-stream")
  }

  /**
   *
   */
  def generateJsonReport(start: String, end: String, report: Int) = Action {
    import ReportsHelper._

    val bytes = createReport(raw(start, end, report)._1)

    Ok(bytes).withHeaders(
      CONTENT_DISPOSITION -> "attachment; filename=export.xlsx",
      CONTENT_TYPE -> "application/octet-stream")
  }

  /**
   *
   */
  def generateExcelPlcEvents() = Action {
    import ReportsHelper._

    val plcEvents = plcService.listPlcEvents()

    val bytes = createReport(plcEvents)

    Ok(bytes).withHeaders(
      CONTENT_DISPOSITION -> "attachment; filename=export.xlsx",
      CONTENT_TYPE -> "application/octet-stream")
  }
}