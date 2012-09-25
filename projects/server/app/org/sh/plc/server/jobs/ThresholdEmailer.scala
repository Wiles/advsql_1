/**
 * FILE: ThresholdEmailer.scala
 * PROJECT: Advanced SQL #1
 * PROGRAMMER: Hekar Khani
 * FIRST VERSION: September 16, 2012
 * DESCRIPTION:
 * 	Quartz Job that deals with checking PLC thresholds
 */
package org.sh.plc.server.jobs

import play.api._
import play.api.Play._

import org.quartz._
import org.quartz.JobBuilder.newJob
import org.quartz.SimpleScheduleBuilder.simpleSchedule
import org.quartz.TriggerBuilder.newTrigger

import org.sh.plc.server.model._
import org.sh.plc.server.services._

import scala.util.control.Exception._

/**
 * Helper for job bean
 */
object ThresholdEmailer {
  "59 * * * * *"

  /**
   * Run interval in seconds
   */
  private val IntervalInHours = 1

  /**
   * Job id
   */
  private val Identity = "thresholdPollJob"

  /**
   * Trigger id
   */
  private val TriggerIdentity = "thresholdPollTrigger"

  /**
   * Group id
   */
  private val Group = "thresholdPollGroup"

  /**
   * Create the job schedule for this worker
   * @return tuple containing job and trigger
   */
  def createSchedule() = {
    val job = newJob(classOf[ThresholdEmailer]).withIdentity(Identity, Group).build();

    val trigger = newTrigger()
      .withIdentity(TriggerIdentity, Group)
      .startNow()
      .withSchedule(simpleSchedule()
        .withIntervalInHours(IntervalInHours)
        .repeatForever())
      .build();

    (job, trigger)
  }
}

/**
 * Quartz job bean that handles the polling of the
 * database for
 */
class ThresholdEmailer extends Job with PlcServiceComponent with EmailServiceComponent {

  val DefaultFrom = "hkhani-cc@conestogac.on.ca"

  /**
   * Execute and process the plc polling background job
   * @param context Quartz context
   */
  def execute(context: JobExecutionContext): Unit = {
    
    val threshold = catching(classOf[NumberFormatException]) opt
    		plcService.getSetting(SettingModel.Key.hourlyEnergyThreshold, "0").toLong

    val configuration = Play.application.configuration
    val from = configuration.getString("smtp.user").getOrElse({
      // TODO: Fix continual warning that will annoy users/admins...
      Logger.warn("Failure to find smtp.user configured in Play Application. Using %s".format(DefaultFrom))
      DefaultFrom
    })

    val to = plcService.getSetting(SettingModel.Key.email, "")
    if (to.isEmpty()) {
      Logger.error("No email address for Hourly Threshold limit configured. Aborting...")
      return
    }

    val subject = "Hourly Threshold Limit Reached for PLC #%d - %s"
    val body = """The hourly threshold limit reached for PLC #%d - %s"""

    // Send emails for invalid
    threshold match {
      case Some(i) =>
        val plcs = plcService.listPlcs()
        plcs.map { plc =>
          (plc, plcService.getConsumptionInLastHour(plc.id))
        }.withFilter { t: (PlcModel, Long) =>
          t._2 > i
        }.map { t: (PlcModel, Long) =>
          val plc = t._1
          // Send out the emails
          val email = Email(
            from,
            List(to),
            subject.format(plc.id, plc.name),
            body.format(plc.id, plc.name))

          emailService.send(email)
        }
      case None =>
        Logger.error("Invalid Hourly Threshold Limit value. Failure to check for overconsumption. Aborting...")
    }

  }
}