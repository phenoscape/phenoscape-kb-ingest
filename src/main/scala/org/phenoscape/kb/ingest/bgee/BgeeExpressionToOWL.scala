package org.phenoscape.kb.ingest.bgee

import java.io._

import org.apache.commons.lang3.StringUtils
import org.phenoscape.kb.ingest.util.Vocab._
import org.phenoscape.kb.ingest.util.{OBOUtil, OntUtil}
import org.phenoscape.scowl._
import org.semanticweb.owlapi.model.{IRI, OWLAxiom}

import scala.collection.mutable
import scala.io.Source

object BgeeExpressionToOWL {

  def strToSource(str: String): Source = {
    io.Source.fromFile(str)
  }

  def convert(expressionData: Source): Set[OWLAxiom] = expressionData.getLines.flatMap(translate).toSet[OWLAxiom]

  def translate(expressionLine: String): Set[OWLAxiom] = {
    val items = expressionLine.split("\t", -1)
    
    // skip ZFA terms as these are embryonic/pre-adult development stages different from the KB data 
    if (items.length == 1 || items(6).startsWith("absent") || items(6).startsWith("Expression") || items(2).startsWith("ZFA")) {
      Set.empty
    } else {
      val axioms = mutable.Set.empty[OWLAxiom]
      val geneID = StringUtils.stripToNull(items(0))
      val anatomicalID = StringUtils.stripToNull(items(2))

      val expression = OntUtil.nextIndividual()
      axioms.add(Declaration(expression))

      val structure = OntUtil.nextIndividual()
      axioms.add(Declaration(structure))

      val term = StringUtils.stripToNull(items(2))
      var structureValid = false
      try {
        structureValid = (term.startsWith("UBERON") || term.startsWith("CL") || term.startsWith("ZFA"))
      } catch {
        case e: NullPointerException => System.err.println(e + " Empty structure in line: " + expressionLine)
      }
      if (structureValid) {
        val structureType = Class(OBOUtil.iriForTermID(term))
        axioms.add(structure Type structureType)
        println(structure Type structureType)
      }

      val id = "http://identifiers.org/ensembl/" + StringUtils.stripToNull(items(0))
      val geneIRI = IRI.create(id)
      val gene = Individual(geneIRI)
      axioms.add(Declaration(gene))
      axioms.add(expression Fact (associated_with_gene, gene))
      axioms.add(expression Fact (occurs_in, structure))
      axioms.toSet
    }
  }
}

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
