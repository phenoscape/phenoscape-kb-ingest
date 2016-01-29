package org.phenoscape.kb.ingest.bgee;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.phenoscape.owl.util.OntologyUtil;
import org.semanticweb.owlapi.model.OWLAxiom;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;


// TODO: potentially change directories to a Java folder (instead of Scala)

public class BgeeExpressionToOWL {

	public static List<OWLAxiom> convert(String filePath) {
		//TODO: turn GeneID into an IRI in BGee?
		System.out.println("Convert Bgee: " + filePath);
		Map<String, String> geneToAnatomyMap = parseFile(filePath);
		
		List<OWLAxiom> list = new ArrayList<OWLAxiom>();
		
	    //?? expression = OntologyUtil.nextIndividual(); 
		// what does this do and how do you add the ontologyutil dependency without maven?

//		OWLDataFactoryImpl factory = new OWLDataFactoryImpl();
//		factory.getOWLDeclarationAxiom(expression);

		
		return null;
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
					String anatomicalID = strArr[1];
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
