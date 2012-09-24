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
import org.sh.plc.conf._

/**
 * Handles Socket communication to the
 * outside world
 */
object PlcServer {
  /**
   * Starts the server listening on the configured port.
   * Creates a new thread for each incoming connection
   */
  def start(socketProcessor: SocketProcessor): Unit = {
    try {
      val port = Configuration.port
      
      Logger.log("[Configuration] Using port: %s".format(port))
      
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
      val out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
      val in = new BufferedReader(
        new InputStreamReader(socket.getInputStream()))

      val request = new SocketRequest(in.readLine())
      Logger.log("[Server] Request: %s".format(request.content))
      val response = socketProcessor.request(request)
      
      Logger.log("[Server] Response: %s".format(response.content))
      out.write(response.content)
      out.flush()
      
      out.close()
      in.close()
      socket.close()
    } catch {
      case e: SocketException => {
        Logger.log(e)
        () // avoid stack trace when stopping a client with Ctrl-C
      }
      case e: Exception => Logger.log(e)
    }
  }

}