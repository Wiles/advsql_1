/**
 * FILE: PlcCommunicator.scala
 * PROJECT: Advanced SQL #1
 * PROGRAMMER: Hekar Khani
 * FIRST VERSION: September 11, 2012
 * DESCRIPTION:
 * 	Logic that deals with commmunication between the Plc
 */

package org.sh.plc.server.communicator

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
  val delimiter = "|"
  
  def energyUsage(plcId: Long): EnergyUsage = {
    
    var in: DataInputStream = null
    var out: BufferedWriter = null
    var socket: Socket = null
    
    try {
      val requestContents = "R|%d".format(plcId)
      
      //TODO: make host and port configurable
      val ia = InetAddress.getByName("localhost")
      socket = new Socket(ia, 9999)
      
      in = new DataInputStream(socket.getInputStream())
      out = new BufferedWriter(
        new OutputStreamWriter(socket.getOutputStream()))
      

      out.write(requestContents)
      out.flush()

      val responseContents = in.readLine()
      
      val values = responseContents.split(delimiter)
      
      if (values.isEmpty) {
    	  //TODO: log error
        throw new Exception();
      } else if (values(0) == "R" && values.length > 1) {
    	  // TODO: protocol
    	  return new EnergyUsage(plcId, values(1).asInstanceOf[Long], new Timestamp(0), new Timestamp(new Date().getTime()))
      } else {
        //TODO: log error to db
        throw new Exception();
      }
    }
    catch {
      case e: Exception => {
    	  e.printStackTrace()
    	  throw e
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
