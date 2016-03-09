package org.phenoscape.kb.ingest.mgi

import scala.collection.mutable
import scala.io.Source

import org.apache.commons.lang3.StringUtils
import org.phenoscape.kb.ingest.util.Vocab._
import org.phenoscape.scowl.Functional._
import org.phenoscape.scowl._
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLAxiom
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary

object MGIGeneticMarkersToOWL {

  val factory = OWLManager.getOWLDataFactory
  val rdfsLabel = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI)
  val hasExactSynonym = factory.getOWLAnnotationProperty(HAS_EXACT_SYNONYM)
  val hasRelatedSynonym = factory.getOWLAnnotationProperty(HAS_RELATED_SYNONYM)

  def convert(markersData: Source): Set[OWLAxiom] = markersData.getLines.flatMap(translate).toSet[OWLAxiom]

  def translate(line: String): Set[OWLAxiom] = {
    val items = line.split("\t")
    if (items(9) != "Gene") {
      Set.empty
    } else {
      val axioms = mutable.Set.empty[OWLAxiom]
      val geneID = StringUtils.stripToNull(items(0))
      val geneSymbol = StringUtils.stripToNull(items(6))
      val geneFullName = StringUtils.stripToNull(items(8))
      val geneIRI = getGeneIRI(geneID)
      val gene = Individual(geneIRI)
      axioms.add(Declaration(gene))
      axioms.add(gene Type Gene)
      axioms.add(geneIRI Annotation (rdfsLabel, geneSymbol))
      axioms.add(geneIRI Annotation (hasExactSynonym, geneFullName))
      if (items.size > 11) {
        val synonymsField = StringUtils.stripToEmpty(items(11))
        synonymsField.split("\\|").foreach(synonym => axioms.add(geneIRI Annotation (hasRelatedSynonym, synonym)))
      }
      axioms.toSet
    }
  }

  def getGeneIRI(geneID: String): IRI = IRI.create("http://www.informatics.jax.org/marker/" + geneID)

}