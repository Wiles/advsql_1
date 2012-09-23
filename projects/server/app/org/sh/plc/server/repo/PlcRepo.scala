/**
 * FILE: PlcRepo.scala
 * PROJECT: Advanced SQL #1
 * PROGRAMMER: Hekar Khani
 * FIRST VERSION: September 11, 2012
 * DESCRIPTION:
 * Repository for the Plc table
 */
package org.sh.plc.server.repo

import java.sql.{ Timestamp }
import anorm._
import anorm.SqlParser._

import play.api.Play._
import play.api.db._
import org.sh.plc.server.model._
import org.sh.plc.server.jobs.DatabaseSetupConstants

/**
 * An enumeration for grouping styles of the timely report
 */
object ListTimelyReport extends Enumeration {
  type ListTimelyReport = Value
  val HOURLY, DAILY, MONTHLY = Value
}

/**
 * Repository containing functions that deal on the plc event table
 */
trait PlcRepo {

  /**
   * Insert record in energy usage table
   *
   * @param plc
   * plc id
   * @param usage
   * amount of energy used and timestamps
   */
  def logEnergyUsage(plc: Int, usage: EnergyUsage): Unit = {
    DB.withConnection {
      implicit c =>
        SQL("""
               insert into plc_event(plc, status, usage, start, end)
               values({plc}, {status}, {usage}, {start}, {end})""")
          .on("plc" -> plc)
          .on("status" -> DatabaseSetupConstants.statuses("Failure"))
          .on("usage" -> usage.usage)
          .on("start" -> usage.start)
          .on("end" -> usage.end)
          .executeInsert()
    }
  }

  /**
   * List all plc ids in the database
   * @return
   * plc ids
   */
  def listPlcs(): Array[Long] = {
    DB.withConnection {
      implicit c =>
        SQL("select id from plc")().map(_[Long]("id")).toArray
    }
  }

  /**
   * List the total consumption for every PLC
   * device
   * @return
   */
  def listTotalConsumption(start: Timestamp, end: Timestamp): List[EnergyUsage] = {
    DB.withConnection {
      implicit c =>
        val rows = SQL(
          """select sum(usage), plc from plc_event
            where start <= {start} and end <= {end}
          group by plc""")()

        rows.map { row =>
          new EnergyUsage(row[Long]("plc"), row[Long]("usage"), start, end)
        }.toList
    }
  }

  /**
   *
   * @param start
   * @param end
   */
  def listTimelyConsumption(style: ListTimelyReport.ListTimelyReport,
    start: Timestamp, end: Timestamp): Unit = {
    DB.withConnection {
      implicit c =>
        def query(group: String) = {
          val rows = SQL(
            """select 
        		sum(usage) as usage_sum,
        		plc,
        		hour(end) as time_hour, 
        		day_of_year(end) as time_day, 
        		month(end) as time_month, 
        		year(end) as time_year
            from plc_event
            where start <= {start} and end <= {end}
          group by %s""".format(group))()

          rows.map { row =>
            new TimelyConsumptionRow(
              row[Long]("plc"),
              row[Long]("usage"),
              row[Long]("hour"),
              row[Long]("dayOfYear"),
              row[Long]("month"),
              row[Long]("yeah"))
          }.toList
        }
        
        style match {
          case ListTimelyReport.HOURLY =>
            query("hour(end)")
          case ListTimelyReport.DAILY =>
            query("day_of_year(end)")
          case ListTimelyReport.MONTHLY =>
            query("month(end)")
          case _ =>
            query("month(end)")
        }
    }
  }

  /**
   * Get a setting from the database.
   *
   * In the case that the setting does not exist,
   * return the default value.
   *
   * @param key
   * @param defaultValue
   * @return
   */
  def getSetting(key: String, defaultValue: String): String = {
    DB.withConnection {
      implicit c =>
        val value = SQL("select value from plc_setting where key={key}")
          .on("key" -> key)
          .as(scalar[String].single)

        if (value == null) defaultValue else value
    }
  }

  /**
   * Put a setting key/value into the database.
   *
   * This performs and upsert, so there is no
   * need to check if the key already has a value
   * in the database.
   *
   * @param key
   * @param value
   */
  def putSetting(key: String, value: String): Unit = {
    DB.withConnection {
      implicit c =>
        // H2 does not have a proper merge statement,
        // so we'll need to check the number of rows
        // modified instead
        val rowsAltered = SQL(
          "update plc_setting set value={value} where key={key}").on("key" -> key,
            "value" -> value).executeUpdate()

        if (rowsAltered <= 0) {
          // Perform the insert
          val rowsAltered = SQL(
            "insert into plc_setting(key, value) values({key}, {value})").on("key" -> key,
              "value" -> value).executeInsert()
        }
    }
  }
}