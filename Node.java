package hw5;

/*
 * Class representing nodes for Decision Tree Algorithm
 */
public class Node {	
	private int attr;
	private double split;	
	private int spam;
	private Node parent;
	private Node leftChild;
	private Node rightChild;
	private boolean isLeaf;	
	
	public Node() {
		parent = null;
		leftChild = null;
		rightChild = null;
		isLeaf = false;
	}
	
	void setAttr(int attr) {
		this.attr = attr;
	}
	
	int getAttr() {
		return this.attr;
	}
	
	void setSplit(double split) {
		this.split = split;
	}
	
	double getSplit() {
		return this.split;
	}

	void setParent(Node parent) {
		this.parent = parent;
	}
	
	Node getParent() {
		return this.parent;
	}
	
	void setLeftChild(Node leftChild) {
		this.leftChild = leftChild;
	}
	
	Node getLeftChild() {
		return this.leftChild;
	}
	
	void setRightChild(Node rightChild) {
		this.rightChild = rightChild;
	}
	
	Node getRightChild() {
		return this.rightChild;
	}
	
	void setIsLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}
	
	boolean isLeaf() {
		return isLeaf;
	}
	
	void setSpam(int spam) {
		this.spam = spam;
	}
	
	int getSpam() {
		return this.spam;
	}
}
