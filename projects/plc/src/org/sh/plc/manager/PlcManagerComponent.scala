package org.sh.plc.manager


import org.sh.plc.conf.Configuration
import scala.collection.mutable.ArrayBuffer
import org.sh.plc.emulator.PlcEmulator

trait PlcManager {
  def all(): Array[PlcEmulator]
  def byId(plcId: Int): PlcEmulator
  def exists(plcId: Int): Boolean
}

class PlcManagerComponent {
  def plcManager: PlcManager = new DefaultPlcManager()
  private class DefaultPlcManager extends PlcManager {
    var buffer:ArrayBuffer[PlcEmulator] = new ArrayBuffer()
    
    //TODO: Get rates from configuration
    var t = "10.0,10.0,10.0".split(",")
    for ( rate <-  t ) {
      buffer += new PlcEmulator(rate.toDouble)
    }
    val plcs : Array[PlcEmulator] = buffer.toArray
        
    def all() = plcs
    
    def byId(plcId: Int): PlcEmulator = {
      plcs(plcId)
    }
    
    def exists(plcId: Int): Boolean = {
    	return if (plcId > 0 && plcId < all().size) true else false;
    }
  }
}