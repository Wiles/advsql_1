/**
 * FILE: TimelyConsumptionRow.scala
 * PROJECT: Advanced SQL #1
 * PROGRAMMER: Hekar Khani
 * FIRST VERSION: September 15, 2012
 * DESCRIPTION:
 *
 */
package org.sh.plc.server.model

/**
 * @param plc
 * id of plc
 * @param usage
 * amount of energy used
 * @param hour
 * @param dayOfYear
 * @param month
 * @param year
 */
class TimelyConsumptionRow(
  val plc: Long,
  val usage: Long,
  val hour: Long,
  val dayOfYear: Long,
  val month: Long,
  val year: Long
)