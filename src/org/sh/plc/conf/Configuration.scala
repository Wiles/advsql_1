/**
 * File: Configuration.scala
 * Author(s): Hekar Khani
 * Date: September 10, 2012
 * Description:
 * 	Contains the necessary contents for a configuration file
 */
package org.sh.plc.conf

import java.io._
import java.net._
import scala.collection.mutable.LinkedHashMap
import scala.io.Source
import org.sh.plc.utils.FileUtils

sealed trait ConfigurationValues {
  var verbose = false
  var port = 9005
}

private object ConfigurationTypes extends Enumeration {
  val Application, User, Default = Value
}

private class ConfigurationReader(val file: File) extends ConfigurationValues {
  require(file != null)
  require(file.exists())

  def fill(c: ConfigurationValues): Unit = {
    parse(file)
    c.verbose = true
  }

  private def parse(file: File): Unit = {
    require(file != null)

    try {
      val contents = FileUtils.readFile(file)
      
      // TODO: Parse XML
    } catch {
      case e: FileNotFoundException =>
        throw new Exception("Failure to open file: %s".format(file.getPath()), e)
      case e: IOException =>
        throw new Exception("Error while reading file: %s".format(file.getPath()), e)
    }
  }
}

/**
 * Global configuration for the program
 */
object Configuration extends ConfigurationValues {

  private val jarLocation = URLDecoder.decode(
    classOf[ConfigurationValues].getProtectionDomain()
      .getCodeSource().getLocation().getPath())
  private val folderLocation = "config"
  private val configFilename = "plc-configuration.xml"

  // Configuration files in order of desired usage
  private val possibleConfigurationFiles = LinkedHashMap(

    // Check the user's current folder
    ConfigurationTypes.User ->
      new File(folderLocation, configFilename),

    // Check the application's folder
    ConfigurationTypes.Application ->
      new File(new File(jarLocation, folderLocation), configFilename),

    // Check the application jar itself for the file
    ConfigurationTypes.Default ->
      new File(classOf[ConfigurationValues]
        .getResource("/org/sh/resource/%s".format(configFilename)).toURI())
  )

  // Select the first configuration
  private val configOption = possibleConfigurationFiles
    .filter(_._2.exists())
    .firstOption

  // Perform pattern matching for the selection of the configuration
  configOption match {
    
    case Some((configType, file)) if configType != ConfigurationTypes.Default => {
      new ConfigurationReader(file).fill(this)
    }
    
    case Some((ConfigurationTypes.Default, file)) => {
      println("""
          #"WARNING - Using default configuration file, 
          # as configuration not found in the following paths: %s""".stripMargin('#')
        .format(possibleConfigurationFiles
          .filter(_._1 != ConfigurationTypes.Default)
          .map(_._2.getPath())
          .toList.mkString)
      )

      new ConfigurationReader(file).fill(this)
    }
    
    case None => {
      println("""
          #ERROR - Failure to load configuration files.
          #Please contact your software application provider""".stripMargin('#'))
      throw new FileNotFoundException("No configuration file could be loaded")
    }
  }
}