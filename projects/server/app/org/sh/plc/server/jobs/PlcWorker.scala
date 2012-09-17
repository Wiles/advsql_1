/**
 * FILE: PlcWorker.scala
 * PROJECT: Advanced SQL #1
 * PROGRAMMER: Hekar Khani
 * FIRST VERSION: September 11, 2012
 * DESCRIPTION:
 * 	Quartz Job that deals with polling the plc server
 */
package org.sh.plc.server.jobs

import org.quartz.JobBuilder.newJob
import org.quartz.SimpleScheduleBuilder.simpleSchedule
import org.quartz.TriggerBuilder.newTrigger
import org.quartz._
import org.sh.plc.server.communicator.PlcCommunicator
import org.sh.plc.server.services.PlcServices

/**
 * Helper to job beans
 */
object PlcWorker {

  /**
   * Run interval in seconds 
   */
  private val intervalInSeconds = 8000

  /**
   * Job id
   */
  private val identity = "plcPollJob"

  /**
   * Trigger id
   */
  private val triggerIdentity = "plcPollTrigger"
    
  /**
   * Group id
   */
  private val group = "plcPollGroup"

  /**
   * Create the job schedule for this worker
   * @return tuple containing job and trigger
   */
  def createSchedule() = {
    val job = newJob(classOf[PlcWorker]).withIdentity(identity, group).build();

    val trigger = newTrigger()
      .withIdentity(triggerIdentity, group)
      .startNow()
      .withSchedule(simpleSchedule()
        .withIntervalInSeconds(intervalInSeconds)
        .repeatForever())
      .build();

    (job, trigger)
  }
}

/**
 * Quartz job bean that handles the polling of the
 * plc server
 */
class PlcWorker extends Job {
  /**
   * Execute and process the plc polling background job
   * @param context Quartz context
   */
  def execute(context: JobExecutionContext): Unit = {
    val plcs = PlcServices.listPlcs()
    plcs.map(new PlcCommunicator().energyUsage(_))
	
  }
}