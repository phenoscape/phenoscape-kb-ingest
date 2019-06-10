package org.phenoscape.kb.ingest.zfin

import org.apache.commons.lang3.StringUtils
import org.phenoscape.kb.ingest.util.{ExpressionUtil, OBOUtil, OntUtil}
import org.phenoscape.kb.ingest.util.Vocab._
import org.phenoscape.scowl._
import org.semanticweb.owlapi.model.{IRI, OWLAxiom, OWLClass}

import scala.collection.mutable
import scala.io.Source

object ZFINPhenotypesToOWL {

  def convert(phenotypeData: Source): Set[OWLAxiom] = phenotypeData.getLines.flatMap(translate).toSet[OWLAxiom]

  def translate(expressionLine: String): Set[OWLAxiom] = {
    val items = expressionLine.split("\t")
    val axioms = mutable.Set[OWLAxiom]()
    val phenotype = OntUtil.nextIndividual()
    axioms += (phenotype Type AnnotatedPhenotype)
    axioms += (Declaration(phenotype))
    val superStructureID = StringUtils.stripToNull(items(7))
    val superStructureLabel = StringUtils.stripToEmpty(items(8))
    val subStructureID = StringUtils.stripToNull(items(3))
    val subStructureLabel = StringUtils.stripToEmpty(items(4))
    val relationID = StringUtils.stripToNull(items(5))
    val entityTerm = if (subStructureID == null) {
      Class(OBOUtil.iriForTermID(superStructureID))
    } else {
      val superStructure = Class(OBOUtil.iriForTermID(superStructureID))
      val subStructure = Class(OBOUtil.iriForTermID(subStructureID))
      val relation = ObjectProperty(OBOUtil.iriForTermID(relationID))
      val (namedComposition, compositionAxioms) = ExpressionUtil.nameForExpressionWithAxioms(subStructure and (relation some superStructure))
      axioms ++= compositionAxioms
      namedComposition
    }
    val qualityTerm = Class(OBOUtil.iriForTermID(StringUtils.stripToNull(items(9))))
    val qualityLabel = StringUtils.stripToEmpty(items(10))
    val relatedSuperStructureID = StringUtils.stripToNull(items(16))
    val relatedSuperStructureLabel = StringUtils.stripToEmpty(items(17))
    val relatedSubStructureID = StringUtils.stripToNull(items(12))
    val relatedSubStructureLabel = StringUtils.stripToEmpty(items(13))
    val relatedRelationID = StringUtils.stripToNull(items(14))
    val phenotypeStructureLabel = if (subStructureLabel.isEmpty) superStructureLabel else s"$subStructureLabel of $superStructureLabel"
    val phenotypeRelatedStructureLabel = if (relatedSubStructureLabel.isEmpty) relatedSuperStructureLabel else s"$relatedSubStructureLabel of $relatedSuperStructureLabel"
    val phenotypeSuffix = if (phenotypeRelatedStructureLabel.isEmpty) "" else s" towards $phenotypeRelatedStructureLabel"
    val phenotypeLabel = s"$phenotypeStructureLabel $qualityLabel$phenotypeSuffix"
    val relatedEntityTerm = if (relatedSubStructureID == null) {
      if (relatedSuperStructureID != null) {
        Class(OBOUtil.iriForTermID(relatedSuperStructureID))
      } else { null }
    } else {
      val relatedSuperStructure = Class(OBOUtil.iriForTermID(relatedSuperStructureID))
      val relatedSubStructure = Class(OBOUtil.iriForTermID(relatedSubStructureID))
      val relatedRelation = ObjectProperty(OBOUtil.iriForTermID(relatedRelationID))
      val (namedComposition, compositionAxioms) = ExpressionUtil.nameForExpressionWithAxioms(relatedSubStructure and (relatedRelation some relatedSuperStructure))
      axioms ++= compositionAxioms
      namedComposition
    }
    val eq_phenotype = (entityTerm, qualityTerm, relatedEntityTerm) match {
      case (null, null, _) => null
      case (entity: OWLClass, null, null) => has_part some (Present and (inheres_in some entity))
      case (entity: OWLClass, null, relatedEntity: OWLClass) => has_part some (Present and (inheres_in some entity) and (towards some relatedEntity))
      case (entity: OWLClass, Absent, null) => has_part some (LacksAllPartsOfType and (inheres_in some MultiCellularOrganism) and (towards some entity))
      case (entity: OWLClass, LacksAllPartsOfType, relatedEntity: OWLClass) => has_part some (LacksAllPartsOfType and (inheres_in some entity) and (towards some relatedEntity))
      case (null, quality: OWLClass, null) => has_part some quality
      case (null, quality: OWLClass, relatedEntity: OWLClass) => has_part some (quality and (towards some relatedEntity))
      case (entity: OWLClass, quality: OWLClass, null) => has_part some (quality and (inheres_in some entity))
      case (entity: OWLClass, quality: OWLClass, relatedEntity: OWLClass) => has_part some (quality and (inheres_in some entity) and (towards some relatedEntity))
    }
    if (eq_phenotype != null) {
      axioms.add(Declaration(MultiCellularOrganism))
      val (phenotypeClass, phenotypeAxioms) = ExpressionUtil.nameForExpressionWithAxioms(eq_phenotype)
      axioms += Declaration(phenotypeClass)
      axioms += (phenotypeClass Annotation (rdfsLabel, phenotypeLabel))
      axioms ++= phenotypeAxioms
      axioms += (phenotype Type phenotypeClass)
      val geneIRI = IRI.create("http://zfin.org/" + StringUtils.stripToNull(items(2)))
      val gene = Individual(geneIRI)
      axioms.add(Declaration(gene))
      axioms.add(phenotype Fact (associated_with_gene, gene))
      axioms.add(phenotype Fact (associated_with_taxon, Zebrafish))
    }
    val figureID = StringUtils.stripToNull(items(24))
    val figure = Individual(OBOUtil.zfinIRI(figureID))
    axioms += (phenotype Fact (dcSource, figure))
    axioms.toSet
  }

}