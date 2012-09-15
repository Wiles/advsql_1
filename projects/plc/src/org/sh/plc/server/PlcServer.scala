/**
 * FILE: PlcServer.scala
 * PROJECT: Advanced SQL #1
 * PROGRAMMER: Hekar Khani
 * FIRST VERSION: September 10, 2012
 * DESCRIPTION:
 * 	The code in this file handles the server
 *  socket processing.
 */

package org.sh.plc.server

import java.io._
import java.net._
import java.util._
import org.sh.plc.conf._
import org.sh.plc._

object PlcServer {

  private var plc = Array(new PlcEmulator(5.0), new PlcEmulator(10.0), new PlcEmulator(15.0))
  
  def getPLC(index: Int): PlcEmulator = {
    plc(index)
  }
  
  def start(socketProcessor: SocketProcessor): Unit = {
    try {
      val listener = new ServerSocket(9999)
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

case class ServerThread(val socket: Socket, 
    val socketProcessor: SocketProcessor) extends Thread("ServerThread") {

  override def run(): Unit = {
    val rand = new Random(System.currentTimeMillis())
    try {
      val out = new DataOutputStream(socket.getOutputStream())
      val in = new BufferedReader(
        new InputStreamReader(socket.getInputStream()))

      val line = in.readLine()
      val response = socketProcessor.request(new SocketRequest(line))
      
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