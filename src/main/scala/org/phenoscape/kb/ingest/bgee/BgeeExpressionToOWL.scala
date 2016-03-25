package org.phenoscape.kb.ingest.bgee

import scala.collection.JavaConversions._
import scala.collection.mutable
import java.io._
import scala.io.Source
import org.phenoscape.scowl.Functional._

import org.apache.commons.lang3.StringUtils
import org.phenoscape.kb.ingest.util.Vocab._
import org.phenoscape.kb.ingest.util.ExpressionUtil
import org.phenoscape.kb.ingest.util.OBOUtil
import org.phenoscape.kb.ingest.util.OntUtil
import org.phenoscape.scowl._
import org.semanticweb.owlapi.model.OWLAxiom
import org.semanticweb.owlapi.model.IRI

object BgeeExpressionToOWL {

  def strToSource(str: String): Source = {
    io.Source.fromFile(str)
  }

  def convert(expressionData: Source): Set[OWLAxiom] = expressionData.getLines.flatMap(translate).toSet[OWLAxiom]

  def translate(expressionLine: String): Set[OWLAxiom] = {
    //    println("====== translate ======")
    val items = expressionLine.split("\t", -1)
    //    println(items(2))
    if (items(6).startsWith("absent") || items(6).startsWith("Expression")) { //not too sure about function of this line originally, but it now skips over absent gene expressions
      Set.empty
    } else {
      val axioms = mutable.Set.empty[OWLAxiom]
      val geneID = StringUtils.stripToNull(items(0))
      val anatomicalID = StringUtils.stripToNull(items(2))

      val expression = OntUtil.nextIndividual()
      //add expression to axiom
      axioms.add(Declaration(expression)) //from this: import org.phenoscape.scowl.Functional._
      //      println(Declaration(expression)) // randomized
      axioms.add(expression Type GeneExpression) //add expression
      //      println(expression Type GeneExpression)
      //      println(expression Type GeneExpression)

      val structure = OntUtil.nextIndividual()
      axioms.add(Declaration(structure))
      //      println(Declaration(structure)); //random

      val term = (Option(StringUtils.stripToNull(items(2))).filter(_ != "\\").get)
      if (term.startsWith("UBERON") || term.startsWith("CL")) {
        val structureType = Class(OBOUtil.iriForTermID(Option(StringUtils.stripToNull(items(2))).filter(_ != "\\").get)) //http:// create IRI. different prefix?  
        axioms.add(structure Type structureType)
        //        println(structure Type structureType) //TODO: factory for outputting into functional?

      } else if (term.startsWith("ZFA")) {
        val structureType = Class(IRI.create("http://zfin.org/" + Option(StringUtils.stripToNull(items(2))).filter(_ != "\\").get))
        axioms.add(structure Type structureType)
        //        println(structure Type structureType)
      } else {
        println("Unprocessed term:");
        println(term);
      }

      //TODO: Declaration(NamedIndividual(<http://zfin.org/brpf1ENSDARG00000000001>))
      val id = "http://identifiers.org/ensembl/" + StringUtils.stripToNull(items(0))
      //      val geneIRI = new IRI("http://identifiers.org/ensembl/", StringUtils.stripToNull(items(0)))
      val geneIRI = IRI.create(id)
      //      val geneIRI = OBOUtil.zfinIRI(StringUtils.stripToNull(items(0)))
      val gene = Individual(geneIRI)
      axioms.add(Declaration(gene))
      //      println(Declaration(gene))
      axioms.add(expression Fact (associated_with_gene, gene))
      //      println(expression Fact (associated_with_gene, gene))
      axioms.add(expression Fact (occurs_in, structure))
      //      println(expression Fact (occurs_in, structure))
      //      println("----done")
      axioms.toSet
    }
  }
}
//TODO
//double check structure type and get gene iri from jim
//check java stuff with jim

object Main extends App {
  println("done parsing")
  val source = BgeeExpressionToOWL.strToSource("source_files/Danio_test.txt");
  val test = BgeeExpressionToOWL.convert(source)

  val file = new File("BgeeResult3.txt")
  val bw = new BufferedWriter(new FileWriter(file))
  bw.write(test.toString())
  bw.close()

  source.close();
  println("done writing results")
}

//classassertion asserting individual to a class 
 
     