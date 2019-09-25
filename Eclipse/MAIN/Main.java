package MAIN;

import java.io.IOException;

import GEN_INPUT.OneDim;

public class Main {

	public static void main(String[] args) throws IOException {
		
//		Main m = new Main();

		// generate 1D Input
		if(CONSTANTS.OneDimConstants.createNew1DFile) {
			OneDim oneDim = new OneDim();
			oneDim.generateFile();
		}
		
		String inputFile = "1D.txt";
		SolveOneDim solve1D = new SolveOneDim(inputFile);
		
		System.out.println(solve1D.helvigApproach());
				
	}
	
	
	

}
