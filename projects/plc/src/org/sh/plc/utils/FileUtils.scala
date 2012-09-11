/**
 * File: FileUtils.scala
 * Author(s): Hekar Khani
 * Date: September 10, 2012
 * Description:
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