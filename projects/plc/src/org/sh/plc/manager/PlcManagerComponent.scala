package org.sh.plc.manager
import org.sh.plc.PlcEmulator
import org.sh.plc.PlcEmulator

trait PlcManager {
  def all(): Array[PlcEmulator]
  def byId(plcId: Int): PlcEmulator
  def exists(plcId: Int): Boolean
}

class PlcManagerComponent {
  val plcManager: PlcManager = new DefaultPlcManager

  private class DefaultPlcManager extends PlcManager {
    val tickRate = 100.0
    val plcs = Array(new PlcEmulator(tickRate), 
        new PlcEmulator(tickRate), 
        new PlcEmulator(tickRate))
        
    def all() = plcs
    
    def byId(plcId: Int): PlcEmulator = {
      require(exists(plcId))
      plcs(plcId)
    }
    
    def exists(plcId: Int): Boolean = {
    	return if (plcId > 0 && plcId < plcs.size) true else false;
    }
  }
}