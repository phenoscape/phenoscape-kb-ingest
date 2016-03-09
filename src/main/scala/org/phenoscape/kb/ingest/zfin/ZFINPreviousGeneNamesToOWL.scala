package org.phenoscape.kb.ingest.zfin

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.io.Source

import org.apache.commons.lang3.StringUtils
import org.phenoscape.owl.Vocab
import org.phenoscape.scowl.Functional._
import org.phenoscape.scowl._
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLAxiom

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