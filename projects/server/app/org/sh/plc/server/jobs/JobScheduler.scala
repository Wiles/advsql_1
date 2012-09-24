/**
 * File: JobScheduler.scala
 * Author(s): Hekar Khani
 * Date: Sep 11, 2012
 * Description:
 * 	Logic for registering jobs
 */
package org.sh.plc.server.jobs

import org.quartz._
import org.quartz.impl._
import play.api._

/**
 * Register jobs for scheduling
 */
object JobScheduler {
  val scheduler = StdSchedulerFactory.getDefaultScheduler()

  def registerSchedules(): Unit = {

    val schedules = List(
      PlcWorker.createSchedule())

    schedules.foreach { tuple: (JobDetail, SimpleTrigger) =>
      scheduler.scheduleJob(tuple._1, tuple._2)
    }
  }

  def onStart(app: Application) {
    Logger.info("Quartz scheduler starting...")
    Logger.info("Registering Jobs...")
    registerSchedules()

    /*
     * Start the Quartz Scheduler
     */
    val thread = new Thread(
      new Runnable() {
        def run(): Unit = {
          Logger.info("Starting Jobs...")
          scheduler.start()
        }
      }
    )

    thread.start()

  }

  def onStop(app: Application) {
    Logger.info("Quartz scheduler shutdown.")
    scheduler.shutdown()
  }

}