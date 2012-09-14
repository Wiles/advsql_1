/**
 * FILE: FileUtils.scala
 * PROJECT: Advanced SQL #1
 * PROGRAMMER: Hekar Khani
 * FIRST VERSION: September 10, 2012
 * DESCRIPTION:
 * 	Miscellaneous File utilities
 */

package org.sh.plc.utils
import scala.io.Source
import java.io.File

/**
 * Some utilities to read files
 *
 * Scala has a garbage standard library...
 * @author hekar
 *
 */
object FileUtils {

  def readFile(file: File): String = {
    val sb = new StringBuilder()
    Source.fromFile(file).getLines.foreach((line) => {
      sb.append(line.trim.toUpperCase)
    })
    sb.toString()
  }

}