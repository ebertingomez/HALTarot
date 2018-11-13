package fr.telecom_paristech.pact42.tarot.tarotplayer.ArtificialIntelligence.qLearning;

import java.io.FileNotFoundException;
import java.io.IOException;

public class ExpectedValueColor extends ExpectedValue {

	private static final String MATRIX_FILE_NAME = "IA/qLearning/expectedValueColor";
	private static final int NB_OF_STATES = GameStateColor.NB_OF_STATES;
	private static final int NB_OF_OUTPUTS = 2;
	
	public ExpectedValueColor() throws ClassNotFoundException, IOException {
		super(MATRIX_FILE_NAME, NB_OF_STATES, NB_OF_OUTPUTS);
	}
	
	public static void displayMatrix() throws FileNotFoundException, ClassNotFoundException, IOException {
		new ExpectedValueColor().nonStaticDisplayMatrix();
	}
}
