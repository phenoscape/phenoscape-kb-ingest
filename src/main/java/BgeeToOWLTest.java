import static org.junit.Assert.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.phenoscape.kb.ingest.bgee.BgeeExpressionToOWL;

public class BgeeToOWLTest {
	public static final String sourceDirectory = "/source_files";
	public static final String danio_rerio = "/Danio_rerio_expr_simple.tsv";

	private static final String expressionPrefix = "http://purl.org/phenoscape/uuid/";
	private static final String DECLARATION_STR = "Declaration";
	private static final String CLASSASSERTION_STR = "ClassAssertion";
	private static final String OBJECTPROPERTYASSERTION_STR = "ObjectPropertyAssertion";

	String testPath;
	int uniqueGeneIDs;
	Set<OWLAxiom> owlAxiomSet;

	@Before
	public void setUp() {
		String absPath = new File("").getAbsolutePath();
		testPath = absPath + sourceDirectory + danio_rerio;

		scala.collection.Set<OWLAxiom> testSet = BgeeExpressionToOWL.convert(BgeeExpressionToOWL.strToSource(testPath));
		System.out.println("UnitTest: Finished creating OWLAxiom test set");
		owlAxiomSet = scala.collection.JavaConverters.setAsJavaSetConverter(testSet).asJava();
	}

	@Test
	public void testNumAxioms() {
		int numExpressions = 0;
		Set<String> uniqueIDs = new HashSet<String>();

		BufferedReader input;
		int duplicates = 0;
		try {
			input = new BufferedReader(new FileReader(testPath));
			String line = input.readLine();
			while (line != null) {
				String[] str = line.split("\t");
				if (line.contains("Gene ID") || str[6].equals("absent")) {
					line = input.readLine();
					continue;
				}
				numExpressions++;
				if (!uniqueIDs.contains(str[0])) {
					uniqueIDs.add(str[0]);
				} else {
					duplicates++;
				}
				line = input.readLine();
			}
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertEquals("Number of expressions match", owlAxiomSet.size(), numExpressions * 7 - duplicates);
	}

	/**
	 * Checks that each expression (beginning with
	 * "http://purl.org/phenoscape/uuid/") occurs 1x within a Declaration 1x
	 * within a ClassAssertion 2x within an ObjectPropertyAssertion
	 */
	@Test
	public void testAccuracy() {
		List<OWLAxiom> setToSort = new ArrayList<OWLAxiom>(owlAxiomSet);
		Collections.sort(setToSort);

		Set<String> declaration = new HashSet<String>();
		Set<String> classAssertion = new HashSet<String>();
		Map<String, Integer> objectPropertyAssertion = new HashMap<String, Integer>();

		for (OWLAxiom axiom : setToSort) {
			String axiomStr = axiom.toString();
			if (axiomStr.contains(DECLARATION_STR)) {
				if (axiomStr.contains(expressionPrefix)) {
					int start = axiomStr.indexOf("<");
					int end = axiomStr.indexOf(">");
					String expression = axiomStr.substring(start + 1, end);
					assertTrue(!declaration.contains(expression));
					declaration.add(expression);
				}
			} else if (axiomStr.contains(CLASSASSERTION_STR)) {
				int prefixStart = axiomStr.indexOf(expressionPrefix);
				int end = axiomStr.indexOf(">", prefixStart);
				String expression = axiomStr.substring(prefixStart, end);
				assertTrue(!classAssertion.contains(expression));
				classAssertion.add(expression);
			} else if (axiomStr.contains(OBJECTPROPERTYASSERTION_STR)) {
				int prefixStart = axiomStr.indexOf(expressionPrefix);
				int end = axiomStr.indexOf(">", prefixStart);
				String expression = axiomStr.substring(prefixStart, end);
				if (!objectPropertyAssertion.containsKey(expression))
					objectPropertyAssertion.put(expression, 1);
				else {
					objectPropertyAssertion.put(expression, objectPropertyAssertion.get(expression) + 1);
				}
			}
		}
		// check objectPropertyAssertion
		for (String obj : objectPropertyAssertion.keySet()) {
			assertTrue(objectPropertyAssertion.get(obj) == 2);
		}
	}

}
