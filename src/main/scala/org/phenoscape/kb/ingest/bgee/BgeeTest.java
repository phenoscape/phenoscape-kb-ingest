package org.phenoscape.kb.ingest.bgee;

import java.io.File;

public class BgeeTest {
	
	public static final String danio_rerio = "/Danio_rerio_expr_simple.tsv";
	public static final String sourceDirectory = "/source_files";
	

	public static void main(String[] args){		
		String absPath = new File("").getAbsolutePath();
		String filePath = absPath + sourceDirectory + danio_rerio;
		System.out.println(filePath);
		BgeeExpressionToOWL.convert(filePath);
	}

}
