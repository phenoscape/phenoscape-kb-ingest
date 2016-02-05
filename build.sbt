enablePlugins(JavaAppPackaging)

organization  := "org.phenoscape"

name          := "phenoscape-kb-ingest"

version       := "1.4.3"

//mainClass in Compile := Some("org.phenoscape.Main")

scalaVersion  := "2.11.7"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

javaOptions += "-Xmx8G"

resolvers += "Phenoscape Maven repository" at "http://phenoscape.svn.sourceforge.net/svnroot/phenoscape/trunk/maven/repository"
resolvers += "nxparser-repo" at "http://nxparser.googlecode.com/svn/repository/"

resolvers += "Bigdata releases" at "http://www.systap.com/maven/releases/"

resolvers += "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases"

resolvers += "apache-repo-releases" at "http://repository.apache.org/content/repositories/releases/"

resolvers += "BBOP repository" at "http://code.berkeleybop.org/maven/repository"

libraryDependencies ++= {
  Seq(
    "net.sourceforge.owlapi" %   "owlapi-distribution"    % "3.5.0",
	"org.phenoscape"         %%  "scowl"                  % "0.9.3",
    "org.phenoscape"         %%  "kb-owl-tools"           % "1.4.2",
    "com.github.tototoshi"   %%  "scala-csv"              % "1.2.2",
    "org.apache.commons"     %   "commons-lang3"          % "3.1"
  )
}
