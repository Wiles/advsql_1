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
import scala.collection.mutable.MutableList

/**
 * Register jobs for scheduling
 */
object JobScheduler {
  var scheduler: Scheduler = _
  // TODO: Threadpool
  val threads = new MutableList[Thread]

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
    
    scheduler = StdSchedulerFactory.getDefaultScheduler()
    
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
    threads += thread
  }

  def onStop(app: Application) {
    Logger.info("Quartz scheduler shutdown.")
    
    scheduler.shutdown()
    
    for(thread <- threads) {
      if (thread.isAlive()) {
    	  thread.join()
      }
    }
  }

}