package org.phenoscape.kb.ingest.zfin

import org.apache.commons.lang3.StringUtils
import org.phenoscape.kb.ingest.util.Vocab
import org.phenoscape.scowl._
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.{IRI, OWLAxiom}

import scala.collection.mutable
import scala.io.Source

object ZFINPreviousGeneNamesToOWL {

  val factory = OWLManager.getOWLDataFactory
  val hasRelatedSynonym = factory.getOWLAnnotationProperty(Vocab.HAS_RELATED_SYNONYM)

  def convert(data: Source): Set[OWLAxiom] = data.getLines.flatMap(translate).toSet

  def translate(line: String): Set[OWLAxiom] = {
    val items = line.split("\t")
    if (!items(0).startsWith("ZDB-GENE")) {
      Set.empty
    } else {
      val axioms = mutable.Set.empty[OWLAxiom]
      val geneID = StringUtils.stripToNull(items(0))
      val previousName = StringUtils.stripToNull(items(3))
      val geneIRI = IRI.create("http://zfin.org/" + geneID)
      val gene = Individual(geneIRI)
      axioms.add(Declaration(gene))
      axioms.add(geneIRI Annotation (hasRelatedSynonym, previousName))
      axioms.toSet
    }
  }

}