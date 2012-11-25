import sbt._
import Process._
import Keys._

resolvers += "typesafe" at "http://repo.typesafe.com/typesafe/releases"

libraryDependencies += "play" %% "anorm" % "2.+"
