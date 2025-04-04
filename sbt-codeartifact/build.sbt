enablePlugins(SbtPlugin)

name := "sbt-codeartifact"

// Unfortunately, there's an issue where the Scala compiler is
// erroneously warning on valid SBT syntax.
scalacOptions -= "-Xfatal-warnings"

libraryDependencies ++= Seq(
  "software.amazon.awssdk" % "sso" % "2.21.37",
  "software.amazon.awssdk" % "codeartifact" % "2.21.37",
  "software.amazon.awssdk" % "sts" % "2.21.37",
  "org.scala-sbt" % "sbt" % "1.9.7",
  "com.lihaoyi" %% "requests" % "0.8.0",
  "com.lihaoyi" %% "os-lib" % "0.9.2",
  "com.lihaoyi" %% "utest" % "0.8.2" % Test
)

testFrameworks += new TestFramework("utest.runner.Framework")

//pluginCrossBuild / sbtVersion := {
//  scalaBinaryVersion.value match {
//    case "2.12" => "1.2.8" // set minimum sbt version
//  }
//}

test := {
  (Test / test).value
//  scripted.toTask("").value
}
