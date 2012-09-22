import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import org.sh.plc.server.jobs.DatabaseSetup

class DatabaseSetupSpec extends Specification {

  "" should {
    "" in {
      running(FakeApplication()) {
        val table = "bob"
        val info = "fasdfadsf"
        val sql = DatabaseSetup
          .createNonExistantTableSql(Map(table -> info))

          println(sql)
          true
      }
    }
  }
}
