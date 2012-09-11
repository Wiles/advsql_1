/**
 * File: PlcServer.scala
 * Author(s): Hekar Khani
 * Date: September 10, 2012
 * Description:
 * 	The code in this file handles the server
 *  socket processing.
 */

package org.sh.plc.server

import java.io.ObjectInputStream
import java.net.ServerSocket
import java.io.IOException
import java.net.Socket
import java.net.SocketException
import java.util.Random
import java.io.DataOutputStream
import java.io.DataInputStream
import org.sh.plc.conf.Logger
import org.sh.plc.conf.Logger

object PlcServer {

  def start(): Unit = {
        try {
      val listener = new ServerSocket(9999);
      while (true)
        new ServerThread(listener.accept()).start();
      listener.close()
    }
    catch {
      case e: IOException =>
        System.err.println("Could not listen on port: 9999.");
        System.exit(-1)
    }
  }

}

case class ServerThread(socket: Socket) extends Thread("ServerThread") {

  override def run(): Unit = {
    val rand = new Random(System.currentTimeMillis());
    try {
      val out = new DataOutputStream(socket.getOutputStream());
      val in = new ObjectInputStream(
        new DataInputStream(socket.getInputStream()));

      val filter = in.readObject().asInstanceOf[Int => Boolean];

      while (true) {
        var succeeded = false;
        do {
          val x = rand.nextInt(1000);
          succeeded = filter(x);
          if (succeeded) out.writeInt(x)
        } while (! succeeded);
        Thread.sleep(100)
      }

      out.close();
      in.close();
      socket.close()
    }
    catch {
      case e: SocketException => {
        Logger.log(e)
        () // avoid stack trace when stopping a client with Ctrl-C
      }
      case e: IOException => {
        e.printStackTrace();
      }
    }
  }

}