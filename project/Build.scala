import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "keyservr"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    "org.bouncycastle" % "bcpg-jdk15on" % "1.49",
    "com.github.nscala-time" %% "nscala-time" % "0.4.2",
    "org.postgresql" % "postgresql" % "9.2-1003-jdbc4",
    jdbc,
    anorm
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
