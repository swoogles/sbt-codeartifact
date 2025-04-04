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

lazy val testSettings: Seq[Setting[_]] = Seq(
  scriptedLaunchOpts := {
    scriptedLaunchOpts.value ++ Seq(
      "-Xmx1024M",
      "-Dplugin.version=" + version.value
    )
  },
  scriptedBufferLog := false
)

lazy val core = project
  .in(file("core"))
  .settings(testSettings)

lazy val `sbt-codeartifact` = project
  .in(file("sbt-codeartifact"))
  .dependsOn(core)
  .settings(testSettings)
  .settings(
    sbtPlugin := true,
  )

lazy val root = project
  .in(file("."))
  .aggregate(core, `sbt-codeartifact`)
  .settings(
    publish / skip := true,
  )