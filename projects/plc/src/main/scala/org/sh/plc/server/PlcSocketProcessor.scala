/**
 * FILE: PlcSocketProcessor.scala
 * PROJECT: Advanced SQL #1
 * PROGRAMMER: Hekar Khani, Samuel Lewis
 * FIRST VERSION: September 13, 2012
 * DESCRIPTION:
 * 	Process the commands coming through the socket
 */

package org.sh.plc.server

import scala.util.Random
import org.sh.plc.conf.Configuration

import org.sh.plc.manager.PlcManagerComponent

/**
 * Response to send back to a client connection
 */
class SocketResponse(val content: String)

/**
 * Request from a client connection
 */
class SocketRequest(val content: String)

/**
 * Unit whose function serves to process incoming requests
 * and create responses
 */
trait SocketProcessor {
  def request(request: SocketRequest): SocketResponse
}

/**
 * Default socket processor component for the PLC server
 */
trait SocketProcessorComponent extends PlcManagerComponent {
  
  /**
   * Default instance of socket processor
   */
  val socketProcessor: SocketProcessor = new PlcSocketProcessor()

  sealed class PlcSocketProcessor extends SocketProcessor {

    /**
     * @return standard read response
     */
    private def readResponse(plc: Int, pulses: Long): SocketResponse = {
      new SocketResponse("R|" + plc + "|" + pulses + "\r\n")
    }

    /**
     * @return error response
     */
    private def errorResponse(): SocketResponse = {
      new SocketResponse("F\r\n")
    }

    /**
     * Process an incoming request
     * @param request request from client to process
     * @return response to pass back to client
     */
    def request(request: SocketRequest): SocketResponse = {
      val line = request.content
      val query = line.split("\\|")

      val validQuery = query.length == 2
      if (validQuery) {
        try {
          val command = query(0)
          val plc = query(1).toInt - 1
          if (command == "R") {
            if (Configuration.failureRate != 0 && Random.nextInt % Configuration.failureRate == 0) {
              // Send an error response on every 100th request
              errorResponse()
            } else {
              readResponse(plc + 1, plcManager.byId(plc).energyUsage)
            }
          } else {
            errorResponse()
          }
        } catch {
          case e: Exception =>
            return errorResponse()
        }
      } else {
        errorResponse()
      }
    }
  }
} 