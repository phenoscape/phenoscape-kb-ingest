package org.phenoscape.kb.ingest.xenbase

import org.apache.commons.lang3.StringUtils
import org.phenoscape.kb.ingest.util.Vocab._
import org.phenoscape.scowl._
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.{IRI, OWLAxiom}
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary

import scala.collection.mutable
import scala.io.Source

object XenbaseGenesToOWL {

  val factory = OWLManager.getOWLDataFactory
  val rdfsLabel = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI())
  val hasExactSynonym = factory.getOWLAnnotationProperty(HAS_EXACT_SYNONYM)
  val hasRelatedSynonym = factory.getOWLAnnotationProperty(HAS_RELATED_SYNONYM)

  def convert(markersData: Source): Set[OWLAxiom] = markersData.getLines.flatMap(translate).toSet[OWLAxiom]

  def translate(line: String): Set[OWLAxiom] = {
    val items = line.split("\t")
    val axioms = mutable.Set[OWLAxiom]()
    val geneID = StringUtils.stripToNull(items(0))
    val geneSymbol = StringUtils.stripToNull(items(1))
    val geneFullName = StringUtils.stripToNull(items(2))
    val geneIRI = getGeneIRI(geneID)
    val gene = Individual(geneIRI)
    axioms.add(Declaration(gene))
    axioms.add(gene Type Gene)
    axioms.add(geneIRI Annotation (rdfsLabel, geneSymbol))
    axioms.add(geneIRI Annotation (hasExactSynonym, geneFullName))
    if (items.size > 4) {
      val synonymsField = StringUtils.stripToEmpty(items(4))
      synonymsField.split("\\|").foreach(synonym => axioms.add(geneIRI Annotation (hasRelatedSynonym, synonym)))
    }
    axioms.toSet
  }

  def getGeneIRI(geneID: String): IRI = IRI.create("http://xenbase.org/" + geneID)

}