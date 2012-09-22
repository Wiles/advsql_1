/**
 * FILE: DatabaseSetupSpec.scala
 * PROJECT: Advanced SQL #1
 * PROGRAMMER: Hekar Khani
 * FIRST VERSION: September 13, 2012
 * DESCRIPTION:
 * 	Constants dealing with the automatic initialization of the
 * 	database schema
 */
package org.sh.plc.server.jobs


object DatabaseSetupConstants {
  /**
   * Table DDLs
   */
  val tables = Map(
    "plc" ->
      """
	  id long primary key auto_increment,
	  name varchar(256) not null
      	""",

    "plc_status" ->
      """
	  id long primary key auto_increment,
	  name varchar(128) not null
      	""",

    "plc_event" ->
      """
	  id long primary key auto_increment,
	  plc long references plc(id),
	  status long references plc_status(id),
	  start date not null,
	  end date not null
      	""",

    "plc_settings" ->
      """
	  key varchar(64) primary key,
	  value varchar(256)
      	"""
  )

  /**
   * ID -> PLC name
   */
  val plcs = Map(
    0 -> "Plc_1",
    1 -> "Plc_2",
    2 -> "Plc_3"
  )

  /**
   * ID -> Status name
   */
  val statuses = Map(
    0 -> "Valid",
    1 -> "Failure"
  )
}
