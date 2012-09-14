/**
 * FILE: Main.scala
 * PROJECT: Advanced SQL #1
 * PROGRAMMER: Hekar Khani
 * FIRST VERSION: September 10, 2012
 * DESCRIPTION:
 * 	Mainline. Starts the server
 */

package org.sh.plc

import org.sh.plc.server._

object Main extends SocketProcessorComponent {
  def main(args: Array[String]): Unit = {
    PlcServer.start(socketProcessor)
  }
}