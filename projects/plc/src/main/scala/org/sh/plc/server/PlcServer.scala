/**
 * FILE: PlcServer.scala
 * PROJECT: Advanced SQL #1
 * PROGRAMMER: Hekar Khani, Samuel Lewis
 * FIRST VERSION: September 10, 2012
 * DESCRIPTION:
 * 	The code in this file handles the server
 *  socket processing.
 */

package org.sh.plc.server

import java.io._
import java.net._
import java.util._
import org.sh.plc._
import org.sh.plc.conf._
import org.sh.plc.manager._
import org.sh.plc.emulator._

/**
 * 
 */
object PlcServer {
  /**
   * 
   */
  def start(socketProcessor: SocketProcessor): Unit = {
    try {
      val port = Configuration.port
      
      Logger.log("Using port: %s".format(port))
      
      val listener = new ServerSocket(port)
      while (true) {
        new ServerThread(listener.accept(), socketProcessor).start()
      }
      listener.close()
    } catch {
      case e: IOException => {
        System.err.println("Could not listen on port: 9999.")
        System.exit(-1)
      }
    }
  }

}

/**
 * Thread to handle client request
 */
class ServerThread(val socket: Socket, 
    val socketProcessor: SocketProcessor) extends Thread("ServerThread") {

  override def run(): Unit = {
    try {
      val out = new DataOutputStream(socket.getOutputStream())
      val in = new BufferedReader(
        new InputStreamReader(socket.getInputStream()))

      val request = new SocketRequest(in.readLine())
      val response = socketProcessor.request(request)
      
      out.write(response.content.getBytes())
      
      out.close()
      in.close()
      socket.close()
    } catch {
      case e: SocketException => {
        Logger.log(e)
        () // avoid stack trace when stopping a client with Ctrl-C
      }
      case e: IOException => {
        e.printStackTrace()
      }
    }
  }

}