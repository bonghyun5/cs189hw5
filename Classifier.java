package hw5;


import java.util.*;

public class Classifier {
	
	static ArrayList<Sample> allSamples;
	
	public static void main(String[] str) {
		initialize();
		decisionTreeClassifier();
		//randomForestClassifier();
	}
	
	static void initialize() {
		//allSamples = Parser.parse("/Users/bonghyunkim/Desktop/Xtrain.txt", "/Users/bonghyunkim/Desktop/Ytrain.txt");
		allSamples = Parser.parse("/Users/yuanshizhan/Documents/George/cs189/hw5/cs189hw5/Xtrain.txt",
				"/Users/yuanshizhan/Documents/George/cs189/hw5/cs189hw5/Ytrain.txt");
		//System.out.println("code");
	}
	
	static void decisionTreeClassifier() {
		ArrayList<Double> errorRates = new ArrayList<Double>();
		//10 way crossvalidation
		for (int i = 0; i < 10; i++) {
			ArrayList<Sample> trainSample = new ArrayList<Sample>();
			ArrayList<Sample> trainTest = new ArrayList<Sample>();
			/**
			for (int j = 0; j < 3105; j++) {
				trainSample.add(allSamples.get(j));
			}
			for (int k = 3105; k < 3450; k++) {
				trainTest.add(allSamples.get(k));
			}**/
			for (int j = 0; j < 345; j++) {
				trainTest.add(allSamples.get(j));
			}
			for (int k = 345; k < 3450; k++) {
				trainSample.add(allSamples.get(k));
			}
			DecisionTree decisionTree = new DecisionTree(trainSample);
			decisionTree.classifyAll(trainTest);
			errorRates.add(getErrorRate(trainTest));
			for (int m = 0; m < 345; m++) {
				allSamples.add(allSamples.remove(0));
			}
		}
		System.out.println("List of error rates: " + errorRates);
		double totalError = 0.0;
		for (int n = 0; n < 10; n++) {
			totalError += errorRates.get(n);
		}
		System.out.println("Total error rate: " + totalError/10);
		/**
		ArrayList<Sample> trainSample = new ArrayList<Sample>();
		ArrayList<Sample> trainTest = new ArrayList<Sample>();
		for (int i = 0; i< 2475; i++) {
			trainSample.add(allSamples.get(i));
		} 
		for (int j = 2475; j < 3450; j++) {
			trainTest.add(allSamples.get(j));
		}
		DecisionTree decisionTree = new DecisionTree(trainSample);
		decisionTree.classifyAll(trainTest);
		System.out.println(getErrorRate(trainTest));
		**/
		
		/**
		DecisionTree decisionTree = new DecisionTree(allSamples);
		decisionTree.classifyAll(allSamples);
		System.out.println(getErrorRate(allSamples));
		**/
	}
	
	static void randomForestClassifier() {
		ArrayList<Sample> trainSample = new ArrayList<Sample>();
		ArrayList<Sample> trainTest = new ArrayList<Sample>();
		for (int i = 0; i< 2475; i++) {
			trainSample.add(allSamples.get(i));
		} 
		for (int j = 2475; j < 3450; j++) {
			trainTest.add(allSamples.get(j));
		}
		
		
		RandomForest randomForest = new RandomForest(trainSample.size() - 5, 10, trainSample);
		randomForest.classifyAll(trainTest);
		System.out.println(getErrorRate(trainTest));
	}
	
	private static double getErrorRate(ArrayList<Sample> samples) {
		int wrongClassification = 0;
		for (Sample sample : samples) {
			if (sample.getSpam() != sample.getPredictedSpam()) {
				wrongClassification ++;
			}
		}
		return (double)wrongClassification / samples.size();
	}
	
	
	
}
