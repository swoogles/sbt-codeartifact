package codeartifact

import java.time.format.DateTimeFormatter
import java.time.ZoneId
import java.time.Instant

final case class CodeArtifactPackage(
  organization: String,
  name: String,
  version: String,
  scalaVersion: String,
  sbtBinaryVersion: Option[String],
  isScalaProject: Boolean,
  isSnapshot: Boolean // TODO Should this just be derived from the version, rather than passing along the SBT property?
) {

  def asMaven: String = {
    // We only want to append the scala binary version if we are publishing a scala library.
    val mvn = if (isScalaProject) {
      sbt.CrossVersion
        .partialVersion(scalaVersion)
        .map {
          case (3, _)     => s"${name}_3"
          case (maj, min) => List(name, "_", maj, ".", min).mkString
        }
        .getOrElse { sys.error("Invalid scalaVersion.") }
    } else {
      name
    }

    sbtBinaryVersion
      .map(mvn + "_" + _)
      .getOrElse(mvn)
  }

  def basePublishPath: String = s"${organization.replace(".", "/")}/$asMaven"
  def versionPublishPath: String = s"$basePublishPath/$version"

  val now = Instant.now()

  val timeStampBeginning =
    DateTimeFormatter
      .ofPattern("yyyyMMddHHmmss")
      .withZone(ZoneId.systemDefault())
      .format(now)

  val timeStampEnd =
    DateTimeFormatter
      .ofPattern("HHmmss")
      .withZone(ZoneId.systemDefault())
      .format(now)

  private def currentTimeNumber() =
    DateTimeFormatter
      .ofPattern("yyyyMMddHHmmss")
      .withZone(ZoneId.systemDefault())
      .format(now)

  def mavenMetadataRelease: String = {
    {
      <metadata modelVersion="1.1.0">
        <groupId>{organization}</groupId>
        <artifactId>{asMaven}</artifactId>
        <versioning>
          <!-- Do I need latest? -->
          <latest>{version}</latest>
          <release>{version}</release>
          <versions>
            <version>{version}</version>
          </versions>
          <lastUpdated>{currentTimeNumber()}</lastUpdated>
        </versioning>
      </metadata>
    }.buildString(stripComments = true)
  }

  /* Snapshot example:
  <?xml version="1.0" encoding="UTF-8"?>
<metadata>
    <groupId>org.carlspring.strongbox</groupId>
    <artifactId>strongbox-metadata</artifactId>
    <version>2.0-SNAPSHOT</version>
    <versioning>
        <snapshot>
            <timestamp>20150508.221712</timestamp>
            <buildNumber>2</buildNumber>
        </snapshot>
        <lastUpdated>20150508221310</lastUpdated>
        <snapshotVersions>
            <snapshotVersion>
                <classifier>javadoc</classifier>
                <extension>jar</extension>
                <value>2.0-20150508.220658-1</value>
                <updated>20150508220658</updated>
            </snapshotVersion>
            <snapshotVersion>
                <extension>jar</extension>
                <value>2.0-20150508.220658-1</value>
                <updated>20150508220658</updated>
            </snapshotVersion>
            <snapshotVersion>
                <extension>pom</extension>
                <value>2.0-20150508.220658-1</value>
                <updated>20150508220658</updated>
            </snapshotVersion>
            <snapshotVersion>
                <classifier>javadoc</classifier>
                <extension>jar</extension>
                <value>2.0-20150508.221205-2</value>
                <updated>20150508221205</updated>
            </snapshotVersion>
            <snapshotVersion>
                <extension>jar</extension>
                <value>2.0-20150508.221205-2</value>
                <updated>20150508221205</updated>
            </snapshotVersion>
            <snapshotVersion>
                <extension>pom</extension>
                <value>2.0-20150508.221205-2</value>
                <updated>20150508221205</updated>
            </snapshotVersion>
        </snapshotVersions>
    </versioning>
</metadata>

   */

  def mavenSnapshotMetadata: String = {
    // from maven docs: For example, snapshot version “1.0-20220119.164608-1” has the baseVersion “1.0-SNAPSHOT”.
    // https://maven.apache.org/repositories/artifacts.html
    val versionStart = version.replace("-SNAPSHOT", "")

    {
      <metadata modelVersion="1.1.0">
      <groupId>{organization}</groupId>
      <artifactId>{asMaven}</artifactId>
      <version>{versionStart}-{timeStampBeginning}.{timeStampEnd}-1</version>
      <baseVersion>{version}</baseVersion>
      <versioning>
        <snapshot>
          <timestamp>{currentTimeNumber()}</timestamp>
          <buildNumber>1</buildNumber>
        </snapshot>
        <lastUpdated>{currentTimeNumber()}</lastUpdated>
        <snapshotVersions>
          <snapshotVersion>
            <classifier>javadoc</classifier>
            <extension>jar</extension>
            <value>{versionStart}-{timeStampBeginning}.{timeStampEnd}-1</value>
            <updated>{currentTimeNumber()}</updated>
          </snapshotVersion>
          <snapshotVersion>
            <classifier>sources</classifier>
            <extension>jar</extension>
            <value>{versionStart}-{timeStampBeginning}.{timeStampEnd}-1</value>
            <updated>{currentTimeNumber()}</updated>
          </snapshotVersion>
          <snapshotVersion>
            <extension>jar</extension>
            <value>{versionStart}-{timeStampBeginning}.{timeStampEnd}-1</value>
            <updated>{currentTimeNumber()}</updated>
          </snapshotVersion>
          <snapshotVersion>
            <extension>pom</extension>
            <value>{versionStart}-{timeStampBeginning}.{timeStampEnd}-1</value>
            <updated>{currentTimeNumber()}</updated>
          </snapshotVersion>
        </snapshotVersions>
      </versioning>
    </metadata>
    }.buildString(stripComments = true)
  }

  def mavenMetadataSnapshot: String = {
    // TODO would be cool if this worked?

    {
      <metadata modelVersion="1.1.0">
        <groupId>{organization}</groupId>
        <artifactId>{asMaven}</artifactId>
        <versioning>
          <latest>{version}</latest>
          <release>{version}</release>
          <versions>
            <version>{version}{currentTimeNumber()}-1</version>
            <baseVersion>{version}</baseVersion>
          </versions>
          <lastUpdated>{currentTimeNumber()}</lastUpdated>
        </versioning>
      </metadata>
    }.buildString(stripComments = true)
  }

  def mavenMetadata: String = {
    if (isSnapshot) mavenSnapshotMetadata
    else
      mavenMetadataRelease
  }

}
