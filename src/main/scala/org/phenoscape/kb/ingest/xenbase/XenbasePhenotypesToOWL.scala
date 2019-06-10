package org.phenoscape.kb.ingest.xenbase

import org.apache.commons.lang3.StringUtils
import org.phenoscape.kb.ingest.util.{ExpressionUtil, OBOUtil, OntUtil, Vocab}
import org.phenoscape.kb.ingest.util.Vocab._
import org.phenoscape.scowl._
import org.semanticweb.owlapi.model.{OWLAxiom, OWLNamedIndividual}

import scala.collection.Map
import scala.io.Source

object XenbasePhenotypesToOWL {

  val laevis = Individual(Vocab.XENOPUS_LAEVIS)
  val tropicalis = Individual(Vocab.XENOPUS_TROPICALIS)

  def convertToAxioms(phenotypeData: Source): Set[OWLAxiom] = phenotypeData.getLines.drop(1).flatMap(translate).toSet

  def translate(annotationLine: String): Set[OWLAxiom] = {
    val items = annotationLine.split("\t")
    val geneText = StringUtils.stripToNull(items(11))
    if (geneText != null) {
      val phenotype = OntUtil.nextIndividual()
      val sourceText = StringUtils.stripToNull(items(0)).toUpperCase
      val source = if (sourceText.contains("IMG")) Individual(OBOUtil.xenbaseImageIRI(sourceText))
      else Individual(OBOUtil.xenbaseArticleIRI(sourceText))
      val species = taxon(StringUtils.stripToNull(items(1)))
      val gene = Individual(XenbaseGenesToOWL.getGeneIRI(fixGeneID(geneText)))
      val quality = Class(OBOUtil.iriForTermID(StringUtils.stripToNull(items(15))))
      val qualityLabel = StringUtils.stripToEmpty(items(16))
      val (entity, entityAxioms) = OBOUtil.translatePostCompositionNamed(StringUtils.stripToNull(items(13)))
      val entityLabel = StringUtils.stripToEmpty(items(14))
      val (optionalRelatedEntity, relatedEntityAxioms) = optionWithSet(Option(StringUtils.stripToNull(items(17))).map(OBOUtil.translatePostCompositionNamed))
      val relatedEntityLabel = Option(StringUtils.stripToNull(items(18))).map(re => s" towards $re").getOrElse("")
      val phenotypeLabel = s"$qualityLabel: $entityLabel$relatedEntityLabel"
      val eqPhenotype = (entity, quality, optionalRelatedEntity) match {
        case (entity, Absent, None)                             => has_part some (LacksAllPartsOfType and (inheres_in some MultiCellularOrganism) and (towards value Individual(entity.getIRI)))
        case (entity, LacksAllPartsOfType, Some(relatedEntity)) => has_part some (LacksAllPartsOfType and (inheres_in some entity) and (towards value Individual(relatedEntity.getIRI)))
        case (entity, quality, Some(relatedEntity))             => has_part some (quality and (inheres_in some entity) and (towards some relatedEntity))
        case (entity, quality, None)                            => has_part some (quality and (inheres_in some entity))
      }
      val (phenotypeClass, phenotypeAxioms) = ExpressionUtil.nameForExpressionWithAxioms(eqPhenotype)
      Set(
        phenotype Type AnnotatedPhenotype,
        phenotypeClass Annotation (rdfsLabel, phenotypeLabel),
        phenotype Fact (associated_with_gene, gene),
        phenotype Fact (associated_with_taxon, species),
        phenotype Fact (dcSource, source),
        phenotype Type phenotypeClass,
        Declaration(phenotypeClass),
        Declaration(gene),
        Declaration(species)) ++
        entityAxioms ++
        relatedEntityAxioms
    } else Set.empty
  }

  def fixGeneID(id: String): String = "XB-GENEPAGE-" + id.split(":")(1)

  val taxon: Map[String, OWLNamedIndividual] = Map(
    "XBTAXON:0000001" -> tropicalis,
    "XBTAXON:0000002" -> laevis)

  private def optionWithSet[T, S](in: Option[(T, Set[S])]): (Option[T], Set[S]) = in match {
    case Some((thing, set)) => (Option(thing), set)
    case None               => (None, Set.empty)
  }

}