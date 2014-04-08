package hw5;


import java.util.*;

public class Sample implements Comparable<Sample> {
	
	//list of attributes
	private ArrayList<Double> attr;
	//1 if spam, 0 if not
	private int spam;
	//sort index needed when iterating through, sorting based on which attribute we're looking at
	private int sortIndex;
	
	private int predictedSpam;
	
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
	
	public void setSpam(int i) {
		this.spam = i;
	}
	public int getSpam() {
		return spam;
	}
	public ArrayList<Double> getAttr() {
		return attr;
	}
	public void setSortIndex(int i) {
		sortIndex = i;
	}
	
	public void setPredictedSpam(int i) {
		this.predictedSpam = i;
	}
	
	public int getPredictedSpam() {
		return this.predictedSpam;
	}
	
	public String toString() {
		return attr.toString() + " y: " + spam + "\n";
		
	}

}


