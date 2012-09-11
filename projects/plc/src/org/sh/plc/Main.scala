/**
 * File: Main.scala
 * Author(s): Hekar Khani
 * Date: September 10, 2012
 * Description:
 * 	Mainline. Starts the server
 */

package org.sh.plc

import org.sh.plc.server.PlcServer

object Main {
  def main(args: Array[String]): Unit = {
    PlcServer.start()
  }
}