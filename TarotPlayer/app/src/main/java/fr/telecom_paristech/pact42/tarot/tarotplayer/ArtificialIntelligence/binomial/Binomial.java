/*
 * Copyright (c) 2018. - Groupe 1PACT 42 - Projet HALTarot
 */

package fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.binomial;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Binomial {
	
	/* A simple dynamic binomial coefficients implementation.
	 * The coefficients are stored in the array coefficients and saved in the file fileName for
	 * future usage.
	 * To get a coefficient, use Binomial.coefficient(n, p).
	 * The complexity is most likely constant for small values of (n, p). 
	 */
	
	private static int[][] coefficients = {{1}};
	private final static String fileName = "IA/binomial/coefficients";
	
	static {
		try {
			read();
		} catch (Exception e) {
			coefficients = new int[1][1];
			try {
				write();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	public static int coefficient(int n, int p) {
		int maxN = coefficients.length - 1;
		int maxP = coefficients[0].length - 1;
		
		//If the matrix has to be enlarged
		if(n > maxN || p > maxP) {
			coefficients = newMatrix(coefficients, n, p);
			try {
				write();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(p < 0 || n < 0 || p > n) //p < 0 and n < 0 in case of stupid user :)
			return 0;
		
		if(n == 0 || p == n) {
			if(coefficients[n][p] == 0) {
				coefficients[n][p] = 1;
				try {
					write();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return 1;
		}
	
		if(coefficients[n][p] == 0) {
			coefficients[n][p] = coefficient(n - 1, p) + coefficient(n - 1, p - 1);
			try {
				write();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return coefficients[n][p];
		} else {
			return coefficients[n][p];
		}
	}
	
	//Copies matrix in a larger newMaxN * newMaxP matrix.
	private static int[][] newMatrix(int[][] matrix, int newMaxN, int newMaxP) {
		
		int maxN = matrix.length - 1;
		int maxP = matrix[0].length - 1;
		int[][] newMatrix = new int[newMaxN + 1][newMaxP + 1];
		
		for(int n = 0; n <= maxN; n ++) {
			for(int p = 0; p <= maxP; p ++) {
				newMatrix[n][p] = matrix[n][p];
			}
		}
		
		return newMatrix;
	}
	
	//Reads the file to edit the matrix
	private static void read() throws FileNotFoundException, IOException, ClassNotFoundException {
		
		System.out.println("Binomial : read");
		
		ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(fileName));
		coefficients = (int[][])inputStream.readObject();
		try {
			inputStream.close();
		} catch(Exception e) {}
	}
	
	//Writes the matrix to the file
	private static void write() throws IOException {
		
		System.out.println("Binomial : write");
		
		File matrixFile = new File(fileName);
		matrixFile.createNewFile();
		
		ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(matrixFile));
		outputStream.writeObject(coefficients);
		try {
			outputStream.close();
		} catch(Exception e) {}
	}

}
