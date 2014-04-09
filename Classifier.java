package hw5;

import java.util.*;

public class Classifier {
	
	static ArrayList<Sample> allSamples;
	
	public static void main(String[] str) {
		initialize();
		//decisionTreeClassifier();
		//randomForestClassifier();
		crossValicationDecisionTree(allSamples);
		crossValicationRandomForest(allSamples);
	}
	
	static void initialize() {
		allSamples = Parser.parse("/Users/bonghyunkim/Desktop/Xtrain.txt", "/Users/bonghyunkim/Desktop/Ytrain.txt");
	}
	
	static void crossValicationDecisionTree(ArrayList<Sample> allSamples) {
		System.out.println("DecisitonTree");
		int[] leafSizes = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,50,100};
		double[] maxTrainEntrs = {0.001, 0.01, 0.02, 0.05, 0.1, 0.2, 0.3, 0.4, 0.5};
		int[] minDepths = {1,2,3,4,5,6,7,8,9,10};
		
		for (int leafSize : leafSizes) {
			for (double maxTrainEntr : maxTrainEntrs) {
				for (int minDepth : minDepths) {
					Collections.shuffle(allSamples);
					ArrayList<Double> errorRates = new ArrayList<Double>();
					for (int i = 0; i < 10; i++) {
						ArrayList<Sample> trainSample = new ArrayList<Sample>();
						ArrayList<Sample> testSample = new ArrayList<Sample>();
						
						for (int j = 0; j < 345; j++) {
							testSample.add(allSamples.get(j));
						}
						for (int k = 345; k < 3450; k++) {
							trainSample.add(allSamples.get(k));
						}
						
						DecisionTree decisionTree = new DecisionTree(trainSample, leafSize, maxTrainEntr, minDepth, 1);
						decisionTree.classifyAll(testSample);
						double errorRate = getErrorRate(testSample);
						errorRates.add(errorRate);
						
						for (int m = 0; m < 345; m++) {
							allSamples.add(allSamples.remove(0));
						}
					}

					System.out.println(leafSize + ", " + maxTrainEntr + ", " + minDepth + ", " + getAvgErrorRate(errorRates));
					
				}
			}
		}
		
	}
	
	static void crossValicationRandomForest(ArrayList<Sample> allSamples) {
		System.out.println("Random Forest");
		int[] numTrees = {50,80,100};
		double[] numSubSampleRatios = {0.7, 0.75, 0.8, 0.85, 0.9, 0.95};
		double[] numFeaturesRatios = {0.001, 0.01, 0.02, 0.05, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6};
		
		for (int numTree : numTrees) {
			for (double numSubSampleRatio : numSubSampleRatios) {
				for (double numFeaturesRatio : numFeaturesRatios) {
					Collections.shuffle(allSamples);
					ArrayList<Double> errorRates = new ArrayList<Double>();
					for (int i = 0; i < 10; i++) {
						ArrayList<Sample> trainSample = new ArrayList<Sample>();
						ArrayList<Sample> testSample = new ArrayList<Sample>();
						
						for (int j = 0; j < 345; j++) {
							testSample.add(allSamples.get(j));
						}
						for (int k = 345; k < 3450; k++) {
							trainSample.add(allSamples.get(k));
						}
						
						RandomForest randomForsest = new RandomForest(trainSample, numTree, numSubSampleRatio, numFeaturesRatio);
						randomForsest.classifyAll(testSample);
						double errorRate = getErrorRate(testSample);
						errorRates.add(errorRate);
						
						for (int m = 0; m < 345; m++) {
							allSamples.add(allSamples.remove(0));
						}
					}
					System.out.println(numTree + ", " + numSubSampleRatio + ", " + numFeaturesRatio + ", " + getAvgErrorRate(errorRates));
				}
			}
		}
		
	}
	
	
	static double getErrorRate(ArrayList<Sample> samples) {
		int wrongClassification = 0;
		for (Sample sample : samples) {
			if (sample.getSpam() != sample.getPredictedSpam()) {
				wrongClassification ++;
			}
		}
		return (double) wrongClassification / samples.size();
	}
	
	private static double getAvgErrorRate(ArrayList<Double> errorRates) {
		Double totalError = 0.0;
		for (Double err : errorRates) {
			totalError = totalError + err;
		}
		return (double) (totalError / errorRates.size());
	}
	
}
