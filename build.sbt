import sbt._
import Process._
import Keys._

resolvers += "typesafe" at "http://repo.typesafe.com/typesafe/releases"


libraryDependencies ++= Seq(
  "play" %% "anorm" % "2.+",
  "play" %% "play" % "2.+",
  "net.sourceforge.jtds" % "jtds" % "1.+",
  "com.github.seratch" %% "scalikejdbc" % "[0.5,)"
)
