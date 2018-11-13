package fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.qLearning;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

public abstract class ExpectedValue {
	
	/* An estimation of the average gain in a given GameState (collection of features) if a given action (output) is made.
	 * This estimation is updated whenever a decision is taken according to the q-learning formula (a centroid between the actual
	 * gain and the former estimated one, weighted by the learning rate).
	 * The estimated values are stored in the matrix, and saved in the matrixFileName file.
	 */
	
	private String matrixFileName;
	private static final float LEARNING_RATE = (float) 0.1;
	private float[][] matrix;
	private int nbOfStates;
	private int nbOfOutputs;
	
	public ExpectedValue(String fileName, int nbOfStates, int nbOfOutputs) throws ClassNotFoundException, IOException {
		matrixFileName = fileName;
		this.nbOfStates = nbOfStates;
		this.nbOfOutputs = nbOfOutputs;
		init();
	}
	
	public void init() throws ClassNotFoundException, IOException {
		
		if(matrix == null) {
			try {
				read();
			} catch(Exception e) {
				System.err.println("Reset matrix.");
				reset();
			}
		} if(matrix[0].length != nbOfOutputs || matrix.length != nbOfStates) {
			reset();
		}
	}
		
	public void reset() throws IOException {
		matrix = new float[nbOfStates][nbOfOutputs];
		write();
	}
	
	public void read() throws FileNotFoundException, IOException, ClassNotFoundException {
		
		System.out.println("Expected value : read");
		
		matrix = new float[nbOfStates][nbOfOutputs];
		ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(matrixFileName));
		matrix = (float[][])inputStream.readObject();
		try {
			inputStream.close();
		} catch(Exception e) {}
	}
	
	public void write() throws IOException {
		
		System.out.println("Expected value : write");
		
		File matrixFile = new File(matrixFileName);
		matrixFile.createNewFile();
		
		ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(matrixFile));
		outputStream.writeObject(matrix);
		try {
			outputStream.close();
		} catch(Exception e) {}
	}
	
	public float getValue(GameState state, int action) {
		int stateIndex = state.stateIndex();
		return matrix[stateIndex][action];
	}
	
	public void updateValue(GameState state, int action, int value) throws IOException, ClassNotFoundException {
		int stateIndex = state.stateIndex();
		float currentValue = matrix[stateIndex][action];
		matrix[stateIndex][action] = currentValue + LEARNING_RATE * (value - currentValue);
	}
	
	//Returns the action with the highest estimated gain.
	public int bestAction(GameState state) {
		int bestAction = 0;
		float bestValue = getValue(state, 0);
		
		for(int i = 1; i < nbOfOutputs; i++) {
			float value = getValue(state, i);
			if(value > bestValue) {
				bestAction = i;
				bestValue = value;
			}
		}
		
		return bestAction;
	}
	
	protected void nonStaticDisplayMatrix() throws FileNotFoundException, ClassNotFoundException, IOException {
		System.out.println(Arrays.deepToString(matrix));
	}
}