package org.phenoscape.kb.ingest.util

import java.io.StringWriter
import java.util.ArrayList
import scala.collection.JavaConversions._
import scala.collection.mutable
import org.phenoscape.scowl._
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.OWLAnnotationProperty
import org.semanticweb.owlapi.model.OWLAxiom
import org.semanticweb.owlapi.model.OWLClass
import org.semanticweb.owlapi.model.OWLClassExpression
import org.semanticweb.owlapi.model.OWLClassExpressionVisitor
import org.semanticweb.owlapi.model.OWLEntity
import org.semanticweb.owlapi.model.OWLIndividual
import org.semanticweb.owlapi.model.OWLLiteral
import org.semanticweb.owlapi.model.OWLObject
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom
import org.semanticweb.owlapi.model.OWLOntology
import org.semanticweb.owlapi.model.OWLOntologySetProvider
import org.semanticweb.owlapi.model.OWLQuantifiedObjectRestriction
import org.semanticweb.owlapi.reasoner.OWLReasoner
import org.semanticweb.owlapi.util.AnnotationValueShortFormProvider
import org.semanticweb.owlapi.util.ShortFormProvider
import java.net.URLEncoder
import java.net.URLDecoder
import java.util.regex.Pattern
import java.net.URL
import java.net.URI
import java.util.UUID
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxObjectRenderer

object ExpressionUtil {

  val factory = OWLManager.getOWLDataFactory
  val namedExpressionPrefix = "http://purl.org/phenoscape/expression?value="
  val namedSubClassPrefix = "http://purl.org/phenoscape/subclassof?value="

  def nameForExpression(expression: OWLClassExpression): OWLClass = name(expression, namedExpressionPrefix, false)

  def nameForSubClassOf(expression: OWLClassExpression): OWLClass = name(expression, namedSubClassPrefix, true)

  private def name(expression: OWLClassExpression, prefix: String, unique: Boolean): OWLClass = expression match {
    case named: OWLClass => named
    case _ => {
      val writer = new StringWriter()
      val renderer = new ManchesterOWLSyntaxObjectRenderer(writer, FullIRIProvider)
      expression.accept(renderer: OWLClassExpressionVisitor)
      writer.close()
      val fragment = if (unique) s"#${UUID.randomUUID.toString}" else ""
      Class(s"$prefix${URLEncoder.encode(writer.toString, "UTF-8")}$fragment")
    }
  }

  def nameForExpressionWithAxioms(expression: OWLClassExpression): (OWLClass, Set[OWLAxiom]) = expression match {
    case named: OWLClass => (named, Set.empty)
    case _ => {
      val named = nameForExpression(expression)
      (named, Set(named EquivalentTo expression))
    }
  }

  def uniqueNameForExpressionWithAxioms(expression: OWLClassExpression): (OWLClass, Set[OWLAxiom]) = expression match {
    case named: OWLClass => (named, Set.empty)
    case _ => {
      val named = name(expression, namedExpressionPrefix, true)
      (named, Set(named EquivalentTo expression))
    }
  }

  def nameForSubClassWithAxioms(expression: OWLClassExpression): (OWLClass, Set[OWLAxiom]) = expression match {
    case named: OWLClass => (named, Set.empty)
    case _ => {
      val named = nameForSubClassOf(expression)
      (named, Set(named SubClassOf expression))
    }
  }

  object FullIRIProvider extends ShortFormProvider {

    def getShortForm(entity: OWLEntity): String = s"<${entity.getIRI.toString}>"

    def dispose(): Unit = Unit

  }

}