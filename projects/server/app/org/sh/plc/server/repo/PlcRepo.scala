/**
 * FILE: PlcRepo.scala
 * PROJECT: Advanced SQL #1
 * PROGRAMMER: Hekar Khani
 * FIRST VERSION: September 11, 2012
 * DESCRIPTION:
 * Repository for the Plc table
 */
package org.sh.plc.server.repo

import java.sql.Timestamp
import java.util.Date
import java.math.BigDecimal
import anorm._
import anorm.SqlParser._
import play.api.Play._
import play.api.db._
import org.sh.plc.server.model._
import org.sh.plc.server.jobs.DatabaseSetupConstants
import java.util.LinkedHashMap

/**
 * An enumeration for grouping styles of the timely report
 */
object ListTimelyReport extends Enumeration {
  type ListTimelyReport = Value
  val TOTAL, HOURLY, DAILY, MONTHLY = Value
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
  def logEnergyUsage(usage: EnergyUsage): Unit = {
    DB.withConnection {
      implicit c =>
        SQL("""
               insert into plc_event(plc, status, usage, end)
               values({plc}, {status}, {usage}, CURRENT_TIMESTAMP())""")
          .on("plc" -> usage.plc)
          .on("status" -> DatabaseSetupConstants.statuses("Valid"))
          .on("usage" -> usage.usage)
          .executeInsert()
    }
  }
  
  /**
   * Log a failure event
   */
  def logFailureEvent(plcId: Long, end: Timestamp): Unit = {
    DB.withConnection {
      implicit c =>
        SQL("""
               insert into plc_event(plc, status, usage, created)
               values({plc}, {status}, {usage}, CURRENT_TIMESTAMP())""")
          .on("plc" -> plcId)
          .on("status" -> DatabaseSetupConstants.statuses("Failure"))
          .on("usage" -> 0)
          .executeInsert()
    }
  }

  /**
   * List all plcs in the database
   * @return
   * plc ids
   */
  def listPlcs(): Array[PlcModel] = {
    DB.withConnection {
      implicit c =>
        SQL("select id, name  from plc")().map { row =>
          new PlcModel(row[Long]("id"), row[String]("name"))
        }.toArray
    }
  }

  /**
   * List statuses
   */
  def listPlcStatuses(): Array[PlcStatusModel] = {
    DB.withConnection {
      implicit c =>
        SQL("""
  				select 
        			max(pe.created) as max_created,
  					plc.id as id, 
  					plc.name as name,
  					ps.name as status,
        			FORMATDATETIME(pe.created, 'd/MM/yyyy HH:mm') as created,
  					pe.usage as usage,
  					ptotal.total as total
  				from plc
  					inner join plc_event pe on pe.plc=plc.id
  					inner join plc_status ps on ps.id=pe.status
  					left join (select plc, sum(usage) as total from plc_event group by plc) ptotal 
  						on  ptotal.plc = plc.id
        		group by
        			plc.id,
        			plc.name,
        			ps.name,
        			pe.usage, 
        			pe.created,
        			ptotal.total
        		limit 50
  			""")().map { row =>
          new PlcStatusModel(
            row[Long]("id"),
            row[String]("name"),
            row[String]("status"),
            row[String]("created"),
            row[Long]("usage"),
            row[BigDecimal]("total"))
        }.toArray
    }
  }

