/**
 * FILE: PlcCommunicator.scala
 * PROJECT: Advanced SQL #1
 * PROGRAMMER: Hekar Khani
 * FIRST VERSION: September 11, 2012
 * DESCRIPTION:
 * 	Logic that deals with commmunication between the Plc
 */

package org.sh.plc.server.communicator

import play.api._
import play.api.Play._

import java.io.BufferedWriter
import java.io.DataInputStream
import java.io.OutputStreamWriter
import java.net.InetAddress
import java.net.Socket
import java.sql.Timestamp
import org.sh.plc.server.model.EnergyUsage
import java.util.Date

/**
 * Provide an interface to communicate with a Plc
 */
class PlcCommunicator {

  /**
   * Delimiter for response values from the PLC server
   */
  val Delimiter = "|"

  val DefaultHost = "localhost"
  val DefaultPort = 9999

  /**
   * Get the energy usage for a PLC
   */
  def energyUsage(plcId: Long): EnergyUsage = {

    // Stupid Scala library doesn't have 'using' statement...
    var in: DataInputStream = null
    var out: BufferedWriter = null
    var socket: Socket = null

    try {
      val configuration = Play.application.configuration

      val hostname = configuration.getString("oplc_server_address").getOrElse({
    	  // TODO: Fix continual warning that will annoy users/admins...
        Logger.warn("Failure to find oplc_server_address configured in Play Application. Using %s".format(DefaultHost))
        "localhost"
      })

      val port = configuration.getInt("oplc_server_port").getOrElse({
    	  // TODO: Fix continual warning that will annoy users/admins...
        Logger.warn("Failure to find oplc_server_port configured in Play Application. Using %d".format(DefaultPort))
        DefaultPort
      })

      val address = InetAddress.getByName(hostname)
      socket = new Socket(address, port)

      in = new DataInputStream(socket.getInputStream())
      out = new BufferedWriter(
        new OutputStreamWriter(socket.getOutputStream()))

      Logger.debug("a")
      val requestContents = "R|%d".format(plcId)
      out.write(requestContents)
      Logger.debug("b")
      out.flush()
      
      Logger.debug("c")
      val responseContents = in.readUTF()
      Logger.debug("d")

      val values = responseContents.split(Delimiter)
      Logger.debug(values.mkString)
      if (values.isEmpty) {
        throw new Exception("Failure to read values from response for PLC #: %d".format(plcId));
      } else if (values.length > 2 && values(0) == "R") {
    	  val responsePlcId = values(1).toInt
        val usage = values(2).toInt
        // TODO: store previous time...
        Logger.debug("PLC #%d usage: %d".format(plcId))
        return new EnergyUsage(plcId, values(1).asInstanceOf[Long], new Timestamp(0), new Timestamp(new Date().getTime()))
      } else {
        //TODO: log error to db?
        throw new Exception();
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
