/**
 * File: Logger.scala
 * Author(s): Hekar Khani
 * Date: September 10, 2012
 * Description:
 * 	As third party libraries (besides Scala) will 
 * 	most likely not be included in this project,
 *  we have to create a crappy logger.
 */

package org.sh.plc.conf

/**
 * Crappy logger.
 */
object Logger {
  def log(message: String): Unit = {
    println(message)
  }

  def log(e: Exception): Unit = {
    println(e.getMessage())
    if (Configuration.verbose) {
      e.printStackTrace();
    }
  }
}