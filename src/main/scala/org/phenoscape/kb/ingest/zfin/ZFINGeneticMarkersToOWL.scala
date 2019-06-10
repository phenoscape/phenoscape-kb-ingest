package org.phenoscape.kb.ingest.zfin

import org.apache.commons.lang3.StringUtils
import org.phenoscape.kb.ingest.util.Vocab._
import org.phenoscape.scowl._
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.{IRI, OWLAxiom}
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary

import scala.collection.mutable
import scala.io.Source

object ZFINGeneticMarkersToOWL {

  val factory = OWLManager.getOWLDataFactory
  val rdfsLabel = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI)
  val hasExactSynonym = factory.getOWLAnnotationProperty(HAS_EXACT_SYNONYM)

  def convert(markersData: Source): Set[OWLAxiom] = markersData.getLines.flatMap(translate).toSet[OWLAxiom]

  def translate(line: String): Set[OWLAxiom] = {
    val items = line.split("\t")
    if (items(3) != "GENE") {
      Set.empty
    } else {
      val axioms = mutable.Set.empty[OWLAxiom]
      val geneID = StringUtils.stripToNull(items(0))
      val geneSymbol = StringUtils.stripToNull(items(1))
      val geneFullName = StringUtils.stripToNull(items(2))
      val geneIRI = IRI.create("http://zfin.org/" + geneID)
      val gene = Individual(geneIRI)
      axioms.add(Declaration(gene))
      axioms.add(gene Type Gene)
      axioms.add(geneIRI Annotation (rdfsLabel, geneSymbol))
      axioms.add(geneIRI Annotation (hasExactSynonym, geneFullName))
      axioms.toSet
    }
  }

}