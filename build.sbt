scalaVersion := "2.11.7"

name := "dsug-reactive-streams"

version := "0.0.1"

import scalariform.formatter.preferences._
import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform.ScalariformKeys

SbtScalariform.scalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value // Scala formatting rules
      .setPreference(AlignSingleLineCaseStatements, true)
      .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 60)
      .setPreference(DoubleIndentClassDeclaration, true)
      .setPreference(CompactControlReadability, true)
      .setPreference(SpacesAroundMultiImports, true) //



libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-stream" % "2.4.4",
    "org.scalatest" %% "scalatest" % "2.2.5" % "test" 
    
)

lazy val root = (project in file("."))
  .settings ( 
    testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oD")    
   )
