package org.phenoscape.kb.ingest.human

import scala.collection.mutable
import scala.io.Source

import org.apache.commons.lang3.StringUtils
import org.phenoscape.owl.Vocab
import org.phenoscape.owl.Vocab._
import org.phenoscape.owl.util.OBOUtil
import org.phenoscape.owl.util.OntologyUtil
import org.phenoscape.scowl.Functional._
import org.phenoscape.scowl._
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLAxiom
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary

object HumanPhenotypesToOWL {

  val factory = OWLManager.getOWLDataFactory
  val rdfsLabel = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI)
  val human = factory.getOWLNamedIndividual(Vocab.HUMAN)

  def convert(phenotypeData: Source): Set[OWLAxiom] = phenotypeData.getLines.drop(1).flatMap(translate).toSet[OWLAxiom]

  def translate(phenotypeLine: String): Set[OWLAxiom] = {
    val items = phenotypeLine.split("\t")
    val axioms = mutable.Set.empty[OWLAxiom]
    val phenotype = OntologyUtil.nextIndividual()
    axioms.add(phenotype Type AnnotatedPhenotype)
    axioms.add(Declaration(phenotype))
    val phenotypeID = StringUtils.stripToNull(items(3))
    val phenotypeClass = Class(OBOUtil.iriForTermID(phenotypeID))
    axioms.add(phenotype Type phenotypeClass)
    val geneIRI = IRI.create("http://www.ncbi.nlm.nih.gov/gene/" + StringUtils.stripToNull(items(0)))
    val geneSymbol = StringUtils.stripToNull(items(1))
    axioms.add(geneIRI Annotation (rdfsLabel, geneSymbol))
    val gene = Individual(geneIRI)
    axioms.add(gene Type Gene)
    axioms.add(Declaration(gene))
    axioms.add(phenotype Fact (associated_with_gene, gene))
    axioms.add(phenotype Fact (associated_with_taxon, human))
    axioms.toSet
  }

}