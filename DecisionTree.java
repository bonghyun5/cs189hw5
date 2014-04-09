package hw5;

import java.util.*;

public class DecisionTree {
	
	private static ArrayList<Sample> allSamples;
	Node root;
	private final int TOTAL_NUM_FEATURES = 57;
	
	DecisionTree(ArrayList<Sample> allSamples, int leafSize, double trainThreshold, int depthThreshold, double numFeaturesRatio) {
		root = buildTree(new Node(), allSamples, 0, leafSize, trainThreshold, depthThreshold, numFeaturesRatio);
		//root = buildTreeC45(new Node(), 0, allSamples, trainThreshold, depthThreshold);
	}
	
	//iterates through all attributes, and determines information gain for each split
	//choose attr and split which provides max information gain
	Node buildTree(Node root, ArrayList<Sample> samples, int depth, int leafSize, double trainThreshold, int depthThreshold, double numFeaturesRatio) {
		double trainEntr = findEntropy(samples);
		//if train is size one, then stop, we reached a leaf!
		if ((samples.size() <= leafSize) || (trainEntr < trainThreshold) || (depth > depthThreshold)) {
			//System.out.println("at leaf at depth: " + depth + ", entropy: " + trainEntr);
			//System.out.println("sample #: " + samples.size());
			
			root.setIsLeaf(true);
			root.setSpam(spamOrNot(samples));
			//System.out.println("class: " + spamOrNot(samples));
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
		
		ArrayList<Integer> featuresToUse = getFeaturesToUse(numFeaturesRatio);
		for (int i : featuresToUse) {
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
			
			for (int j = 1; j < samples.size(); j++) {
				right.add(samples.get(j));
			}
			//left.add(right.remove(0));
			left.add(samples.get(0));
			//loop through all samples, and find info gain, see if max
			for (int k = 1; k < samples.size(); k++) {
				Sample curr = samples.get(k);
				if ((curr.getSpam() == lastSpam)&&(k!=0))  {
					if (right.size()>0) {
						left.add(right.remove(0));
					}
					continue;
				} else {
					if (k < samples.size() - 1) {
						if (curr.getAttr().get(i).equals(samples.get(k+1).getAttr().get(i))) {
							left.add(right.remove(0));
							continue;
						}
					}
				
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
					if (right.size() > 0) {
						left.add(right.remove(0));
					}
				}	
			}
		}
		
		root.setAttr(maxAttr);
		root.setSplit(maxAttrSplit);
		Node leftChild = new Node();
		root.setLeftChild(buildTree(leftChild, maxLeft, depth + 1, leafSize, trainThreshold, depthThreshold, numFeaturesRatio));
		leftChild.setParent(root);
		Node rightChild = new Node();
		root.setRightChild(buildTree(rightChild, maxRight, depth + 1, leafSize, trainThreshold, depthThreshold, numFeaturesRatio));
		rightChild.setParent(root);
		return root;
	}
	
	
	Node buildTreeC45(Node root, int depth, ArrayList<Sample> samples, double infoGainThreshold, int depthThreshold) {
		//Check BaseCase
		int allSameClass = allSameClass(samples);
		if (allSameClass == 0 || allSameClass == 1) {
			root.setIsLeaf(true);
			root.setSpam(allSameClass);
			return root;
		}
		double infoGainNow = findEntropy(samples);
		if (infoGainNow <= infoGainThreshold || depth > depthThreshold) {
			root.setIsLeaf(true);
			root.setSpam(spamOrNot(samples));
			return root;
		}
		
		double bestNormInfoGain = 0.0;
		int bestAttrNum = 0;
		double bestAttrSplit = 0.0;

		//For Each attribute a
		for (int attrNum = 0; attrNum < TOTAL_NUM_FEATURES; attrNum++) {
			//Get Best Split and split data
			double attrSplit = findAttrSplit(samples, attrNum);
			ArrayList<Sample>[] splitSamples = splitSamples(samples, attrNum, attrSplit);
			ArrayList<Sample> leftOnAttr = splitSamples[0];
			ArrayList<Sample> rightOnAttr = splitSamples[1];
			
			//Find normalized information gain ratio from this split
			double normInfoGain = getNormInfoGainOnSplit(leftOnAttr, rightOnAttr);
			if (normInfoGain > bestNormInfoGain) {
				bestNormInfoGain = normInfoGain;
				bestAttrNum = attrNum;
				bestAttrSplit = attrSplit;
			}
		}
		
		root.setAttr(bestAttrNum);
		root.setSplit(bestAttrSplit);
		ArrayList<Sample> leftOnBestAttr = new ArrayList<Sample>();
		ArrayList<Sample> rightOnBestAttr = new ArrayList<Sample>();
		Node leftChild = new Node();
		Node rightChild = new Node();
		leftChild.setParent(root);
		rightChild.setParent(root);
		root.setLeftChild(buildTreeC45(leftChild, depth + 1, leftOnBestAttr, infoGainThreshold, depthThreshold));
		root.setRightChild(buildTreeC45(rightChild, depth + 1, rightOnBestAttr, infoGainThreshold, depthThreshold));

		return root;
	}	
	
	double findAttrSplit(ArrayList<Sample> samples, int attrNum) {
		double attrSplit = 0.0;
		for (int i = 0; i < 1; i ++) {
			for (Sample sample : samples) {
				double attr = sample.getAttr().get(attrNum);
				int actualClass = sample.getSpam();
				int predictedClass = (attrSplit * attr > 0) ? 1 : 0;
				if (predictedClass != actualClass) {
					if (predictedClass == 1) {
						attrSplit = attrSplit + attr; 
					} else {
						attrSplit = attrSplit - attr;
					}
				} 
			}
		}
		return attrSplit;
	}
	
	ArrayList<Sample>[] splitSamples(ArrayList<Sample> samples, int attrNum, double attrSplit) {
		ArrayList<Sample> left = new ArrayList<Sample>();
		ArrayList<Sample> right = new ArrayList<Sample>();
		for (Sample sample : samples) {
			 double attr = sample.getAttr().get(attrNum);
			 if (attr <= attrSplit) {
				 left.add(sample);
			 } else {
				 right.add(sample);
			 }
		}
		ArrayList<Sample>[] splitSamples = new ArrayList[2];
		splitSamples[0] = left;
		splitSamples[1] = right;
		return splitSamples;
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
	
	private double getNormInfoGainOnSplit(ArrayList<Sample> left, ArrayList<Sample> right) {
		ArrayList<Sample> allSamples = new ArrayList<Sample>(left);
		allSamples.addAll(right);
		double allEntropy = findEntropy(allSamples);
		double leftEntropy = findEntropy(left);
		double rightEntropy = findEntropy(right);
		double leftNormalized = ((double)(left.size()) / (double)(allSamples.size())) * leftEntropy;
		double rightNormalized = ((double)(right.size()) / (double)(allSamples.size())) * rightEntropy;
		return allEntropy - (leftNormalized + rightNormalized); 
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
	
	//Return 0, 1, -1
	private int allSameClass(ArrayList<Sample> samples) {
		if (samples.size() < 1) {
			return -1;
		}
		int firstSampleClass = samples.get(0).getSpam();
		for (Sample sample : samples) {
			int sampleClass = sample.getSpam();
			if (firstSampleClass != sampleClass) {
				return -1;
			}
		}
		return firstSampleClass;
	}
	
	private ArrayList<Integer> getFeaturesToUse(double numFeaturesRatio) {
		ArrayList<Integer> features = new ArrayList<Integer>();
		for (int i = 0; i < TOTAL_NUM_FEATURES; i++) {
			features.add(i);
		}
		if (numFeaturesRatio == 1) {
			return features;
		} else {
			Collections.shuffle(features);
			int numsToGet = (int) (TOTAL_NUM_FEATURES * numFeaturesRatio);
			ArrayList<Integer> subFeatures = new ArrayList<Integer>();
			for (int i = 0; i < numsToGet; i++) {
				subFeatures.add(features.get(i));
			}
			return subFeatures;
		}
	}
	
}
