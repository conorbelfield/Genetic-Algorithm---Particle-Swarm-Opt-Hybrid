/*Ryan St. Pierre
 * John Ahn
 * Conor Belfield
 * 
 * This file runs the code and
 * was implemented for data collection
 * The jar file is for use in outputting
 * to the excel spreadsheet		
 */
		

import java.util.ArrayList;
import java.util.Arrays;
import jxl.Workbook;
import java.io.File;
import java.*;
import java.io.IOException;
import jxl.write.*;
import jxl.write.Number;

public class Playground {

	private static int DIM = 30;
	private static int MAX_ITERATIONS = 10000;

	private static double[] mutProbs = new double[] { 0.2 };
	private static double[] crossProbs = new double[] { 1.0 };
	private static int[] numParticles = new int[] {16, 49, 64, 144};
	private static String[] nbhdTypes = new String[] { "ra", "ri", "vn"};
	private static String[] selection = new String[] { /*"none", "rs", "ts", "bs"*/ };
	private static String[] crossMethods = new String[] {"1c", "uc" };
	private static int[] functionNums = new int[] { 0, 1, 2, 3, 4 };
	private static boolean DATA_COLLECTION = true;
	private static int counter = 0;
	private static double[] cutRatios = new double[] {0.0, 0.05, 0.1, 0.2, 0.5, 1.0 };

	public static void main(String[] args) {

		//loop that would run PBIL while printing out some data
//		PBIL pbil = new PBIL(mutProbs[0], crossProbs[0],
//				numParticles[1], DIM, MAX_ITERATIONS,
//				nbhdTypes[0], crossMethods[0], functionNums[0], selection[0]);
//		
//		double[][] trialStat = pbil.solvePBIL();
//		for(int i = 0; i < trialStat.length; i++) {
//			System.out.println("new stats");
//			for(int j = 0; j < trialStat[i].length; j++) {
//				System.out.println(trialStat[i][j]);
//			}
//		}
		
		//extensive data collection loop for hybrids,
		//which was modified when collecting different data types
		if(DATA_COLLECTION) {
			try {
				String fileName = "Data.xls";
				WritableWorkbook workbook = Workbook.createWorkbook(new File(fileName));


				for (int h = 0; h < cutRatios.length; h++) {
					WritableSheet sheet = workbook.createSheet("Sheet" + ++counter, 0);
				for(int a = 0; a < mutProbs.length; a++) { //mutProbs loop
					for(int b = 0; b < crossProbs.length; b++) { //crossProbs
						for(int c = 0; c < numParticles.length; c++) { //numParticles
							for(int d = 0; d < nbhdTypes.length; d++) { //nbhdTypes
									for (int f = 0; f < crossMethods.length; f++) { //crossMethods
										
										Label label = new Label(0 + 6 * c,0, "CrossMethod: " + crossMethods[f] + 
												". Nbhd: " + nbhdTypes[d] + ". NumParticles: " + numParticles[c] + ". Crossprob: " + crossProbs[b] + ". MutProb: " + mutProbs[a]);
										
										sheet.addCell(label);
										for (int g = 0; g < functionNums.length; g++) { //functionNums
											
											Label function = new Label(g + 6 * f, 3, "Function: " + g);
											sheet.addCell(function);
											
											//Hybrid call
											Hybrid hybrid = new Hybrid( cutRatios[h],  mutProbs[a],  crossProbs[b],
													 numParticles[c],  DIM,  MAX_ITERATIONS,
													 nbhdTypes[d],  crossMethods[f],  "none");
											
											//running the  hybrid here
											double[][] trialStats = hybrid.trial(g); // Doing the test
											//Number number = new Number(1,1, trialStats[0][0]);
											//sheet.addCell(number);
											Label bestIter = new Label(2 + 6 * f,0, "gBest Iteration");
											Number bestItera = new Number(2 + 6 *f,1, trialStats[0][3]);
											sheet.addCell(bestIter);
											sheet.addCell(bestItera);
											Label gBest = new Label(3 + 6 * f,0, "gBest");
											sheet.addCell(gBest);
											Number glBest = new Number(3 + 6 * f,1, trialStats[0][0]);
											sheet.addCell(glBest);
											for (int z = 0; z < trialStats[1].length; z++){
												Number curr = new Number(g + 6 * f, 4+z, trialStats[1][z]);
												sheet.addCell(curr);
												
											}
											Label runTime = new Label(g + 6  * f, 4 + trialStats[1].length, "Run Time:");
											sheet.addCell(runTime);
											Number run = new Number(g + 6  * f, 4 + trialStats[1].length + 1, trialStats[0][4]);
											sheet.addCell(run);

										}
									}
								}
							}
						}
					}
				}
		workbook.write();
		workbook.close();
		
		} catch(WriteException e) {
			System.out.println("Write Exception");
		}
		catch (IOException e) {
		System.out.println("IO Exception");
		}
	}
}
}
