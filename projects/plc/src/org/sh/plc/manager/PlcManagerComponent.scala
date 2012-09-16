package org.sh.plc.manager

import org.sh.plc.conf.Configuration
import scala.collection.mutable.ArrayBuffer
import org.sh.plc.emulator.PlcEmulator

sealed trait PlcManager {
  def all(): Array[PlcEmulator]
  def byId(plcId: Int): PlcEmulator
  def exists(plcId: Int): Boolean
}

private class DefaultPlcManager extends PlcManager {
  var buffer: ArrayBuffer[PlcEmulator] = new ArrayBuffer()

  for (rate <- Configuration.rates) {
    buffer += new PlcEmulator(rate.toDouble)
  }

  val plcs: Array[PlcEmulator] = buffer.toArray

  override def all() = plcs

  override def byId(plcId: Int): PlcEmulator = {
    plcs(plcId)
  }

  override def exists(plcId: Int): Boolean = {
    return if (plcId > 0 && plcId < all().size) true else false;
  }
}

object PlcManagerComponent {
  lazy val instance: PlcManager = new DefaultPlcManager()
}

class PlcManagerComponent {
  val plcManager = PlcManagerComponent.instance
}