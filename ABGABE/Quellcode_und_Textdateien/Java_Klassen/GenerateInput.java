package MAIN;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * 
 * @author FelixGreuling
 *
 */
public class GenerateInput {

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
	public String generateFile() throws IOException {
		
		BufferedWriter writer;
		String path = "";
		
		if(Constants.IS_1D) {
			File file = new File("D:\\Uni\\eclipse_workspace_new\\BA_MTTSP");
			File[] fileArray = file.listFiles();
			int index = 0;
			for(int i = 0; i<fileArray.length-1; i++) {
				if(fileArray[i].getPath().contains("1D")) {
					index++;					
				}
			}
			path = "1D_" + index + ".txt";
			writer = new BufferedWriter(new FileWriter(path));
		} else {
			File file = new File("D:\\Uni\\eclipse_workspace_new\\BA_MTTSP");
			File[] fileArray = file.listFiles();
			int index = 0;
			for(int i = 0; i<fileArray.length-1; i++) {
				if(fileArray[i].getPath().contains("2OA")) {
					index++;					
				}		
			}
			path = "2OA_" + index + ".txt";
			writer = new BufferedWriter(new FileWriter(path));
		}
		
		// n
		writer.write(Constants.N + "\n");
		
		Random rand = new Random();
		
		// pPos (origin) - pursuer position
		// purPos = rand.nextInt(Constants.limitXCoord+1);
		// Pursuer position currently fixed
		String axisPur = "0";
		if(!Constants.IS_1D) {
			axisPur = Constants.PURSUER_POS_AXIS? "0":"1";	
		}
		writer.write(Constants.PURSUER_POS + " " + axisPur + "\n");
		
		
		// pV - pursuer velocity / vmax
		writer.write(Constants.V_PURSUER + "\n");
		ArrayList<Integer> currentPositions = new ArrayList<>(); 
		// positions & velocities
		String pos = "";
		String vel = "";
		String axis = "";
				
		for(int i=0; i<Constants.N; i++) {
			int tmp = rand.nextInt(Constants.LIMIT_COORD+1);
			
			if(!currentPositions.contains(tmp) && tmp!=Constants.PURSUER_POS) {
				pos += String.valueOf(rand.nextInt(2)==0? tmp : tmp*(-1));
				currentPositions.add(tmp);
				
				if(Constants.CONSTANT_VELOCITIES) {
					tmp = Constants.V_I_MAX;
				} else{
					tmp = rand.nextInt((int)Constants.V_I_MAX)+1;
				}
				tmp = rand.nextInt(2)==0? tmp : tmp*(-1);
				vel += String.valueOf(tmp);
				
				if(Constants.IS_1D) {
					tmp = 0;
				} else {
					tmp = rand.nextInt(2)==0? 0 : 1;
				}
				
				axis += String.valueOf(tmp);
				
				if(i!=Constants.N-1) {
					pos += " ";
					vel += " ";
					axis += " ";
				} 
				
			} else {
				i--;
			}			
		}
		
		writer.write(pos + "\n");
		writer.write(vel + "\n");
		writer.write(axis);
		
		writer.close();
		return path;
		
	}

}
