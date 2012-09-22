/**
 * FILE: PlcServiceSpec.scala
 * PROJECT: Advanced SQL #1
 * PROGRAMMER: Hekar Khani
 * FIRST VERSION: September 11, 2012
 * DESCRIPTION:
 *
 */

import java.util.{Calendar, GregorianCalendar}
import java.sql.Timestamp
import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import org.sh.plc.server.jobs.DatabaseSetup
import org.sh.plc.server.model.EnergyUsage
import org.sh.plc.server.services.PlcServices

class PlcServiceSpec extends Specification with Before {

  def before() = {
    running(FakeApplication()) {
      DatabaseSetup.setup()
    }
  }

  "plcs" should {
    "be listed" in {
      running(FakeApplication()) {
        val plcs = PlcServices.listPlcs()

        assert(plcs.size == 3)
        assert(plcs(0) == 0)
        assert(plcs(1) == 1)
        assert(plcs(2) == 2)

        true
      }
    }

      "logged" in {
        running(FakeApplication()) {
          val start = new GregorianCalendar()
          start.add(Calendar.HOUR, -5)

          val usage = new EnergyUsage(50,
            new Timestamp(start.getTime().getTime()),
            new Timestamp(Calendar.getInstance().getTime().getTime())
          )

          PlcServices.logEnergyUsage(0, usage)

          true
        }
      }
    }
}
