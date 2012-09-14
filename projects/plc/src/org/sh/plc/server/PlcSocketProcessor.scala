/**
 * FILE: PlcSocketProcessor.scala
 * PROJECT: Advanced SQL #1
 * PROGRAMMER: Hekar Khani
 * FIRST VERSION: September 13, 2012
 * DESCRIPTION:
 * 	Process the commands coming through the socket
 */

package org.sh.plc.server

import org.sh.plc.manager._

class SocketResponse(val content: String)

class SocketRequest(val content: String)

trait SocketProcessor {
  def request(request: SocketRequest): SocketResponse
}

trait SocketProcessorComponent extends PlcManagerComponent {
  val socketProcessor: SocketProcessor = new PlcSocketProcessor()

  sealed class PlcSocketProcessor extends SocketProcessor {
    private def errorResponse(): SocketResponse = {
      new SocketResponse("F\r\n")
    }

    def request(request: SocketRequest): SocketResponse = {
      val line = request.content
      val query = line.split("|")

      if (query.length == 2) {
        try {
        	val command = query(0)
    		val plc = query(1).toInt
    		if (command == "query") {
    			
    		}
        } catch {
        case e: Exception =>
            return errorResponse()
        }

        errorResponse()
      } else {
        errorResponse()
      }
    }
  }
} 