package org.phenoscape.kb.ingest.mgi

import org.apache.commons.lang3.StringUtils
import org.phenoscape.kb.ingest.util.{OBOUtil, OntUtil, Vocab}
import org.phenoscape.kb.ingest.util.Vocab._
import org.phenoscape.scowl._
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.OWLAxiom

import scala.collection.mutable
import scala.io.Source

object MGIExpressionToOWL {

  val factory = OWLManager.getOWLDataFactory
  val mouse = Individual(Vocab.MOUSE)
  val rdfsLabel = factory.getRDFSLabel

  def convert(expressionData: Source): Set[OWLAxiom] =
    expressionData.getLines.drop(1).flatMap(translate).toSet[OWLAxiom] +
      (mouse Annotation (rdfsLabel, "Mus musculus"))

  def translate(expressionLine: String): Set[OWLAxiom] = {
    val items = expressionLine.split("\t", -1)
    if (StringUtils.stripToNull(items(5)) == "Absent") {
      Set.empty
    } else {
      val axioms = mutable.Set.empty[OWLAxiom]
      val expression = OntUtil.nextIndividual()
      axioms.add(Declaration(expression))
      axioms.add(expression Type GeneExpression)
      val structureID = StringUtils.stripToNull(items(4))
      val structureType = Class(OBOUtil.mgiAnatomyIRI(structureID))
      val punnedStructure = Individual(structureType.getIRI)
      axioms.add(Declaration(punnedStructure))
      axioms.add(expression Fact (occurs_in, punnedStructure))
      val geneIRI = MGIGeneticMarkersToOWL.getGeneIRI(StringUtils.stripToNull(items(1)))
      val gene = Individual(geneIRI)
      axioms.add(Declaration(gene))
      axioms.add(expression Fact (associated_with_gene, gene))
      axioms.add(expression Fact (associated_with_taxon, mouse))
      val publicationID = StringUtils.stripToNull(items(10))
      val publication = Individual(OBOUtil.mgiReferenceIRI(publicationID))
      axioms.add(expression Fact (dcSource, publication))
      axioms.toSet
    }
  }

}