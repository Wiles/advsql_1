import org.sh.plc.server.jobs.DatabaseSetup
import org.sh.plc.server.services.PlcServices
import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._

/**
 * FILE: PlcSettingSpec.scala
 * PROJECT: Advanced SQL #1
 * PROGRAMMER: Hekar Khani
 * FIRST VERSION: September 11, 2012
 * DESCRIPTION:
 *
 */
class PlcSettingSpec extends Specification with Before {
  def before() = {
    running(FakeApplication()) {
      DatabaseSetup.setup()
    }
  }

  "settings" should {
    "be saved" in {
      running(FakeApplication()) {
        PlcServices.putSetting("Bob", "Bill")
        val setting = PlcServices.getSetting("Bob", "Joe")

        assert(setting != "Joe")
        assert(setting == "Bill")
      }
      true
    }
  }
}
