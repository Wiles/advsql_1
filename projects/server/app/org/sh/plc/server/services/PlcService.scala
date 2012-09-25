/**
 * PROJECT: Advanced SQL #1
 * PROGRAMMER: Hekar Khani
 * FIRST VERSION: September 11, 2012
 * DESCRIPTION:
 * 	Container for the plc services
 */
package org.sh.plc.server.services

import org.sh.plc.server.repo._

/**
 * Services for dealing with plc information
 */
private object PlcService extends PlcRepo

/**
 * Trait for dependency injection
 */
trait PlcServiceComponent {
	val plcService: PlcRepo = PlcService
}