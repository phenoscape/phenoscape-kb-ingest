package org.phenoscape.kb.ingest.bgee;
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import org.junit.Test;

public class BgeeToOWLTest {
	private static final int digitAccuracy = 10;
	
	public static final String danio_rerio = "/Danio_rerio_expr_simple.tsv";
	public static final String sourceDirectory = "/source_files";
	public static final String results = "/BgeeResult.txt";

	@Test
	public void testExpectScore() {
		
		String absPath = new File("").getAbsolutePath();
		String filePath = absPath + sourceDirectory + danio_rerio;
		String filePathResults = absPath + results;
		System.out.println(filePath);
//		BgeeExpressionToOWLJava.convert(filePath);
		
		// Check that all expect scores match (based upon results printed to text files)
		int numExpressions = 0;

		BufferedReader input;
		try {
			input = new BufferedReader(new FileReader(
					filePath));
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
//			int counter = 0;
			results = new BufferedReader(new FileReader(
					filePathResults));
			String line = results.readLine();
			String[] str = line.split(",");
			length = str.length;
			results.close(); 
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertEquals("Number of expressions match", length, numExpressions * 5);

	}
	
	
}

