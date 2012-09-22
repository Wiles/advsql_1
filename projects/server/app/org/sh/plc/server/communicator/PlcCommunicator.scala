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
import java.sql._
import org.sh.plc.server.model._


/**
 * Provide an interface to communicate with a Plc
 */
class PlcCommunicator {

  def energyUsage(plcId: Long): EnergyUsage = {
    
    var in: DataInputStream = null
    var out: BufferedWriter = null
    var socket: Socket = null
    
    try {
      val requestContents = "R|%d".format(plcId)
      
      val ia = InetAddress.getByName("localhost")
      socket = new Socket(ia, 9999)
      
      in = new DataInputStream(socket.getInputStream())
      out = new BufferedWriter(
        new OutputStreamWriter(socket.getOutputStream()))
      

      out.write(requestContents)
      out.flush()

      val responseContents = in.readLine()
      
      // TODO: protocol
      return new EnergyUsage(0, new Timestamp(0), new Timestamp(0))
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
