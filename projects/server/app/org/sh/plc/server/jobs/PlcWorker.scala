/**
 * FILE: PlcWorker.scala
 * PROJECT: Advanced SQL #1
 * PROGRAMMER: Hekar Khani
 * FIRST VERSION: September 11, 2012
 * DESCRIPTION:
 * 	Quartz Job that deals with polling the plc server
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
object PlcWorker {

  /**
   * Run interval in seconds 
   */
  private val IntervalInSeconds = 10

  /**
   * Job id
   */
  private val Identity = "plcPollJob"

  /**
   * Trigger id
   */
  private val TriggerIdentity = "plcPollTrigger"
    
  /**
   * Group id
   */
  private val Group = "plcPollGroup"

  /**
   * Create the job schedule for this worker
   * @return tuple containing job and trigger
   */
  def createSchedule() = {
    val job = newJob(classOf[PlcWorker]).withIdentity(Identity, Group).build();

    val trigger = newTrigger()
      .withIdentity(TriggerIdentity, Group)
      .startNow()
      .withSchedule(simpleSchedule()
        .withIntervalInSeconds(IntervalInSeconds)
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
    try {
	    val plcs = PlcServices.listPlcs()
	    plcs.map { plc =>
	    	new PlcCommunicator().energyUsage(plc.id)
		}
    } catch {
      case e: Exception =>
      	e.printStackTrace()
    }
	
  }
}