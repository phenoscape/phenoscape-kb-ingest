package org.phenoscape.kb.ingest.mgi

import org.apache.commons.lang3.StringUtils
import org.phenoscape.kb.ingest.util.{OBOUtil, OntUtil, Vocab}
import org.phenoscape.kb.ingest.util.Vocab._
import org.phenoscape.scowl._
import org.semanticweb.owlapi.model.OWLAxiom

import scala.collection.mutable
import scala.io.Source

object MGIPhenotypesToOWL {

  val mouse = Individual(Vocab.MOUSE)

  def convert(phenotypeData: Source): Set[OWLAxiom] = phenotypeData.getLines.drop(1).flatMap(translate).toSet[OWLAxiom]

  def translate(expressionLine: String): Set[OWLAxiom] = {
    val items = expressionLine.split("\t", -1)
    val axioms = mutable.Set.empty[OWLAxiom]
    val phenotype = OntUtil.nextIndividual()
    axioms.add(phenotype Type AnnotatedPhenotype)
    axioms.add(Declaration(phenotype))
    val phenotypeID = StringUtils.stripToNull(items(10))
    val phenotypeClass = Class(OBOUtil.iriForTermID(phenotypeID))
    axioms.add(phenotype Type phenotypeClass)
    val geneIRI = MGIGeneticMarkersToOWL.getGeneIRI(StringUtils.stripToNull(items(0)))
    val gene = Individual(geneIRI)
    axioms.add(Declaration(gene))
    axioms.add(phenotype Fact (associated_with_gene, gene))
    axioms.add(phenotype Fact (associated_with_taxon, mouse))
    val publicationID = StringUtils.stripToNull(items(11))
    val publication = Individual(OBOUtil.mgiReferenceIRI(publicationID))
    axioms.add(phenotype Fact (dcSource, publication))
    axioms.toSet
  }

}