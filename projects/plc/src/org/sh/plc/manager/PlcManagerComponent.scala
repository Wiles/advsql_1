/**
 * FILE: PlcManagerComponent.scala
 * PROJECT: Advanced SQL #1
 * PROGRAMMER: Hekar Khani
 * FIRST VERSION: September 11, 2012
 * DESCRIPTION:
 * 	Plc manager classes and cake component model
 */
package org.sh.plc.manager

import org.sh.plc.conf.Configuration
import scala.collection.mutable.ArrayBuffer
import org.sh.plc.emulator.PlcEmulator

/**
 * Interface for a Plc manager.
 * 
 * The plc manager's job is to contain and
 * provide actions on multiple plc devices.
 */
sealed trait PlcManager {
  def all(): Array[PlcEmulator]
  def byId(plcId: Int): PlcEmulator
  def exists(plcId: Int): Boolean
}

/**
 * Default implementation of a plc manager.
 */
private class DefaultPlcManager extends PlcManager {

  /**
   * plcs
   */
  val plcs: Array[PlcEmulator] = Configuration.rates.map(new PlcEmulator(_))

  /**
   * Array of all the plcs
   */
  override def all() = plcs

  /**
   * Get a plc by id
   */
  override def byId(plcId: Int): PlcEmulator = {
    plcs(plcId)
  }

  /**
   * Does the plc exist?
   */
  override def exists(plcId: Int): Boolean = {
    return if (plcId > 0 && plcId < all().size) true else false;
  }
}

/**
 * Component for dependency injection
 */
object PlcManagerComponent {
  /**
   * Singleton
   */
  lazy val instance: PlcManager = new DefaultPlcManager()
}

/**
 * Component for dependency injection 
 */
class PlcManagerComponent {
  /**
   * Instance of plc manager
   */
  val plcManager = PlcManagerComponent.instance
}