  /**
   *
   * @param start
   * @param end
   */
  def listTimelyConsumption(style: ListTimelyReport.ListTimelyReport,
    start: Timestamp, end: Timestamp): List[LinkedHashMap[String, Any]] = {
    DB.withConnection {
      implicit c =>
        def total() = {
          val rows = SQL(
            """
              select 
        		  sum(usage) as usage_sum,
        		  plc.id as plc_id,
        		  plc.name as plc_name
              from plc_event
              inner join plc on plc.id=plc_event.plc
    		  where 
        		  created >= {start}
              and
        		  created <= {end}
    		  group by 
        		  plc.id, plc.name""")
    	.on("start" -> start, "end" -> end)()

          rows.map { row =>
            val map = new LinkedHashMap[String, Any]()
            map.put("PLC #", row[Long]("plc_id"))
            map.put("PLC Name", row[String]("plc_name"))
            map.put("Total Usage", row[BigDecimal]("usage_sum").longValue)
            map
          }.toList
        }

        def hourly() = {
          val rows = SQL(
            """select 
        		sum(usage) as usage_sum,
        		plc.id as plc_id,
                plc.name as plc_name,
        		hour(created) as time_hour, 
        		day_of_month(created) as time_day_of_month, 
        		day_of_year(created) as time_day, 
        		month(created) as time_month, 
        		year(created) as time_year
            from plc_event
            inner join plc on plc.id=plc_event.plc
            where created >= {start} and created <= {end}
        	group by
              plc.id,
    		  plc.name,
              hour(created),
        	  day_of_month(created),
              day_of_year(created),
              month(created),
              year(created)
        	order by 
    		  year(created) desc, 
    		  month(created) desc,
    		  day_of_year(created) desc,
    		  hour(created) desc
              """)
            .on("start" -> start, "end" -> end)()

          rows.map { row =>
              val map = new LinkedHashMap[String, Any]()
	          map.put("PLC #", row[Long]("plc_id"))
	          map.put("PLC Name", row[String]("plc_name"))
	          map.put("Usage", row[BigDecimal]("usage_sum").longValue)
	          map.put("Hour", row[Long]("time_hour"))
	          map.put("Day", row[Long]("time_day_of_month"))
	          map.put("Month", row[Long]("time_month"))
	          map.put("Year", row[Long]("time_year"))
	          map
          }.toList
        }

        def daily() = {
          println("start", start)
          println("end", end)
          val rows = SQL(
            """select 
        		sum(usage) as usage_sum,
        		plc.id as plc_id,
        		plc.name as plc_name,
        		hour(created) as time_hour, 
        		day_of_year(created) as time_day,
        		day_of_month(created) as time_day_of_month,
        		month(created) as time_month, 
        		year(created) as time_year
            from plc_event
            inner join plc on plc.id=plc_event.plc
            where created >= {start} and created <= {end}
        	group by
              plc.id,
    		  plc.name,
              hour(created),
              day_of_year(created),
              day_of_month(created),
              month(created),
              year(created)
        	order by
    		  year(created) desc,
    		  month(created) desc,
    		  day_of_year(created) desc,
    		  hour(created) desc
              """)
            .on("start" -> start, "end" -> end)()

          rows.map { row =>
            val map = new LinkedHashMap[String, Any]()
              map.put("PLC #", row[Long]("plc_id"))
              map.put("PLC Name", row[String]("plc_name"))
              map.put("Usage", row[BigDecimal]("usage_sum").longValue)
              map.put("Day", row[Long]("time_day_of_month"))
              map.put("Month", row[Long]("time_month"))
              map.put("Year", row[Long]("time_year"))
              map
          }.toList
        }

        def monthly() = {
          val rows = SQL(
            """select 
        		sum(usage) as usage_sum,
        		plc.id as plc_id,
        		plc.name as plc_name,
        		month(created) as time_month, 
        		year(created) as time_year
            from plc_event
        	inner join plc on plc.id=plc_event.plc
            where created >= {start} and created <= {end}
        	group by 
              plc.id,
        	  plc.name,
              month(created),
              year(created)
        	order by
    		  year(created),
    		  month(created)
              """)
            .on("start" -> start, "end" -> end)()

          rows.map { row =>
              val map = new LinkedHashMap[String, Any]()
              map.put("PLC #", row[Long]("plc_id"))
              map.put("PLC Name", row[String]("plc_name"))
              map.put("Usage", row[BigDecimal]("usage_sum").longValue)
              map.put("Month", row[Long]("time_month"))
              map.put("Year", row[Long]("time_year"))
              map
          }.toList
        }

        style match {
          case ListTimelyReport.TOTAL =>
            total
          case ListTimelyReport.HOURLY =>
            hourly
          case ListTimelyReport.DAILY =>
            daily
          case ListTimelyReport.MONTHLY =>
            monthly
          case _ =>
            monthly
        }
    }
  }

  /**
   * @return
   * sum of usage in last hour of PLC's usage events
   */
  def getConsumptionInLastHour(plcId: Long): Long = {
    DB.withConnection {
      implicit c =>
        SQL("""
      	    select
      			sum(pe.usage) as total
      	    from plc_event pe
        		inner join plc on plc.id=pe.plc
      	    where
	      			created >= dateadd(hour, -1, current_timestamp())
	  			and
	      			plc.id = {plcId}
     	""")
          .on("plcId" -> plcId)
          .as(scalar[Long].single)
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
        try {
        	val value = SQL("select value from plc_setting where key={key}")
    			.on("key" -> key)
    			.as(scalar[String].single)
          
			if (value == null) defaultValue else value
        } catch {
          case e: SqlMappingError =>
            // In the case of the setting not existing in the DB
            defaultValue
          case e: Exception =>
            e.printStackTrace()
            defaultValue
        }
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