package hw5;

import java.util.*;

public class AdaBoost {
	private ArrayList<Double> alphas;
	private ArrayList<DecisionTree> weakTrees;
	private ArrayList<Sample> rightClass;
	private ArrayList<Sample> wrongClass;
	
	public AdaBoost(ArrayList<Sample> s) {
		alphas = new ArrayList<Double>();
		weakTrees = new ArrayList<DecisionTree>();
		rightClass = new ArrayList<Sample>();
		wrongClass = new ArrayList<Sample>();
		buildAdaBoostTree(s);
	}
	
	public void buildAdaBoostTree(ArrayList<Sample> trainSamples) {
		for (int i = 0; i < trainSamples.size(); i++) {
			trainSamples.get(i).setWeight(1.0/trainSamples.size());
		}
		for (int j = 0; j < 10; j++) {
			// on each iteration, train on 2500 samples
			ArrayList<Sample> allCopy = new ArrayList<Sample>(trainSamples);
			ArrayList<Sample> train = new ArrayList<Sample>();
			for (int k = 0; k < 2500; k++) {
				int randIndex = sampleFromDistr(allCopy);
				train.add(allCopy.get(randIndex));
				allCopy.remove(randIndex);
			}
			DecisionTree decTree = new DecisionTree(train, Classifier.DT_OPTIMAL_LEAF_SIZE, Classifier.DT_OPTIMAL_TRAIN_ENTR, Classifier.DT_OPTIMAL_DEPTH, 1);
			weakTrees.add(decTree);
			decTree.classifyAll(trainSamples);
			
			Double errorRate = getErrorRate(trainSamples);
			
			alphas.add(.5*Math.log((1-errorRate)/errorRate));

			for (int i = 0; i < rightClass.size(); i++) {
				Sample curr = rightClass.get(i);
				curr.setWeight(Math.exp(-1.0*alphas.get(j)*curr.getWeight()));
			}
			for (int i = 0; i < wrongClass.size(); i++) {
				Sample curr = wrongClass.get(i);
				curr.setWeight(Math.exp(alphas.get(j)*curr.getWeight()));
			}

			double tempTotalWeight = 0.0;
			for (int i = 0; i < trainSamples.size(); i++) {
				tempTotalWeight += trainSamples.get(i).getWeight();
			}
			for (int i = 0; i < trainSamples.size(); i++) {
				trainSamples.get(i).setWeight(trainSamples.get(i).getWeight()/tempTotalWeight);
			}
			
		}
	}
	
	void classifyAll(ArrayList<Sample> samples) {
		for (Sample sample : samples) {
			int classified = classify(sample);
			sample.setPredictedSpam(classified);
		}
	}
	
	//Returns int for ham or spam: returns 0 or 1
	int classify(Sample s) {
		//get classification from each tree and weight according to alpha
		double sum = 0.0;
		for (int i = 0; i < weakTrees.size(); i++) {
			DecisionTree currTree = weakTrees.get(i);
			int tempC = currTree.classify(s);
			//need this nonsense because we want negative one or pos one
			int c;
			if (tempC == 1) {
				c = 1;
			} else {
				c = -1;
			}
			sum += alphas.get(i) * c;
		}
		if (sum < 0.0) {
			return 0;
		} else {
			return 1;
		}
	}
	
	//Given an ArrayList of samples, get the random index of an item with weighted probability
	int sampleFromDistr(ArrayList<Sample> in) {
		// Compute the total weight of all items together
		double totalWeight = 0.0d;
		for (Sample s : in)
		{
		    totalWeight += s.getWeight();
		}
		// Now choose a random item
		int randomIndex = in.size() - 1;
		double random = Math.random() * totalWeight;
		for (int i = 0; i < in.size(); i++)
		{
		    random -= (double)in.get(i).getWeight();
		    if (random <= 0.0d)
		    {
		        randomIndex = i;
		        break;
		    }
		}
		return randomIndex;
	}
	
	//Get error rate to calculate the error for each iteration of adaboost
	private double getErrorRate(ArrayList<Sample> samples) {
		int wrongClassification = 0;
		for (Sample sample : samples) {
			if (sample.getSpam() != sample.getPredictedSpam()) {
				wrongClassification ++;
				wrongClass.add(sample);
			} else {
				rightClass.add(sample);
			}
		}
		return (double)wrongClassification / samples.size();
	}

}

