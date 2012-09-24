/**
 * FILE: PlcModel.scala
 * PROJECT: Advanced SQL #1
 * PROGRAMMER: Hekar Khani
 * FIRST VERSION: September 14, 2012
 * DESCRIPTION:
 * 
 */
package org.sh.plc.server.model

import java.util.Date
import java.math.BigDecimal

case class PlcStatusModel(
	id: Long, 
	name: String, 
	status: String, 
	end: String, 
	usage: Long, 
	total: BigDecimal
)