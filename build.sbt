inThisBuild(
  List(
    organization := "io.github.swoogles",
    homepage := Some(url("https://github.com/swoogles/sbt-codeartifact")),
    licenses := Seq("MIT" -> url("https://choosealicense.com/licenses/mit/")),
    developers := List(
      Developer(
        "swoogles",
        "Bill Frasure",
        "billfrasure@gmail.com",
        url("https://github.com/swoogles")
      )
    ),
  )
)

import xerial.sbt.Sonatype.sonatypeCentralHost
ThisBuild / sonatypeCredentialHost := sonatypeCentralHost

//ThisBuild / versionScheme := Some("semver-spec")

lazy val testSettings: Seq[Setting[_]] = Seq(
//  scriptedLaunchOpts := {
//    scriptedLaunchOpts.value ++ Seq(
//      "-Xmx1024M",
//      "-Dplugin.version=" + version.value
//    )
//  },
//  scriptedBufferLog := false
)

lazy val root = project
  .in(file("sbt-codeartifact"))
  .settings(
    sbtPlugin := true,
    sbtPluginPublishLegacyMavenStyle := false
  )

Global / onChangedBuildSource := ReloadOnSourceChanges