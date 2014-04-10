package hw5;

import java.util.*;

/*
 * Class representing sample data.
 */
public class Sample implements Comparable<Sample> {
	
	//List of attributes
	private ArrayList<Double> attr;
	//1 if spam, 0 if not
	private int spam;
	//Sort index needed when iterating through, sorting based on which attribute we're looking at
	private int sortIndex;
	//1 if spam, 0 if not
	private int predictedSpam;
	private double weight;
	
	public Sample(ArrayList<Double> attr) {
		this.attr = attr;
	}
	
	@Override
	public int compareTo(Sample arg0) {
		if ((attr.get(sortIndex) - arg0.attr.get(sortIndex)) == 0) {
			return 0;
		} else if ((attr.get(sortIndex) - arg0.attr.get(sortIndex)) < 0) {
			return -1;
		} else {
			return 1;
		}
	}
	
	void setSpam(int i) {
		this.spam = i;
	}
	
	int getSpam() {
		return spam;
	}
	
	ArrayList<Double> getAttr() {
		return attr;
	}
	
	void setSortIndex(int i) {
		sortIndex = i;
	}
	
	void setPredictedSpam(int i) {
		this.predictedSpam = i;
	}
	
	int getPredictedSpam() {
		return this.predictedSpam;
	}
	
	void setWeight(double weight) {
		this.weight = weight;
	}
	
	double getWeight() {
		return this.weight;
	}
	
	public String toString() {
		return attr.toString() + " y: " + spam + "\n";
	}
	
}