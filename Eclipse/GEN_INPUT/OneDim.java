package GEN_INPUT;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import CONSTANTS.OneDimConstants;

public class OneDim {

	// targets, geschwindigkeit, maxSpeed, PurPos 
	
	/**
	 * n - number targets
	 * pPos - pursuer position
	 * pV - pursuer velocity / vmax
	 * n x positions
	 * n x velocities
	 *  
	 * @throws IOException
	 */
	public void generateFile() throws IOException {
		
		BufferedWriter writer = new BufferedWriter(new FileWriter("1D.txt"));
		
		// n
		writer.write(OneDimConstants.n + "\n");
		
		// pPos - pursuer position
		// Purseuer position currently fixed
		Random rand = new Random();
//		int pursuerPos = rand.nextInt(CONSTANTS.Consts.limitXCoord);
		writer.write(OneDimConstants.purPos + "\n");
		
		// pV - pursuer velocity / vmax
		writer.write(OneDimConstants.vMax + "\n");
		ArrayList<Integer> currentPositions = new ArrayList<>(); 
		// positions & velocities
		String pos = "";
		String vel = "";
		
		
		
		for(int i=0; i<OneDimConstants.n; i++) {
			int tmp = rand.nextInt(OneDimConstants.limitXCoord+1);
			
//			System.out.println(i+ " " + tmp);
			
			if(!currentPositions.contains(tmp) && tmp!=OneDimConstants.purPos) {
				pos += String.valueOf(rand.nextInt(2)==0? tmp : tmp*(-1));
				currentPositions.add(tmp);
				
				tmp = rand.nextInt((int)OneDimConstants.vMax-1)+1;
				tmp = rand.nextInt(2)==0? tmp : tmp*(-1);
				vel += String.valueOf(tmp);
				
				if(i!=OneDimConstants.n-1) {
					pos += " ";
					vel += " ";
				} 
				
			} else {
				i--;
			}			
		}
		
		writer.write(pos + "\n");
		writer.write(vel);
		
		
		writer.close();
		
	}

}
