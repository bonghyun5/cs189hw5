package hw5;

import java.util.*;

public class Classifier {
	
	static ArrayList<Sample> allSamples;
	
	public static void main(String[] str) {
		initialize();
		//decisionTreeClassifier();
		randomForestClassifier();
	}
	
	static void initialize() {
		allSamples = Parser.parse("/Users/bonghyunkim/Desktop/Xtrain.txt", "/Users/bonghyunkim/Desktop/Ytrain.txt");
	}
	
	static void decisionTreeClassifier() {
		DecisionTree decisionTree = new DecisionTree(allSamples);
		decisionTree.classifyAll(allSamples);
		System.out.println(getErrorRate(allSamples));
	}
	
	static void randomForestClassifier() {
		RandomForest randomForest = new RandomForest(allSamples.size() - 5, 10, allSamples);
		randomForest.classifyAll(allSamples);
		System.out.println(getErrorRate(allSamples));
	}
	
	private static double getErrorRate(ArrayList<Sample> samples) {
		int wrongClassification = 0;
		for (Sample sample : samples) {
			if (sample.getSpam() != sample.getPredictedSpam()) {
				wrongClassification ++;
			}
		}
		return wrongClassification / samples.size();
	}
	
	
	
}
