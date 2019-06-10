package org.phenoscape.kb.ingest.util

import junit.framework.Assert
import org.junit.Test
import org.phenoscape.scowl._
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.IRI

class PostCompositionParserTest {

  val factory = OWLManager.getOWLDataFactory

  @Test
  def testParser(): Unit = {
    Assert.assertEquals(factory.getOWLObjectIntersectionOf(Class(id("XAO:0004060")),
      (ObjectProperty(id("OBO_REL:part_of")) some (Class(id("XAO:0004060")) and (ObjectProperty(id("OBO_REL:made_from")) some Class(id("XAO:0004396"))))),
      (ObjectProperty(id("OBO_REL:has_part")) some Class(id("XAO:0004396")))),
      PostCompositionParser.parseExpression("XAO:0004060^OBO_REL:part_of(XAO:0004060^OBO_REL:made_from(XAO:0004396))^OBO_REL:has_part(XAO:0004396)").get)
  }

  def id(term: String): IRI = OBOUtil.iriForTermID(term)

}