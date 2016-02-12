package org.phenoscape.kb.ingest.example;

import java.util.HashSet;
import java.util.Set;

import org.phenoscape.owl.Vocab;
import org.phenoscape.owl.util.OntologyUtil;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

public class ScalaFromJava {

	private OWLDataFactory factory = OWLManager.getOWLDataFactory();

	public void createAxioms() {
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		OWLNamedIndividual expression = OntologyUtil.nextIndividual();
		OWLNamedIndividual gene = factory.getOWLNamedIndividual(IRI.create("http://zfin.org/brpf1"));
		axioms.add(factory.getOWLClassAssertionAxiom(Vocab.GeneExpression(), expression));
		axioms.add(factory.getOWLObjectPropertyAssertionAxiom(Vocab.associated_with_gene(), expression, gene));
	}

}
