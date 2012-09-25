/**
 * FILE: PlcServiceSpec.scala
 * PROJECT: Advanced SQL #1
 * PROGRAMMER: Hekar Khani
 * FIRST VERSION: September 11, 2012
 * DESCRIPTION:
 *
 */

import java.util.{ Calendar, GregorianCalendar }
import java.sql.Timestamp
import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import org.sh.plc.server.jobs._
import org.sh.plc.server.model._
import org.sh.plc.server.services._
import org.sh.plc.server.repo._

class PlcServiceSpec extends Specification with Before with PlcServiceComponent {

  def before() = {
    running(FakeApplication()) {
      DatabaseSetup.setup()
    }
  }

  "plcs" should {
    "be listed" in {
      running(FakeApplication()) {
        val plcs = plcService.listPlcs()

        assert(plcs.size == 3)
        assert(plcs(0) == 1)
        assert(plcs(1) == 2)
        assert(plcs(2) == 3)

        true
      }
    }

    "logged" in {
      running(FakeApplication()) {
        val start = new GregorianCalendar()
        start.add(Calendar.HOUR, -5)

        val usage = new EnergyUsage(1, 50,
          new Timestamp(start.getTime().getTime()),
          new Timestamp(Calendar.getInstance().getTime().getTime()))

        PlcServices.logEnergyUsage(usage)

        true
      }
    }
    
    "be reported totally" in {
      running(FakeApplication()) {
        val start = new GregorianCalendar()
        start.add(Calendar.HOUR, -5)

        val items = plcService.listTimelyConsumption(ListTimelyReport.TOTAL,
          new Timestamp(start.getTime().getTime()),
          new Timestamp(Calendar.getInstance().getTime().getTime()))

        println(items)
        true
      }      
    }

    "be reported daily" in {
      running(FakeApplication()) {
        val start = new GregorianCalendar()
        start.add(Calendar.HOUR, -5)

        val items = plcService.listTimelyConsumption(ListTimelyReport.DAILY,
          new Timestamp(start.getTime().getTime()),
          new Timestamp(Calendar.getInstance().getTime().getTime()))

        println(items)
        true
      }
    }

    "be reported monthly" in {
      running(FakeApplication()) {
        val start = new GregorianCalendar()
        start.add(Calendar.HOUR, -5)

        val items = plcService.listTimelyConsumption(ListTimelyReport.MONTHLY,
          new Timestamp(start.getTime().getTime()),
          new Timestamp(Calendar.getInstance().getTime().getTime()))

        println(items)
        true
      }
    }

    "be reported hourly" in {
      running(FakeApplication()) {
        val start = new GregorianCalendar()
        start.add(Calendar.HOUR, -5)

        val items = plcService.listTimelyConsumption(ListTimelyReport.HOURLY,
          new Timestamp(start.getTime().getTime()),
          new Timestamp(Calendar.getInstance().getTime().getTime()))

        println(items)
        true
      }
    }
  }
}
