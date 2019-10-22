package MAIN;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

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
		
		if(Constants.is1D) {
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
		writer.write(Constants.n + "\n");
		
		Random rand = new Random();
		
		// pPos (origin) - pursuer position
		// purPos = rand.nextInt(Constants.limitXCoord+1);
		// Purseuer position currently fixed
		if(Constants.is1D) {
			writer.write(Constants.purPos + "\n");
		} else {
			String tmp = Constants.pursuerStartsOnLeftRightAxis? "0":"1";
			writer.write(Constants.purPos + " " + tmp + "\n");
		}
		
		
		// pV - pursuer velocity / vmax
		writer.write(Constants.vMax + "\n");
		ArrayList<Integer> currentPositions = new ArrayList<>(); 
		// positions & velocities
		String pos = "";
		String vel = "";
		String axis = "";
				
		for(int i=0; i<Constants.n; i++) {
			int tmp = rand.nextInt(Constants.limitCoord+1);
			
			if(!currentPositions.contains(tmp) && tmp!=Constants.purPos) {
				pos += String.valueOf(rand.nextInt(2)==0? tmp : tmp*(-1));
				currentPositions.add(tmp);
				
				tmp = rand.nextInt((int)Constants.vMax-1)+1;
				tmp = rand.nextInt(2)==0? tmp : tmp*(-1);
				vel += String.valueOf(tmp);
				
				tmp = rand.nextInt(2)==0? 0 : 1;
				axis += String.valueOf(tmp);
				
				if(i!=Constants.n-1) {
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
		if(!Constants.is1D) {
			writer.write(axis);
		}
		
		writer.close();
		return path;
		
	}

}
