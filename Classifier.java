package hw5;


import java.util.*;

public class Classifier {
	
	//Optimal Parameters 
	static final int DT_OPTIMAL_LEAF_SIZE = 100;
	static final double DT_OPTIMAL_TRAIN_ENTR = 0.001;
	static final int DT_OPTIMAL_DEPTH = 3;
	static final int RF_OPTIMAL_NUM_TREE = 80;
	static final double RF_OPTIMAL_SAMPLE_RATIO = 0.8;
	static final double RF_OPTIMAL_FEATURE_RATIO = 0.2;
	
	public static void main(String[] str) {
		//For Classifying dataset
		String trainDataX = "/Users/bonghyunkim/Desktop/Xtrain.txt";
		String trainDataY = "/Users/bonghyunkim/Desktop/Ytrain.txt";
		String inputFileName = "/Users/bonghyunkim/Desktop/Xtest.txt";
		String outputFileName = "/Users/bonghyunkim/Desktop/RFClassifiedBKGW1.csv";
		//classifyWithDecisionTree(trainDataX, trainDataY, inputFileName, outputFileName);
		classifyWithRandomForest(trainDataX, trainDataY, inputFileName, outputFileName);
		//classifyWithAdaBoost(trainDataX, trainDataY, inputFileName, outputFileName);
		
		//For CrossValidation
		String inputFileNameX = "/Users/bonghyunkim/Desktop/Xtrain.txt";
		String inputFileNameY = "/Users/bonghyunkim/Desktop/Ytrain.txt";
		//crossValidationDecisionTree(inputFileNameX, inputFileNameY);
		//crossValidationRandomForest(inputFileNameX, inputFileNameY);
		//crossValidationAdaBoost(inputFileNameX, inputFileNameY);
	}
	
	static void classifyWithDecisionTree(String trainDataX, String trainDataY, String inputFileName, String outputFileName) {
		ArrayList<Sample> trainSamples = Parser.parse(trainDataX, trainDataY);
		DecisionTree decisionTree = new DecisionTree(trainSamples, DT_OPTIMAL_LEAF_SIZE, DT_OPTIMAL_TRAIN_ENTR, DT_OPTIMAL_DEPTH, 1);
		ArrayList<Sample> testSamples = Parser.parse(inputFileName);
		decisionTree.classifyAll(testSamples);
		convertAndGenerateCSV(testSamples, outputFileName);
		System.out.println("Classfied with Decision Tree");
	}
	
	static void classifyWithRandomForest(String trainDataX, String trainDataY, String inputFileName, String outputFileName) {
		ArrayList<Sample> trainSamples = Parser.parse(trainDataX, trainDataY);
		RandomForest randomForest = new RandomForest(trainSamples, RF_OPTIMAL_NUM_TREE, RF_OPTIMAL_SAMPLE_RATIO, RF_OPTIMAL_FEATURE_RATIO);
		ArrayList<Sample> testSamples = Parser.parse(inputFileName);
		randomForest.classifyAll(testSamples);
		convertAndGenerateCSV(testSamples, outputFileName);
		System.out.println("Classfied with Random Forest");
	}
	
	static void classifyWithAdaBoost(String trainDataX, String trainDataY, String inputFileName, String outputFileName) {
		ArrayList<Sample> trainSamples = Parser.parse(trainDataX, trainDataY);
		AdaBoost adaboost = new AdaBoost(trainSamples);
		ArrayList<Sample> testSamples = Parser.parse(inputFileName);
		adaboost.classifyAll(testSamples);
		convertAndGenerateCSV(testSamples, outputFileName);
		System.out.println("Classfied with AdaBoost");
	}
	
	static void crossValidationDecisionTree(String inputFileNameX, String inputFileNameY) {
		System.out.println("DecisitonTree");
		ArrayList<Sample> allSamples = Parser.parse(inputFileNameX, inputFileNameY);
		int[] leafSizes = {100};
		double[] maxTrainEntrs = {0.01, 0.001, 0.0001};
		int[] minDepths = {3};
		
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
	
	static void crossValidationRandomForest(String inputFileNameX, String inputFileNameY) {
		ArrayList<Sample> allSamples = Parser.parse(inputFileNameX, inputFileNameY);
		System.out.println("Random Forest");
		int[] numTrees = {80,100};
		double[] numSubSampleRatios = {0.8, 0.85};
		double[] numFeaturesRatios = {0.2, 0.3};
		
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
	
	static void crossValidationAdaBoost(String inputFileNameX, String inputFileNameY) {
		ArrayList<Sample> allSamples = Parser.parse(inputFileNameX, inputFileNameY);
		System.out.println("AdaBoost");
		
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
						
			AdaBoost adaBoost = new AdaBoost(trainSample);
			adaBoost.classifyAll(testSample);
			double errorRate = getErrorRate(testSample);
			errorRates.add(errorRate);
				
			for (int m = 0; m < 345; m++) {
				allSamples.add(allSamples.remove(0));
			}
		}
		System.out.println(getAvgErrorRate(errorRates));
	}
	
	private static double getErrorRate(ArrayList<Sample> samples) {
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
	
	private static void convertAndGenerateCSV(ArrayList<Sample> samples, String outputFileName) {
		ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
		int i = 1;
		for (Sample sample : samples) {
			int predicted = sample.getPredictedSpam();
			ArrayList<String> row = new ArrayList<String>();
			row.add(Integer.toString(i));
			row.add(Integer.toString(predicted));
			data.add(row);
			i++;
		}
		CSVGenerator.generateCSV(outputFileName, data);
	}
	
}
