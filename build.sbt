enablePlugins(JavaAppPackaging)

organization  := "org.phenoscape"

name          := "phenoscape-kb-ingest"

version       := "1.6.2"

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

licenses := Seq("MIT license" -> url("https://opensource.org/licenses/MIT"))

homepage := Some(url("https://github.com/phenoscape/phenoscape-kb-ingest"))

scalaVersion  := "2.11.12"

crossScalaVersions := Seq("2.11.12", "2.12.8", "2.13.0")

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

javaOptions += "-Xmx8G"

libraryDependencies ++= {
  Seq(
    "net.sourceforge.owlapi"    %   "owlapi-distribution"      % "4.2.8",
    "org.phenoscape"            %%  "scowl"                    % "1.3.4",
    "org.scala-lang.modules"    %%  "scala-parser-combinators" % "1.1.2",
    "com.github.tototoshi"      %%  "scala-csv"                % "1.3.6",
    "org.apache.commons"        %   "commons-lang3"            % "3.1",
    "log4j"                     %   "log4j"                    % "1.2.17",
    "junit"                     %   "junit"                    % "4.12"
  )
}

pomExtra := <scm>
    <url>git@github.com:phenoscape/phenoscape-kb-ingest.git</url>
    <connection>scm:git:git@github.com:phenoscape/phenoscape-kb-ingest.git</connection>
  </scm>
  <developers>
    <developer>
      <id>balhoff</id>
      <name>Jim Balhoff</name>
      <email>jim@balhoff.org</email>
    </developer>
  </developers>
