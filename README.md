# phenoscape-kb-ingest

Translations for data sources into Phenoscape Knowlegebase axioms.

## Building

Install `sbt` on your system. For Mac OS X, it is easily done using [Homebrew](http://brew.sh): `brew install sbt`

To build:

`sbt compile`

## Using Eclipse

You must have the [Scala IDE](http://scala-ide.org) plugin installed in Eclipse.

You must have the [sbteclipse](https://github.com/typesafehub/sbteclipse) plugin installed for sbt.

In the project directory, run `sbt eclipse`. In Eclipse, choose File > Import... > Existing Projects into Workspace. After any change to `build.sbt`, run `sbt eclipse` and refresh the project in Eclipse.

## Obtaing the legacy history for this repository

The code in this repo was initially split off from the
[Phenoscape/phenoscape-owl-tools] repository. The relevant legacy history
of commits is archived in the tag [legacy-history]. It is also shared as
a replacement of the initial commit, which unless suppressed yields a
continuous history for all git commands, including `log`. To enable this,
you must explicitly fetch the replacement:

```sh
git fetch origin 'refs/replace/*:refs/replace/*'
```
Replace _origin_ with whatever you named your remote.

[Phenoscape/phenoscape-owl-tools]: https://github.com/phenoscape/phenoscape-owl-tools
[legacy-history]: https://github.com/phenoscape/phenoscape-kb-ingest/releases/tag/legacy-history