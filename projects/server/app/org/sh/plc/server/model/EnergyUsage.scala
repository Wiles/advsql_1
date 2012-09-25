/**
 * FILE: EnergyUsage.scala
 * PROJECT: Advanced SQL #1
 * PROGRAMMER: Hekar Khani
 * FIRST VERSION: September 13, 2012
 * DESCRIPTION:
 * 	Models pertaining to energy usage
 */
package org.sh.plc.server.model

import java.sql._

/**
 * Energy usage tracker
 * @param plc
 * id of the plc
 * @param usage
 * 	amount of energy used
 * @param created
 * 	time at which energy usage tracking was created
 */
class EnergyUsage(val plc: Long, val usage: Long, val created: Timestamp)