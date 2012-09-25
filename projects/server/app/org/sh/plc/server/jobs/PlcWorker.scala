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
import play.api.Play._

import org.quartz._
import org.quartz.JobBuilder.newJob
import org.quartz.SimpleScheduleBuilder.simpleSchedule
import org.quartz.TriggerBuilder.newTrigger
import org.sh.plc.server.communicator.PlcCommunicator
import org.sh.plc.server.services.PlcServiceComponent
import java.net.SocketException

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
   * First run of the PLC Worker
   */
  var firstRun = true
  
  /**
   * Last result of polling the PLC controller
   */
  var lastPollSuccessful = false

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
class PlcWorker extends Job with PlcServiceComponent {

  import PlcWorker._
  
  val DefaultHost = "127.0.0.1"
  val DefaultPort = 9999

  val configuration = Play.application.configuration

  val address = configuration.getString("oplc_server_address").getOrElse({
    // TODO: Fix continual warning that will annoy users/admins...
    Logger.warn("Failure to find oplc_server_address configured in Play Application. Using %s".format(DefaultHost))
    DefaultHost
  })

  val port = configuration.getInt("oplc_server_port").getOrElse({
    // TODO: Fix continual warning that will annoy users/admins...
    Logger.warn("Failure to find oplc_server_port configured in Play Application. Using %d".format(DefaultPort))
    DefaultPort
  })

  val communicator = new PlcCommunicator(address, port)

  def connectionMessage(success: Boolean, lastSuccess: Boolean, errorMessage: String = "") = {
    val message = if (success) {
      "Successfully Connected to PLC server %s:%d".format(address, port)
    } else {
      "Failure to connect to PLC server %s:%d".format(address, port)
    }

    if (firstRun || lastSuccess != success) {
      Logger.info(message)
      if (errorMessage != null && !errorMessage.isEmpty) {
        Logger.error(errorMessage)
      }
    }

    firstRun = false
  }

  /**
   * Execute and process the plc polling background job
   * @param context Quartz context
   */
  def execute(context: JobExecutionContext): Unit = {
    try {

      val plcs = plcService.listPlcs()
      for (plc <- plcs) {
        communicator.pollAndUpdateDatabase(plc.id)
      }

      connectionMessage(true, lastPollSuccessful)
      lastPollSuccessful = true
    } catch {
      case e: SocketException =>
        connectionMessage(false, lastPollSuccessful, e.getMessage())
        lastPollSuccessful = false
      case e: Exception =>
        e.printStackTrace()
    }

  }
}