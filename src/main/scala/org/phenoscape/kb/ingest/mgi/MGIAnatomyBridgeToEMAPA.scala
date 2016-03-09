package org.phenoscape.kb.ingest.mgi

import java.io.File

import scala.collection.JavaConversions._
import scala.io.Source

import org.apache.commons.lang3.StringUtils
import org.phenoscape.kb.ingest.util.OBOUtil
import org.phenoscape.scowl.Functional._
import org.phenoscape.scowl._
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.IRI
import org.semanticweb.owlapi.model.OWLAxiom
import org.semanticweb.owlapi.model.OWLOntology

object MGIAnatomyBridgeToEMAPA {

  val ontologyName = "http://purl.org/phenoscape/mgi/anatomy.owl"
  val manager = OWLManager.createOWLOntologyManager()

  def convert(mappings: Source): OWLOntology = {
    val axioms = mappings.getLines.map(translate(_)).flatten.toSet[OWLAxiom]
    manager.createOntology(axioms, IRI.create(ontologyName))
  }

  def translate(mapping: String): Set[OWLAxiom] = {
    val items = mapping.split("\t", -1)
    val mgiTerm = Class(OBOUtil.mgiAnatomyIRI(StringUtils.stripToNull(items(0))))
    val emapaTerm = Class(OBOUtil.iriForTermID(StringUtils.stripToNull(items(1))))
    Set(
      Declaration(mgiTerm),
      Declaration(emapaTerm),
      mgiTerm SubClassOf emapaTerm)
  }

}