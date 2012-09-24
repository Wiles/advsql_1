/**
 * FILE: Configuration.scala
 * PROJECT: Advanced SQL #1
 * PROGRAMMER: Hekar Khani, Samuel Lewis
 * FIRST VERSION: September 10, 2012
 * DESCRIPTION:
 * 	Contains the necessary contents for a configuration file
 */
package org.sh.plc.conf

import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.net.URLDecoder
import java.util.Properties

import scala.Array.canBuildFrom
import scala.collection.mutable.LinkedHashMap

/**
 * Configuration values for the PLC server
 */
sealed trait ConfigurationValues {
  var verbose = false
  var port = 9005
  var rates = Array(100.0, 100.0, 100.0)
}

/**
 * Types of configuration files to open
 */
private object ConfigurationTypes extends Enumeration {
  val Application, User, Default = Value
}

/**
 * Configuration file reader
 */
private class ConfigurationReader(val file: File) extends ConfigurationValues {
  
  require(file != null)
  require(file.exists())

  /**
   * Fill in the configuration
   */
  def fill(c: ConfigurationValues): Unit = {
    val props = parse(file)
    
    c.verbose = props.getProperty("verbose", verbose.toString).equalsIgnoreCase("true")
    try {
    	c.port = props.getProperty("port", port.toString).toInt
    } catch {
      case e: NumberFormatException => {
        Logger.log(e, "[Configuration] Failure to parse port number")
      }
    }
    
    try {
      if (props.getProperty("rates") != "null") {
    	  c.rates = props.getProperty("rates").split(",").map(_.toDouble)
      }
    } catch {
      case e: Exception => {
        Logger.log(e, "[Configuration] Failure to parse rates")
      }
    }
    Logger.log("[Configuration] %d plcs running at tick rates %s".format(c.rates.length, c.rates.mkString(" ")))
  }

  /**
   * Parse a properties file for configuration files
   */
  private def parse(file: File): Properties = {
    require(file != null)
    
    val properties = new Properties()
    
    var propertiesStream : FileInputStream = null
    try {
      propertiesStream = new FileInputStream(file)
      properties.loadFromXML(propertiesStream)
    } catch {
      case e: FileNotFoundException => {
        Logger.log(e)
        throw new Exception("Failure to open file: %s".format(file.getPath()), e)
      }
      case e: IOException => {
        Logger.log(e)
        throw new Exception("Error while reading file: %s".format(file.getPath()), e)
      }
    } finally {
      if (propertiesStream != null) {
        propertiesStream.close()
      }
    }
    
    return properties
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
      new File(new File(jarLocation, folderLocation), configFilename)
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
          #WARNING - Using default configuration file, 
          #as configuration not found in the following paths: %s""".stripMargin('#')
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