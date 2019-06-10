package org.phenoscape.kb.ingest.util

import java.util.UUID

import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.{IRI, OWLNamedIndividual}

object OntUtil {

  val factory = OWLManager.getOWLDataFactory

  def nextIndividual(): OWLNamedIndividual = factory.getOWLNamedIndividual(this.nextIRI)

  def nextIRI(): IRI = {
    val uuid = UUID.randomUUID.toString
    val id = "http://purl.org/phenoscape/uuid/" + uuid
    IRI.create(id)
  }

}