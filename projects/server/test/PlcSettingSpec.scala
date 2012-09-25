import org.sh.plc.server.jobs._
import org.sh.plc.server.services._
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
class PlcSettingSpec extends Specification with Before with PlcServiceComponent {
  def before() = {
    running(FakeApplication()) {
      DatabaseSetup.setup()
    }
  }

  "settings" should {
    "be saved" in {
      running(FakeApplication()) {
        plcService.putSetting("Bob", "Bill")
        val setting = plcService.getSetting("Bob", "Joe")

        assert(setting != "Joe")
        assert(setting == "Bill")
      }
      true
    }
  }
}
