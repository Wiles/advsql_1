/**
 * File: Global.scala
 * Author(s): Hekar Khani
 * Date: Sep 11, 2012
 * Description:
 * 	Global Play 2.0 configurations
 */
package org.sh.plc.server.jobs

import scala.Array.canBuildFrom
import scala.annotation.implicitNotFound

import org.sh.plc.server.communicator.PlcCommunicator
import org.sh.plc.server.services.PlcServices

import akka.util.duration.intToDurationInt
import play.api.Application
import play.api.GlobalSettings
import play.api.Play.current
import play.api.libs.concurrent.Akka


/**
 * Do not move this file or change its package without updating
 * application.conf
 * 
 * 
 * This defines the global settings for the play application and
 * listens on certain events. The events are then sent throughout
 * the application.
 */
object Global extends GlobalSettings {

  override def onStart(app: Application) {
    JobScheduler.onStart(app)
    DatabaseSetup.onStart(app)
  }
  
  override def onStop(app: Application) {
    JobScheduler.onStop(app)
  }
}

