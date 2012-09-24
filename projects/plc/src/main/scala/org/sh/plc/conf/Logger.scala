/**
 * FILE: Logger.scala
 * PROJECT: Advanced SQL #1
 * PROGRAMMER: Hekar Khani
 * FIRST VERSION: September 10, 2012
 * DESCRIPTION:
 * 	As third party libraries (besides Scala) will 
 * 	most likely not be included in this project,
 *  we have to create a crappy logger.
 */

package org.sh.plc.conf

/**
 * Crappy logger.
 */
object Logger {
  
  /**
   * Write a custom message to the log
   */
  def log(message: String): Unit = {
    println(message)
  }

  /**
   * Write an Exception to the log
   */
  def log(e: Exception): Unit = {
    println(e.getMessage())
    if (Configuration.verbose) {
      e.printStackTrace();
    }
  }
  
  /**
   * Write and Exception and a custom message to the log
   */
  def log(e: Exception, message: String): Unit = {
    log(message)
    log(e)
  }
}