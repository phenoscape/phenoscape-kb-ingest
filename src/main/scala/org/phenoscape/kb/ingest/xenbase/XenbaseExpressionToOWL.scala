package org.phenoscape.kb.ingest.xenbase

import scala.collection.JavaConversions._
import scala.collection.Map
import scala.collection.mutable
import scala.io.Source

import org.apache.commons.lang3.StringUtils
import org.phenoscape.owl.Vocab
import org.phenoscape.owl.Vocab._
import org.phenoscape.owl.util.OBOUtil
import org.phenoscape.owl.util.OntologyUtil
import org.phenoscape.scowl.OWL._
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.OWLAxiom
import org.semanticweb.owlapi.model.OWLNamedIndividual

object XenbaseExpressionToOWL {

  val laevis = Individual(Vocab.XENOPUS_LAEVIS)
  val tropicalis = Individual(Vocab.XENOPUS_TROPICALIS)
  val manager = OWLManager.createOWLOntologyManager()
  val rdfsLabel = factory.getRDFSLabel()

  def convert(genepageMappingsFile: Source, laevisExpressionFile: Source, tropicalisExpressionFile: Source): Set[OWLAxiom] = {
    val mappings = indexGenepageMappings(genepageMappingsFile)
    convert(laevisExpressionFile, mappings, laevis) ++ convert(tropicalisExpressionFile, mappings, tropicalis)
  }

  def indexGenepageMappings(mappings: Source): Map[String, String] = {
    val index = mutable.Map[String, String]()
    for (mapping <- mappings.getLines()) {
      val items = mapping.split("\t", -1)
      val genepageID = StringUtils.stripToNull(items(0))
      for {
        geneIDs <- Option(StringUtils.stripToNull(items(1)))
        geneID <- geneIDs.split(",")
      } {
        index(StringUtils.stripToNull(geneID)) = genepageID
      }
    }
    return index
  }

  def convert(expressionData: Source, genepageMappings: Map[String, String], species: OWLNamedIndividual): Set[OWLAxiom] = {
    expressionData.getLines.flatMap(translate(_, genepageMappings, species)).toSet[OWLAxiom] +
      (laevis Annotation (rdfsLabel, factory.getOWLLiteral("Xenopus laevis"))) +
      (tropicalis Annotation (rdfsLabel, factory.getOWLLiteral("Xenopus tropicalis")))
  }

  def translate(expressionLine: String, genepageMappings: Map[String, String], species: OWLNamedIndividual): Set[OWLAxiom] = {
    val items = expressionLine.split("\t")
    if (StringUtils.stripToEmpty(items(3)) == "unspecified") {
      Set.empty
    } else {
      val axioms = mutable.Set[OWLAxiom]()
      val expression = OntologyUtil.nextIndividual()
      axioms.add(factory.getOWLDeclarationAxiom(expression))
      axioms.add(expression Type GeneExpression)
      val structureItems = items(3).split(",", -1)
      for (structureItem <- structureItems) {
        val structureID = StringUtils.stripToNull(structureItem.trim().split(" ")(0))
        val structureType = Class(OBOUtil.iriForTermID(structureID))
        val structure = OntologyUtil.nextIndividual()
        axioms.add(factory.getOWLDeclarationAxiom(structure))
        axioms.add(structure Type structureType)
        axioms.add(expression Fact (occurs_in, structure))
      }
      val evidenceText = StringUtils.stripToEmpty(items(7))
      if (evidenceText.contains("XB-IMG")) {
        val image = Individual(OBOUtil.xenbaseImageIRI(evidenceText))
        axioms.add(expression Fact (dcSource, image))
      }
      val genepageID = genepageMappings(StringUtils.stripToNull(items(0)))
      val geneIRI = XenbaseGenesToOWL.getGeneIRI(genepageID)
      val gene = Individual(geneIRI)
      axioms.add(factory.getOWLDeclarationAxiom(gene))
      axioms.add(expression Fact (associated_with_gene, gene))
      axioms.add(expression Fact (associated_with_taxon, species))
      axioms.toSet
    }
  }

}