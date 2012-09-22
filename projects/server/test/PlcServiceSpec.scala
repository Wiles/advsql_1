import org.sh.plc.server.jobs.DatabaseSetup
import org.sh.plc.server.services.PlcServices
import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._

class PlcServiceSpec extends Specification {

  "" should {
    "" in {
      running(FakeApplication()) {
        DatabaseSetup.setupTables()

        val plcs = PlcServices.listPlcs()


      }
    }
  }

}
