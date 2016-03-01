package org.phenoscape.kb.ingest.zfin

import scala.collection.JavaConversions._
import scala.collection.mutable
import java.io._
import scala.io.Source
import org.phenoscape.scowl.Functional._

import org.apache.commons.lang3.StringUtils
import org.phenoscape.owl.Vocab._
import org.phenoscape.owl.util.ExpressionUtil
import org.phenoscape.owl.util.OBOUtil
import org.phenoscape.owl.util.OntologyUtil
import org.phenoscape.scowl.OWL._
import org.semanticweb.owlapi.model.OWLAxiom
import org.semanticweb.owlapi.model.IRI


object BgeeExpressionToOWL {

  def convert(expressionData: Source): Set[OWLAxiom] = expressionData.getLines.flatMap(translate).toSet[OWLAxiom]

  def translate(expressionLine: String): Set[OWLAxiom] = {
    //    println("======")
    val items = expressionLine.split("\t", -1)
    if (items(6).startsWith("absent")) { //not too sure about function of this line originally, but it now skips over absent gene expressions
      Set.empty
    } else {
      val axioms = mutable.Set.empty[OWLAxiom]
      val geneID = StringUtils.stripToNull(items(0))
      val anatomicalID = StringUtils.stripToNull(items(2))

      val expression = OntologyUtil.nextIndividual()
      //add expression to axiom
      axioms.add(Declaration(expression)) //from this: import org.phenoscape.scowl.Functional._
      println(Declaration(expression))
      //      println(Declaration(expression))
      axioms.add(expression Type GeneExpression) //add expression
      println(expression Type GeneExpression)
      //      println(expression Type GeneExpression)

      val structure = OntologyUtil.nextIndividual()
      axioms.add(Declaration(structure))
      println(Declaration(structure));
      
//      val structureType = Class(OBOUtil.iriForTermID(Option(StringUtils.stripToNull(items(2))).filter(_ != "\\").get)); //http:// create IRI. different prefix?  
//       println(Class(IRI.create("http://zfin.org/" + Option(StringUtils.stripToNull(items(2))).filter(_ != "\\").get)))//.replaceAll(":", "_"))
      //val structureType = Class(IRI.create("http://purl.obolibrary.org/obo/" + Option(StringUtils.stripToNull(items(2))).filter(_ != "\\").get)).replaceAll(":", "_")); //http:// create IRI. different prefix?  http://purl.obolibrary.org/obo/UBERON_0000468
//      println(structureType)
      //https://zfin.org/ZFA:0001093
         //   val subStructureIDOpt = Option(StringUtils.stripToNull(items(5))).filter(_ != "\\")
  //println(subStructureIDOpt)
      //println(Option(StringUtils.stripToNull(items(2))).filter(_ != "\\").get))
      //if(
//      println((Option(StringUtils.stripToNull(items(2))).filter(_ != "\\").get))
      val term = (Option(StringUtils.stripToNull(items(2))).filter(_ != "\\").get)
      if (term.startsWith("UBERON") || term.startsWith("CL")){
          val structureType = Class(OBOUtil.iriForTermID(Option(StringUtils.stripToNull(items(2))).filter(_ != "\\").get)) //http:// create IRI. different prefix?  
          println(structureType)
          axioms.add(structure Type structureType)
          println(structure Type structureType)

      }
      
      if(term.startsWith("ZFA")){
          val structureType = Class(IRI.create("http://zfin.org/" + Option(StringUtils.stripToNull(items(2))).filter(_ != "\\").get))
          println(structureType)
          axioms.add(structure Type structureType)
          println(structure Type structureType)

      }
     

      val id = "http://identifiers.org/ensembl/" + StringUtils.stripToNull(items(0))
      //      val geneIRI = new IRI("http://identifiers.org/ensembl/", StringUtils.stripToNull(items(0)))
      val geneIRI = IRI.create(id)
      //      val geneIRI = OBOUtil.zfinIRI(StringUtils.stripToNull(items(0)))
      val gene = Individual(geneIRI)
      axioms.add(Declaration(gene))

      axioms.add(expression Fact (associated_with_gene, gene))
            println(expression Fact (associated_with_gene, gene))
      axioms.add(expression Fact (occurs_in, structure))
            println(expression Fact (occurs_in, structure))
            println("----llll")
      axioms.toSet
    }
  }
}
//TODO
//double check structure type and get gene iri from jim
//check java stuff with jim

object Main extends App {
  val source = io.Source.fromFile("source_files/Danio_rerio_expr_simple.tsv")
  //    for (line <- source.getLines) {
  //      println(line)
  //    }
  println("done parsing")
  val test = BgeeExpressionToOWL.convert(source)
  println("done converting")

  //println(test.isEmpty); //how to view items from Set[OWLAxiom]
  //    println(test);

  val file = new File("BgeeResult.txt")
  val bw = new BufferedWriter(new FileWriter(file))
  bw.write(test.toString())
  bw.close()

  source.close();
  println("done writing results")

  //convert(source);
}
 //TODO
  //test: compare lines to the input file
 // test: check total number of declarations
 
 
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
      
     