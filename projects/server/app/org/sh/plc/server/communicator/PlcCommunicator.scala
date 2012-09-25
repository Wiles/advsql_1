/**
 * FILE: PlcCommunicator.scala
 * PROJECT: Advanced SQL #1
 * PROGRAMMER: Hekar Khani
 * FIRST VERSION: September 11, 2012
 * DESCRIPTION:
 * 	Logic that deals with commmunication between the Plc
 */

package org.sh.plc.server.communicator

import java.io._
import java.net._

import java.sql.Timestamp

import java.util.Date
import java.util.Calendar

import scala.util.control.Exception._

import play.api._
import play.api.Play._

import org.sh.plc.server.model.EnergyUsage
import org.sh.plc.server.services.PlcServiceComponent

/**
 * Provide an interface to communicate with a Plc
 */
class PlcCommunicator(val address: String, val port: Int) extends PlcServiceComponent {

  /**
   * Delimiter for response values from the PLC server
   */
  val Delimiter = "\\|"

  val DefaultTimeout = 30000 // milliseconds

  /**
   * Get the energy usage for a PLC
   */
  def pollAndUpdateDatabase(plcId: Long): Unit = {

    // Stupid Scala library doesn't have 'using' statement...
    var in: BufferedReader = null
    var out: BufferedWriter = null
    var socket: Socket = null

    try {
      /**
       * TODO: Do not reconnect on every request
       */
      socket = new Socket()
      socket.connect(new InetSocketAddress(address, port), DefaultTimeout)

      in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
      out = new BufferedWriter(
        new OutputStreamWriter(socket.getOutputStream()))

      val requestContents = "R|%d\r\n".format(plcId)
      out.write(requestContents)
      out.flush()

      val responseContents = in.readLine()

      val values = responseContents.split(Delimiter)

      val currentTimestamp = new Timestamp(Calendar.getInstance().getTime().getTime())
      
      if (values.isEmpty) {
        throw new Exception("Failure to read values from response for PLC #%d".format(plcId));
      } else if (values.length > 2 && values(0) == "R") {
        val responsePlcId = values(1).toInt
        val usage = values(2).toInt

        Logger.debug("PLC #%d usage: %d".format(plcId, usage))
        
        val energyUsage = new EnergyUsage(plcId, usage, currentTimestamp)
        
        plcService.logEnergyUsage(energyUsage)
      } else {
        plcService.logFailureEvent(plcId, currentTimestamp)
      }
      
    } finally {
      if (out != null) {
        out.close()
      }
      
      if (in != null) {
        in.close()
      }
      
      if (socket != null) {
        socket.close()
      }
    }
  }
}
