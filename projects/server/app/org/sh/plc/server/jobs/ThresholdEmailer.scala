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

import org.quartz._
import org.quartz.JobBuilder.newJob
import org.quartz.SimpleScheduleBuilder.simpleSchedule
import org.quartz.TriggerBuilder.newTrigger
import org.sh.plc.server.communicator.PlcCommunicator
import org.sh.plc.server.services.PlcServices


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
class ThresholdEmailer extends Job {
  /**
   * Execute and process the plc polling background job
   * @param context Quartz context
   */
  def execute(context: JobExecutionContext): Unit = {
    val plcs = PlcServices.listPlcs()
    
  }
}