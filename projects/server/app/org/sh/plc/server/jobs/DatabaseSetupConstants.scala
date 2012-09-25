/**
 * FILE: DatabaseSetupSpec.scala
 * PROJECT: Advanced SQL #1
 * PROGRAMMER: Hekar Khani
 * FIRST VERSION: September 13, 2012
 * DESCRIPTION:
 * Constants dealing with the automatic initialization of the
 * database schema
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
      usage long not null,
	  created timestamp not null
      	""",

    "plc_setting" ->
      """
	  key varchar(64) primary key,
	  value varchar(256)
      	"""
  )

  /**
   * PLC name -> ID
   */
  val plcs = Map(
    "Plc_1" -> 1,
    "Plc_2" -> 2,
    "Plc_3" -> 3
  )

  /**
   * Status name -> ID
   */
  val statuses = Map(
    "Valid" -> 0,
    "Failure" -> 1
  )
}
