package org.phenoscape.kb.ingest.bgee;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.phenoscape.owl.util.OntologyUtil;
//import org.phenoscape.owl.util.OntologyUtil;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

// TODO: potentially change directories to a Java folder (instead of Scala)

public class BgeeExpressionToOWLJava {

	public static List<OWLAxiom> convert(String filePath) {
		//TODO: turn GeneID into an IRI in BGee?
		System.out.println("Convert Bgee: " + filePath);
		Map<String, String> geneToAnatomyMap = parseFile(filePath);
		
		List<OWLAxiom> axioms = new ArrayList<OWLAxiom>(); //one axiom object or is there an axiom list/set for each expression
		
		for (String gene: geneToAnatomyMap.keySet()){
			OWLDataFactoryImpl factory = new OWLDataFactoryImpl();

			OWLNamedIndividual expression = OntologyUtil.nextIndividual();			
			axioms.add(factory.getOWLDeclarationAxiom(expression));
			axioms.add(factory.getOWLClassAssertionAxiom(GeneExpression, expression)); //axioms.add(expression Type GeneExpression);
			
			
			//change gene into an OwlEntity
			
			factory.add(factory.getOWLDeclarationAxiom(gene)); //need to pass in an OWLEntity for gene
			
			
			//add anatomical ID
			OWLNamedIndividual structure = OntologyUtil.nextIndividual();
			axioms.add(factory.getOWLDeclarationAxiom(structure)) //TODO: does this need to be transformed beforehand?
		      
		      // add fact associating expression and gene to axiom
		    axioms.add(expression Fact (associated_with_gene, gene))
		      
		      // add fact associating expression and anatomy to axiom
		    axioms.add(expression Fact (associated_with_anatomy, anatomicalID))  //http://owlapi.sourceforge.net/javadoc/org/semanticweb/owlapi/model/OWLObjectProperty.html
			
		    //?? expression = OntologyUtil.nextIndividual(); 
			// what does this do and how do you add the ontologyutil dependency without maven?
	
	//		factory.getOWLDeclarationAxiom(expression);

		}
		return axioms;
	}

	private static Map<String, String> parseFile(String filePath) {
		Map<String, String> geneToAnatomy = new HashMap<String, String>();
		BufferedReader reader;
		int counter = 0;

		try {
			reader = new BufferedReader(new FileReader(filePath));
			String line = reader.readLine();
			while (line != null) {
				counter++;
				if (line.contains("Anatomical entity ID")) {
					line = reader.readLine();
					continue;
				}

				String[] strArr = line.split("\t");
				String present = strArr[6];
				if (present.equals("present")) {
					String geneID = strArr[0];
					String anatomicalID = strArr[2];
					geneToAnatomy.put(geneID, anatomicalID);
				}
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Done parsing " + counter + " lines");
		return geneToAnatomy;
	}
}
