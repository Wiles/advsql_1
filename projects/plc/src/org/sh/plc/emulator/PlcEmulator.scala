/**
 * FILE: PlcEmulator.scala
 * PROJECT: Advanced SQL #1
 * PROGRAMMER: Hekar Khani
 * FIRST VERSION: September 10, 2012
 * DESCRIPTION:
 * 	Emulator of the PLC hardware logic
 */

package org.sh.plc.emulator

import java.util.Date

class PlcEmulator(val tickRate: Double) {
  
  private var lastRead = new Date().getTime()
  
	def read() : Long = {
	  var curTime = new Date().getTime()
	  var oldTime = lastRead
	  lastRead = curTime
	  (((curTime - oldTime) * tickRate)/1000L).asInstanceOf[Long]
	}
}