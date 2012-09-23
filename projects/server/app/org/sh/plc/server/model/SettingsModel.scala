/**
 * FILE: SettingsForm.scala
 * PROJECT: Advanced SQL #1
 * PROGRAMMER: Hekar Khani
 * FIRST VERSION: September 14, 2012
 * DESCRIPTION:
 *
 */
package org.sh.plc.server.model

object SettingModel {
  /**
   * Database setting keys
   */
  object Key {
    val email = "main_setting_hourly_threshold_email"
    val hourlyEnergyThreshold = "main_setting_hourly_threshold_value"
  }
  object Default {
    val hourlyEnergyThreshold = 100000
  }
}

case class SettingModel(
  email: String,
  hourlyEnergyThreshold: Int
)
