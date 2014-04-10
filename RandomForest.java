package hw5;

import java.util.*;

/*
 * Class for building RandomForest and classifying samples with them
 */
public class RandomForest {

	DecisionTree[] decisionTrees;
	
	RandomForest(ArrayList<Sample> samples, int numTrees, double numSubSampleRatio, double numFeaturesRatio) {
		decisionTrees = new DecisionTree[numTrees];
		for (int i = 0; i < numTrees; i++) {
			Collections.sort(samples);
			ArrayList<Sample> subSamples = getSubSamples(samples, numSubSampleRatio);
			DecisionTree decisionTree = new DecisionTree(subSamples, Classifier.DT_OPTIMAL_LEAF_SIZE, Classifier.DT_OPTIMAL_TRAIN_ENTR, Classifier.DT_OPTIMAL_DEPTH, numFeaturesRatio);
			decisionTrees[i] = decisionTree;
		}
	}

	void classifyAll(ArrayList<Sample> samples) {
		for (Sample sample : samples) {
			int classified = classify(sample);
			sample.setPredictedSpam(classified);
		}
	}
	
	int classify(Sample sample) {
		int[] predicted = new int[decisionTrees.length];
		for (int i = 0; i < predicted.length; i++) {
			DecisionTree tree = decisionTrees[i];
			predicted[i] = tree.classify(sample);
		}
		return majorVoting(predicted);
	}
	
	private ArrayList<Sample> getSubSamples(ArrayList<Sample> samples, double numSubSampleRatio) {
		Collections.sort(samples);
		ArrayList<Sample> subSamples = new ArrayList<Sample>();
		int numSamples = (int) (samples.size() * numSubSampleRatio);
		for (int i = 0; i < numSamples; i++) {
			subSamples.add(samples.get(i));
		}
		return subSamples; 
	}
	
	private int majorVoting(int[] predicted) {
		int numSpam = 0;
		int numNotSpam = 0;
		for (int val : predicted) {
			if (val == 1) {
				numSpam ++;
			} else {
				numNotSpam ++;
			}
		}
		return (numSpam >= numNotSpam) ? 1 : 0;
	}
	
}