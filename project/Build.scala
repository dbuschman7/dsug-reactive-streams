import sbt._
import Keys._
import sbt.Keys._
import java.io.PrintWriter
import java.io.File
import play.Play.autoImport._
//import sys.process.stringSeqToProcess

object ApplicationBuild extends Build {

  scalaVersion := "2.11.7"

  val appName = "dsug-reactive-streams"

  val branch = "git rev-parse --abbrev-ref HEAD".!!.trim
  val commit = "git rev-parse --short HEAD".!!.trim
  val buildTime = (new java.text.SimpleDateFormat("yyyyMMdd-HHmmss")).format(new java.util.Date())

  val major = 0
  val minor = 1
  val patch = 0
  val appVersion = s"$major.$minor.$patch-$commit"

  println()
  println(s"App Name      => ${appName}")
  println(s"App Version   => ${appVersion}")
  println(s"Git Branch    => ${branch}")
  println(s"Git Commit    => ${commit}")
  println(s"Scala Version => 2.11.7")
  println()

  val scalaBuildOptions = Seq("-unchecked", "-feature", "-language:reflectiveCalls", "-deprecation",
    "-language:implicitConversions", "-language:postfixOps", "-language:dynamics", "-language:higherKinds",
    "-language:existentials", "-language:experimental.macros", "-Xmax-classfile-name", "140")

  implicit def dependencyFilterer(deps: Seq[ModuleID]) = new Object {
    def excluding(group: String, artifactId: String) =
      deps.map(_.exclude(group, artifactId))
  }

  val appDependencies = Seq(ws,

    // GUI
    "org.webjars" %% "webjars-play" % "2.4.0-1",
    "org.webjars" % "angularjs" % "1.4.3",
    "org.webjars" % "bootstrap" % "3.2.0",
//    "org.webjars" % "angular-ui-bootstrap" % "0.12.0",
    "org.webjars" % "d3js" % "3.5.3",

    // Reactive
    "com.typesafe.akka" % "akka-stream-experimental_2.11" % "1.0",
    "com.typesafe.akka" %% "akka-contrib" % "2.4-M2",
    "nl.grons" %% "metrics-scala" % "3.5.1_a2.3",
    "com.beachape" %% "enumeratum" % "1.2.2",
    "com.beachape" %% "enumeratum-play" % "1.2.2",

    // Testing
    "org.scalatestplus" %% "play" % "1.1.0" % "test",
    "org.scalatest" %% "scalatest" % "2.2.1" % "test",
    "com.typesafe.akka" %% "akka-testkit" % "2.3.12" % "test" //
    )

  val exclusions =
    <dependencies>
      <exclude org="javax.jms" module="jms"/>
      <exclude org="com.sun.jdmk" module="jmxtools"/>
      <exclude org="com.sun.jmx" module="jmxri"/>
      <exclude module="slf4j-jdk14"/>
      <exclude module="slf4j-log4j"/>
      <exclude module="slf4j-log4j12"/>
      <exclude module="slf4j-simple"/>
      <exclude module="cglib-nodep"/>
    </dependencies>

  val root = Project(appName, file("."))
    .enablePlugins(play.PlayScala)
    .settings(scalacOptions ++= scalaBuildOptions, ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) })
    .settings(
      version := appVersion,
      autoScalaLibrary := false,
      libraryDependencies ++= appDependencies,
      ivyXML := exclusions //  
      )

  println("Done")

}

