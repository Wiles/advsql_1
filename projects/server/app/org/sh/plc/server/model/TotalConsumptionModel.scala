package org.sh.plc.server.model

import java.text._

object TotalConsumptionModel {
  object Key {
    val start = "start"
    val end = "end"
  }
  object Default {
    val dateFormat = "dd/MM/yyyy KK:mm"
    val dateFormatter = new SimpleDateFormat(dateFormat)
  }
}

/**
 * This is the model for the total consumption form
 *
 * Because this is used by the form, we cannot use Date,
 * we must use string instead
 */
case class TotalConsumptionModel(start: String, end: String)