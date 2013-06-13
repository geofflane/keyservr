import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "keyservr"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    "org.bouncycastle" % "bcpg-jdk15on" % "1.49",
    jdbc,
    anorm
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
