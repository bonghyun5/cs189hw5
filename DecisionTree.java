package hw5;

import java.util.*;

public class DecisionTree {
	
	private static ArrayList<Sample> allSamples;
	Node root;
	
	DecisionTree(ArrayList<Sample> allSamples) {
		root = buildTree(new Node(), allSamples, 0);
	}
	
	//iterates through all attributes, and determines information gain for each split
	//choose attr and split which provides max information gain
	Node buildTree(Node root, ArrayList<Sample> samples, int depth) {
		double trainEntr = findEntropy(samples);
		//if train is size one, then stop, we reached a leaf!
		if ((samples.size() <= 10)||(trainEntr<.2)||(depth>200)) {
			root.setIsLeaf(true);
			root.setSpam(spamOrNot(samples));
			return root;
		}
		
		//max information gain encountered in entire loop
		double maxIg = 0.0;
		//attribute where max information gain is found
		int maxAttr = 0;
		//the split value where max ig is found
		double maxAttrSplit = 0.0;
		//the left and right sides where max ig is found
		ArrayList<Sample> maxLeft = new ArrayList<Sample>();
		ArrayList<Sample> maxRight = new ArrayList<Sample>();
		
		for (int i = 0; i < 57; i++) {
			for (int ii = 0; ii < samples.size(); ii++) {
				samples.get(ii).setSortIndex(i);
			}			
			Collections.sort(samples);
			//need this lastSpam value to skip splits that are between two points of same class
			int lastSpam = samples.get(0).getSpam();
			//double lastSplit = samples.get(0).getAttr().get(i);

			//creating left and right ArrayLists
			ArrayList<Sample> left = new ArrayList<Sample>();
			ArrayList<Sample> right = new ArrayList<Sample>();
			left.add(samples.get(0));

			for (int j = 1; j < samples.size(); j++) {
				right.add(samples.get(j));
			}

			//loop through all samples, and find info gain, see if max
			for (int k = 1; k < samples.size(); k++) {
				Sample curr = samples.get(k);
				if (curr.getSpam() == lastSpam) {
					left.add(right.remove(0));
					continue;
				} else {
					//halfway point between this and last split
					//^incorrect, bug, because split needs to be here! not halfway
					double currSplit = curr.getAttr().get(i);
					int totalSize = left.size() + right.size();
					double entrLeft = findEntropy(left);
					double entrRight = findEntropy(right);
	
					//information gain equation
					double ig = trainEntr - (((double)left.size()/totalSize)*entrLeft
							+ ((double)right.size()/totalSize)*entrRight);
					//if this is max information gain, update
					if (ig > maxIg) {
						maxIg = ig;	
						maxAttr = i;
						maxAttrSplit = currSplit;
						maxLeft = new ArrayList<Sample>(left);
						maxRight = new ArrayList<Sample>(right);
					}
					//lastSplit = currSplit;
					lastSpam = curr.getSpam();
					left.add(right.remove(0));
				}	
			}
		}
		
		root.setAttr(maxAttr);
		root.setSplit(maxAttrSplit);
		Node leftChild = new Node();
		root.setLeftChild(buildTree(leftChild, maxLeft, depth + 1));
		leftChild.setParent(root);
		Node rightChild = new Node();
		root.setRightChild(buildTree(rightChild, maxRight, depth + 1));
		rightChild.setParent(root);
		return root;
	}
	
	void classifyAll(ArrayList<Sample> samples) {
		for (Sample sample : samples) {
			sample.setPredictedSpam(classify(sample));
		}
	}
	
	int classify(Sample sample) {
		Node currentNode = root;
		while (!currentNode.isLeaf()) {
			if (sample.getAttr().get(currentNode.getAttr()) < currentNode.getSplit()) {
				currentNode = currentNode.getLeftChild();
			} else {
				currentNode = currentNode.getRightChild();
			}
		}
		return currentNode.getSpam();
	}
	
	
	/**
	 * findEntropy returns the entropy of this set of vectors
	 * @param in
	 * @return the value of entropy for this set of vectors
	 */
	protected static double findEntropy(ArrayList<Sample> in) {
		double prob = findProb(in);
		return -1.0*(prob*(log2(prob)) + (1-prob)*log2(1-prob));
	}
	
	private static double log2(double in) {
		if (in==0.0) {
			return 0.0;
		} else {
			return Math.log(in)/Math.log(2);
		}
	}
	
	/**
	 * findProb returns the probability of spam given these samples
	 * @param in takes in list of points
	 * @return the probability of spam given these points
	 */
	private static double findProb(ArrayList<Sample> in) {
		int y = 0;
		for (int i = 0; i< in.size(); i++) {
			y += in.get(i).getSpam();
		}
		return ((double)y)/in.size();
	}
	
	private int spamOrNot(ArrayList<Sample> samples) {
		int zero = 0;
		int one = 1;
		for (int i = 0; i < samples.size(); i++) {
			if (samples.get(i).getSpam() == 0) {
				zero++;
			} else {
				one++;
			}
		}
		if (one > zero) {
			return 1;
		} else {
			return 0;
		}
	}
	
}
