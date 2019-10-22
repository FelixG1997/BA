package MAIN;

import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {

		// 1D-case
		if (Constants.is1D) {

			// generate 1D Input
			if (Constants.generateNewInput) {
				GenerateInput input = new GenerateInput();
				String inputFile = input.generateFile();
				SolveOneDim solve1D = new SolveOneDim(inputFile);
				System.out.println(solve1D.helvigApproach());

			} else {
				String inputFile = "1D_0.txt";
				SolveOneDim solve1D = new SolveOneDim(inputFile);
				System.out.println(solve1D.helvigApproach());
			}
		}

		// 2-orth.-axis-case
		else {
			// generate 1D Input
			if (Constants.generateNewInput) {
				GenerateInput input = new GenerateInput();
				String inputFile = input.generateFile();
				SolveTwoOrthogonalAxes solve2OA = new SolveTwoOrthogonalAxes(inputFile);
				System.out.println(solve2OA.prioApproach());

			} else {
				String inputFile = "2OA_1.txt";
				SolveTwoOrthogonalAxes solve2OA = new SolveTwoOrthogonalAxes(inputFile);
				System.out.println(solve2OA.prioApproach());
			}
		}

	}
}