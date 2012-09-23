import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "server"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
        "org.quartz-scheduler" % "quartz" % "2.1.6",
        "joda-time" % "joda-time" % "2.1"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here      
    )

}
