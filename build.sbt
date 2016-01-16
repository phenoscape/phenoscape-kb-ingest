enablePlugins(JavaAppPackaging)

organization  := "org.phenoscape"

name          := "phenoscape-kb-ingest"

version       := "1.4.1"

//mainClass in Compile := Some("org.phenoscape.Main")

scalaVersion  := "2.11.7"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

javaOptions += "-Xmx8G"

resolvers += "Phenoscape Maven repository" at "http://phenoscape.svn.sourceforge.net/svnroot/phenoscape/trunk/maven/repository"


libraryDependencies ++= {
  Seq(
    "net.sourceforge.owlapi" %   "owlapi-distribution"    % "3.5.0",
	"org.phenoscape"         %   "scowl"                  % "0.9.2",
    "org.phenoscape"         %%  "kb-owl-tools"           % "1.4.1",
    "com.github.tototoshi"   %%  "scala-csv"              % "1.2.2",
    "org.apache.commons"     %   "commons-lang3"          % "3.1"
  )
}
