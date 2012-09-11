/**
 * File: PlcWorker.scala
 * Author(s): Hekar Khani
 * Date: Sep 11, 2012
 * Description:
 * 	Quartz Job that deals with polling the Plc
 */
package org.sh.plc.server.jobs

import org.quartz._
import org.quartz.JobBuilder.newJob
import org.quartz.TriggerBuilder.newTrigger
import org.quartz.SimpleScheduleBuilder.simpleSchedule

object PlcWorker {
  
  private val intervalInSeconds = 8000
  private val identity = "job1"
  private val triggerIdentity = "trigger1"
  private val group = "group1"

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

class PlcWorker extends Job {
  def execute(context: JobExecutionContext): Unit = {

  }
}