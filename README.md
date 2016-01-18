# phenoscape-kb-ingest

Translations for data sources into Phenoscape Knowlegebase axioms.

## Building

Install `sbt` on your system. For Mac OS X, it is easily done using [Homebrew](http://brew.sh): `brew install sbt`

To build:

`sbt compile`

## Using Eclipse

You must have the [sbteclipse](https://github.com/typesafehub/sbteclipse) plugin installed.

In the project directory, run `sbt eclipse`. In Eclipse, choose File > Import... > Existing Projects into Workspace. After any change to `build.sbt`, run `sbt eclipse` and refresh the project in Eclipse.