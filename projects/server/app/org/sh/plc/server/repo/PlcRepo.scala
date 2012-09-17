/**
 * FILE: PlcRepo.scala
 * PROJECT: Advanced SQL #1
 * PROGRAMMER: Hekar Khani
 * FIRST VERSION: September 11, 2012
 * DESCRIPTION:
 * 	Repository for the Plc table
 */
package org.sh.plc.server.repo

import java.sql._
import anorm._
import play.api.Play._
import play.api.db._
import org.sh.plc.server.model._

/**
 * Repository containing functions that deal on the plc event table
 */
trait PlcRepo {

  /**
   * Insert record in energy usage table
   *
   * @param plc
   * 	plc id
   * @param energyUsage
   * 	amount of energy used and timestamps
   */
  def logEnergyUsage(plc: Int, usage: EnergyUsage): Unit = {
    DB.withConnection { implicit c =>
      SQL("insert into plc_event(plc, usage, start, end) values({plc}, {usage}, {start}, {end})")
        .on("plc" -> plc)
        .on("usage" -> usage.usage)
        .on("start" -> usage.start)
        .on("end" -> usage.end)
        .executeInsert()
    }
  }
  
  def listPlcs(): Array[Int] = {
    DB.withConnection { implicit c =>
      SQL("select * from plc")
    }
  }

  //    def findConsumptionByPlc(plc: PlcModel): Unit = {
  //  
  //    }
}