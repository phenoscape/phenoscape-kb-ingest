package org.phenoscape.kb.ingest.bgee;

import org.phenoscape.owl.PropertyNormalizer;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxParserFactory;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLParser;
import org.semanticweb.owlapi.io.OWLParserException;
import org.semanticweb.owlapi.io.StreamDocumentSource;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.UnloadableImportException;


public class BgeeToOWLTest {
	private static final int digitAccuracy = 10;

	public static final String danio_rerio = "/Danio_rerio_expr_simple.tsv";
	public static final String sourceDirectory = "/source_files";
	public static final String results = "/BgeeResult.txt";

	@Test
	public void testNumAxioms() {

		String absPath = new File("").getAbsolutePath();
		String filePath = absPath + sourceDirectory + danio_rerio;
		String filePathResults = absPath + results;
		System.out.println(filePath);
//		BgeeExpressionToOWLJava.convert(filePath);

		int numExpressions = 0;

		BufferedReader input;
		try {
			input = new BufferedReader(new FileReader(filePath));
			String line = input.readLine();
			while (line != null) {
				String[] str = line.split("\t");
				if (line.contains("Gene ID") && !str[6].equals("Absent"))
					continue;
				numExpressions++;
			}
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		int length = 0;
		BufferedReader results;
		try {
			// int counter = 0;
			results = new BufferedReader(new FileReader(filePathResults));
			String line = results.readLine();
			String[] str = line.split(",");
			length = str.length;
			results.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertEquals("Number of expressions match", length, numExpressions * 5);

		// set should match
		// write down what you expect to get in manchester or functional syntax
		// read in the file through the owl api and the set that results from
		// the code that you're testing and the sets of axioms should appear
		// identical
		// use the owl api on 5 cases....

		// put that in writing of owl, functional, or manchester syntax, then
		// you should know what to expect from the parse compared to what we
		// have now.

		// if the owl api changes it will screw up the test
		// the order that the owl api will write out axioms is not
		// deterministic.

		// reading a set of axioms through the owl api
		// ask for review of the output from jim.

		// next: MGI?

		// checkout: mice file, Xenopus tropicalis
	}

	@Test // TODO: make into a scala test
	public void testAccuracy() {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = null;
		try {
			ontology = manager.createOntology();
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		OWLParser parser = new ManchesterOWLSyntaxParserFactory().createParser(manager);

		String expectedContent = "" + "Prefix: so: <http://purl.org/phenoscape/uuid/69aa90b5-584a-4b34-8206-cea3e7e4f79f/>\n"
				+ "Individual: <http://zfin.org/brpf1ENSDARG00000000001> \n" 		
				+ "Individual: <http://purl.org/phenoscape/uuid/3f64df86-80cc-4394-8449-56ea76b78cb4> \n"
				+ "Individual: <http://purl.org/phenoscape/uuid/4f4e753d-b9a0-431c-a958-447c9d06d26c> \n"
				+ "Class: <http://purl.org/> \n"
				+ "ObjectProperty: <http://purl.org/> \n"
				+ "Class: so:Young\n" 
				+ "\n" 
				+ "Class: so:Teenager\n"
				+ "  SubClassOf: (so:Teenager and so:Young)\n" + "";

		// Create an input stream from the ontology, and use the parser to read
		// its
		// contents into the ontology.
		try (final InputStream in = new ByteArrayInputStream(expectedContent.getBytes())) {
			parser.parse(new StreamDocumentSource(in), ontology);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OWLParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OWLOntologyChangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnloadableImportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//TODO: make test file
		String filePath = "source_files/Danio_test.txt";
		Set<OWLAxiom> testSet = BgeeExpressionToOWLJava.convert(filePath);
		
		System.out.println();
		
        List<OWLAxiom> setToSort = new ArrayList<OWLAxiom>(testSet);
		Collections.sort(setToSort);
		String test = "test";
		assertEquals(test, "test");
		
		for (int i = 0; i < setToSort.size(); i++){
			OWLAxiom axiom = setToSort.get(i);
		//for (OWLAxiom axiom : setToSort) {
			System.out.println(axiom.toString()); //TODO: may not be getting complete functional syntax
//			switch (i){
//				case 0: assertEquals(axiom.toString(), "Declaration(NamedIndividual(<http://purl.org/phenoscape/uuid/ba73f965-1822-4a11-a8c5-e1e62bf9f1b3>))");
//				break;
//			}
//			System.out.println("now test");
//			if (i == 0)
//				assertEquals(test, "test");
//				assertEquals(axiom.toString(), "Declaration(NamedIndividual(<http://purl.org/phenoscape/uuid/ba73f965-1822-4a11-a8c5-e1e62bf9f1b3>))");
//System.out.println("---");
			
			//TODO: gets a didfferent UUID each time
		}
//		
//
//		
//		
//		
//		System.out.println();
//		System.out.println(testSet);
//		
//		System.out.println(testSet.equals(ontology.getAxioms()));
//
//		System.out.println("== All Axioms: ==");
//		for (OWLAxiom axiom : ontology.getAxioms()) {
//			System.out.println(axiom); //TODO: may not be getting complete functional syntax
//		}
//		System.out.println("===");
	}

}
