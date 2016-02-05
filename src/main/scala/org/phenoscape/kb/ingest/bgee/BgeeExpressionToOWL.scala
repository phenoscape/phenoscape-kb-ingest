package org.phenoscape.kb.ingest.zfin

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.io.Source

import org.apache.commons.lang3.StringUtils
import org.phenoscape.owl.Vocab._
import org.phenoscape.owl.util.ExpressionUtil
import org.phenoscape.owl.util.OBOUtil
import org.phenoscape.owl.util.OntologyUtil
import org.phenoscape.scowl.OWL._
import org.semanticweb.owlapi.model.OWLAxiom

object BgeeExpressionToOWL {

  def convert(expressionData: Source): Set[OWLAxiom] = expressionData.getLines.flatMap(translate).toSet[OWLAxiom]

  def translate(expressionLine: String): Set[OWLAxiom] = {
    val items = expressionLine.split("\t", -1)
    if (items(6).startsWith("absent")) { //not too sure about function of this line originally, but it now skips over absent gene expressions
      Set.empty
    } else {
      val axioms = mutable.Set.empty[OWLAxiom]
      val geneID = StringUtils.stripToNull(items(0))
      val anatomicalID = StringUtils.stripToNull(items(2))

      val expression = OntologyUtil.nextIndividual()
      //add expression to axiom
      axioms.add(factory.getOWLDeclarationAxiom(expression)) //add OWLEntity?
      axioms.add(expression Type GeneExpression) //add expression
      
      val structure = OntologyUtil.nextIndividual() //why called multiple times?
      axioms.add(factory.getOWLDeclarationAxiom(structure))
      axioms.add(expression Fact (occurs_in, structure)) //which expression to use for  anatomicalID relationship?
    
//      val geneIRI = OBOUtil.zfinIRI(StringUtils.stripToNull(items(0))) //TODO: after finding out how to convert geneID into IRI
//      val gene = Individual(geneIRI)
      
      // add gene to axiom
      axioms.add(factory.getOWLDeclarationAxiom(gene))
      
      // add anatomicalID to axiom
      axioms.add(factory.getOWLDeclarationAxiom(anatomicalID)) //TODO: does this need to be transformed beforehand?
      
      // add fact associating expression and gene to axiom
      axioms.add(expression Fact (associated_with_gene, gene))
      
      // add fact associating expression and anatomy to axiom
      axioms.add(expression Fact (associated_with_anatomy, anatomicalID))  //http://owlapi.sourceforge.net/javadoc/org/semanticweb/owlapi/model/OWLObjectProperty.html
      // TODO: which fact to use
      axioms.toSet
    }
  }
}
  object Main extends App {
    val source = io.Source.fromFile("source_files/Danio_rerio_expr_simple.tsv")
    for (line <- source.getLines) {
      println(line)
    }
    val inst: ZFINExpressionToOWL = new ZFINExpressionToOWL()
    source.close();
    //convert(source);
  }
  
  
  // each gene within the file has an expression  (each line)
  // respective expression of the gene on that line
  // we need to add expression
  // we need to add gene
  // then need to add fact associated with gene and the anatomical id 
  // relate expression to the gene
  // relate gene to anatomical structure (expressed in the anatomical structure)
  // link expression to the anatomical structure and then link expression to the gene as well
  
  // 1. gene expression
  // 2. gene
  // 3. anatomical id
  // 4. fact between expression and gene
  // 5. fact between expression and anatomical id