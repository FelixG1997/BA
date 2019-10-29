package MAIN;

import java.io.IOException;
import java.util.ArrayList;

public class Main {

	public static void main(String[] args) throws IOException {

		String inputFile;
		
		// 1D-case
		if (Constants.is1D) {

			// generate 1D Input
			if (Constants.generateNewInput) {
				GenerateInput input = new GenerateInput();
				inputFile = input.generateFile();
			} else {
				inputFile = "1D_0.txt";
			}
			SolveOneDim solve1D = new SolveOneDim(inputFile);
			System.out.println(solve1D.helvigApproach());
		}

		// 2-orth.-axis-case
		else {
			// generate 1D Input
			if (Constants.generateNewInput) {
				GenerateInput input = new GenerateInput();
				inputFile = input.generateFile();
			} else {
				inputFile = "2OA_0.txt";
			}
			SolveTwoOrthogonalAxes solve2OA = new SolveTwoOrthogonalAxes(inputFile);
			// weights for calculating the priority of a target
			int a = 100;
			int b = 80;
			int c = 2;
			int d = 1;
			solve2OA.setPriorityWeights(a, b, c, d);
			ArrayList<Target> OUTPUT = solve2OA.prioApproach();
			for(Target t : OUTPUT) {
				System.out.println(t);
			}
		}

	}
}