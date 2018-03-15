enablePlugins(JavaAppPackaging)

organization  := "org.phenoscape"

name          := "phenoscape-kb-ingest"

version       := "1.6.1"

scalaVersion  := "2.12.4"

crossScalaVersions := Seq("2.11.8", "2.12.4")

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

javaOptions += "-Xmx8G"

libraryDependencies ++= {
  Seq(
    "net.sourceforge.owlapi"    %   "owlapi-distribution"      % "4.2.8",
    "org.phenoscape"            %%  "scowl"                    % "1.3",
    "org.scala-lang.modules"    %%  "scala-parser-combinators" % "1.0.4",
    "com.github.tototoshi"      %%  "scala-csv"                % "1.3.4",
    "org.apache.commons"        %   "commons-lang3"            % "3.1",
    "log4j"                     %   "log4j"                    % "1.2.17",
    "junit"                     %   "junit"                    % "4.12"
  )
}
