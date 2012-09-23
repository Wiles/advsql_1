/**
 * FILE: DatabaseSetup.scala
 * PROJECT: Advanced SQL #1
 * PROGRAMMER: Hekar Khani
 * FIRST VERSION: September 13, 2012
 * DESCRIPTION:
 * Functions dealing with the automatic initialization of the
 * database schema
 */
package org.sh.plc.server.jobs

import anorm._
import anorm.SqlParser._
import play.api._
import play.api.Play._
import play.api.db._
import DatabaseSetupConstants._

import scala.collection.immutable.Map

object DatabaseSetup {
  def onStart(app: Application): Unit = {
    setup()
  }

  def setup(): Unit = {
    setupTables()
    setupDemoData()
  }

  def setupTables(): Unit = {
    val sql = createNonExistantTableSql(tables)
    DB.withConnection {
      implicit c =>
        SQL(sql).execute()
    }
  }

  def createNonExistantTableSql(tables: Map[String, String]): String = {
    val template = "create table if not exists %s (%s);"

    DB.withConnection {
      implicit c =>
      // Find out which tables exist and which of them do not
      // Create the DDL for creating the tables that do not exist
        val ddl = tables
          .map {
          case (key, value) =>
            template.format(key, value)
        }.mkString("", "\n", "\n")

        ddl
    }
  }

  def setupDemoData(): Unit = {
    def setup(items: Map[String, Int], insertTemplate: String, countStatement: String): Unit = {
      DB.withConnection {
        implicit c =>
          val ddl = items
            .withFilter {
            case (key, value) =>
              val count = SQL(countStatement)
                .on(
                "id" -> value
              ).as(scalar[Long].single)

              count <= 0
          }.map {
            case (key, value) => {
              // SQL injection right here...
              insertTemplate.format(value, key)
              }
          }.mkString("")

          SQL(ddl).execute()
      }
    }

    setup(plcs, "insert into plc(id, name) values(%d, '%s');",
      "select count(*) as c from plc where id={id}")

    setup(statuses, "insert into plc_status(id, name) values(%d, '%s');",
      "select count(*) as c from plc_status where id={id}")
  }
}