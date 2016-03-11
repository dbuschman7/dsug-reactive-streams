scalaVersion := "2.11.7"

name := "dsug-reactive-streams"

version := "0.0.1"

libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-stream" % "2.4.2",
    "org.scalatest" %% "scalatest" % "2.2.5" % "test" 
    
)

lazy val root = (project in file("."))
  .settings ( 
    testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oD")
   )
