/**
 * File: Global.scala
 * Author(s): Hekar Khani
 * Date: Sep 11, 2012
 * Description:
 * 	Global Play 2.0 configurations
 */
package org.sh.plc.server.jobs

import play.api._


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
  }
  
  override def onStop(app: Application) {
    JobScheduler.onStop(app)
  }
}
