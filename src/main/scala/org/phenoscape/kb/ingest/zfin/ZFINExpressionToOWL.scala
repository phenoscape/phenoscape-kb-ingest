package org.phenoscape.kb.ingest.zfin

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.io.Source

import org.apache.commons.lang3.StringUtils
import org.phenoscape.kb.ingest.util.Vocab._
import org.phenoscape.kb.ingest.util.ExpressionUtil
import org.phenoscape.kb.ingest.util.OBOUtil
import org.phenoscape.kb.ingest.util.OntUtil
import org.phenoscape.scowl.Functional._
import org.phenoscape.scowl._
import org.semanticweb.owlapi.model.OWLAxiom

object ZFINExpressionToOWL {

  /**
   * @param expressionData tab-delimited input from http://zfin.org/downloads/wildtype-expression_fish.txt
   * Each line (an annotation) is translated into a set of OWL axioms
   * @return
   */
  def convert(expressionData: Source): Set[OWLAxiom] = expressionData.getLines.flatMap(translate).toSet[OWLAxiom]

  /**
   * @param expressionLine
   * Example line:
   * ZDB-GENE-000125-4	dlc	AB/TU	ZFA:0000107	eye	\ 	\ 	Pharyngula:Prim-15	Pharyngula:Prim-25	mRNA in situ hybridization	ZDB-PUB-051025-1	ZDB-EST-051107-52		ZDB-FISH-150901-29084
   *
   * Should generate OWL:
   *
   * Prefix: GO: <http://purl.obolibrary.org/obo/GO_>
   * Prefix: NCBITaxon: <http://purl.obolibrary.org/obo/NCBITaxon_>
   * Prefix: ZFA: <http://purl.obolibrary.org/obo/ZFA_>
   * Prefix: BFO: <http://purl.obolibrary.org/obo/BFO_>
   * Prefix: dc: <http://purl.org/dc/terms/>
   * Prefix: ps: <http://purl.org/phenoscape/vocab.owl#>
   * Prefix: zfin: <http://zfin.org/>
   * Prefix: uuid: <http://purl.org/phenoscape/uuid/>
   *
   *
   * Individual: zfin:ZDB-GENE-000125-4 # dlc
   *
   * Individual: uuid:3e1ad895-56b2-4b54-a3f8-c99e7b42f646
   *     Types: GO:0010467 # gene expression
   *     Facts:
   *         ps:associated_with_taxon  NCBITaxon:7955, # Danio rerio
   *         dc:source                 zfin:ZDB-PUB-051025-1,
   *         BFO:0000066               ZFA:0000107, # occurs_in eye (punned)
   *         ps:associated_with_gene   zfin:ZDB-GENE-000125-4 # dlc
   *
   * @return
   */
  def translate(expressionLine: String): Set[OWLAxiom] = {
    val items = expressionLine.split("\t", -1)
    if (items(0).startsWith("ZDB-EFG")) {
      Set.empty
    } else {
      // Example OWL that would be generated is provided in comments
      val axioms = mutable.Set.empty[OWLAxiom]
      // Individual: uuid:3e1ad895-56b2-4b54-a3f8-c99e7b42f646
      val expression = OntUtil.nextIndividual()
      axioms.add(Declaration(expression))
      // Individual: uuid:3e1ad895-56b2-4b54-a3f8-c99e7b42f646
      //     Types: GO:0010467
      axioms.add(expression Type GeneExpression)

      val superStructureID = Option(StringUtils.stripToNull(items(3))).filter(_ != "\\").get
      val subStructureIDOpt = Option(StringUtils.stripToNull(items(5))).filter(_ != "\\")
      val structureType = subStructureIDOpt match {
        case Some(subStructureID) => {
          val superStructure = Class(OBOUtil.iriForTermID(superStructureID))
          val subStructure = Class(OBOUtil.iriForTermID(subStructureID))
          val (structureClass, structureAxioms) = ExpressionUtil.nameForExpressionWithAxioms(subStructure and (part_of some superStructure))
          axioms ++= structureAxioms
          structureClass
        }
        case None => {
          Class(OBOUtil.iriForTermID(superStructureID))

        }
      }
      val punnedStructure = Individual(structureType.getIRI)
      axioms.add(Declaration(punnedStructure))
      // Individual: uuid:3e1ad895-56b2-4b54-a3f8-c99e7b42f646
      //     Facts:
      //         BFO:0000066 ZFA:0000107
      axioms.add(expression Fact (occurs_in, punnedStructure))

      val geneIRI = OBOUtil.zfinIRI(StringUtils.stripToNull(items(0)))
      val gene = Individual(geneIRI)
      val publicationID = StringUtils.stripToNull(items(10))
      val publication = Individual(OBOUtil.zfinIRI(publicationID))
      // Individual: zfin:ZDB-GENE-000125-4
      //     Facts:
      //         ps:associated_with_gene   zfin:ZDB-GENE-000125-4,
      //         ps:associated_with_taxon  NCBITaxon:7955, 
      //         dc:source                 zfin:ZDB-PUB-051025-1
      axioms.add(Declaration(gene))
      axioms.add(expression Fact (associated_with_gene, gene))
      axioms.add(expression Fact (associated_with_taxon, Zebrafish))
      axioms.add(expression Fact (dcSource, publication))
      axioms.toSet
    }
  }

}