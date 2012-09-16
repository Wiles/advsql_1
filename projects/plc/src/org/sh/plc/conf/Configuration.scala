/**
 * FILE: Configuration.scala
 * PROJECT: Advanced SQL #1
 * PROGRAMMER: Hekar Khani
 * FIRST VERSION: September 10, 2012
 * DESCRIPTION:
 * 	Contains the necessary contents for a configuration file
 */
package org.sh.plc.conf

import java.io._
import java.net._
import java.util.Properties
import scala.collection.mutable.LinkedHashMap
import scala.io.Source

sealed trait ConfigurationValues {
  var verbose = false
  var port = 9005
  var rates = "100.0,100.0,100.0"
}

private object ConfigurationTypes extends Enumeration {
  val Application, User, Default = Value
}

private class ConfigurationReader(val file: File) extends ConfigurationValues {
  
  private var properties = new Properties()
      
  require(file != null)
  require(file.exists())

  def fill(c: ConfigurationValues): Unit = {
    parse(file)
    c.verbose = true
  }

  private def parse(file: File): Unit = {
    require(file != null)
    //TODO Doing this the java way. Not sure if there a better way.
    var propertiesStream : FileInputStream = null
    try {
      propertiesStream = new FileInputStream(file)
      properties.loadFromXML(propertiesStream)
    } catch {
      case e: FileNotFoundException =>
        throw new Exception("Failure to open file: %s".format(file.getPath()), e)
      case e: IOException =>
        throw new Exception("Error while reading file: %s".format(file.getPath()), e)
    } finally {
      if (propertiesStream != null) {
        propertiesStream.close()
      }
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