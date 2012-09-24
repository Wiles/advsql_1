/**
 * FILE: PlcEmulator.scala
 * PROJECT: Advanced SQL #1
 * PROGRAMMER: Hekar Khani, Samuel Lewis
 * FIRST VERSION: September 10, 2012
 * DESCRIPTION:
 * 	Emulator of the PLC hardware logic
 */

package org.sh.plc.emulator

import java.util.Date

/**
 * Represents a single PLC device
 *
 * @param tickRate the number of ticks the simulated PLC generates per-seconds
 */
class PlcEmulator(val tickRate: Double) {
  require(tickRate > 0)
  /**
   * Time of last read
   */
  private var lastRead = new Date().getTime()

  /**
   * Calculate the energy usage since the last time
   * this function has been called
   */
  def energyUsage(): Long = {
    var curTime = new Date().getTime()
    var oldTime = lastRead
    lastRead = curTime
    (((curTime - oldTime) * tickRate) / 1000L).asInstanceOf[Long]
  }
}