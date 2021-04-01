package bptree;

import java.util.ArrayList;
import java.util.Collections;

public class Node {
	
	protected Node parent;
	protected int maxSize;
	protected ArrayList<Integer> keys;
	
	public Node(int d) {//d는 degree
		parent = null;
		maxSize = d - 1;
		keys = new ArrayList<Integer>(maxSize);
	}
	
	public void setParent(Node p) {
		parent = p;
	}
	
	public Node getParent()
	{
		return parent;
	}
	
	protected Node findRoot() {
		Node node = this;
		while(node.parent != null) {
			node = node.parent;
		}
		return node;
	}
	
	protected void insertKey(int key, int val) {		
		if(keys.size() < maxSize) {//not full to insert
			keys.add(key);
			Collections.sort(keys);
		}
		else {//full
			((LeafNode)this).split(key, val);
		}
	}
	
	protected void changeParent_i(int key, Node right) {//parnet가 변경되는경우, right는 this(leaf)의 오른쪽
		if(parent == null) {
			IndexNode newparent = new IndexNode(maxSize+1);
			
			newparent.keys.add(key);
			newparent.children.add(this);
			newparent.children.add(right);
			
			this.setParent(newparent);
			right.setParent(newparent);
		}
		else if(parent.keys.size() == maxSize) {//parent : full
			((IndexNode)parent).split(key, this, right);
		}
		else {//parent: not full
			this.parent.keys.add(key);
			Collections.sort(this.parent.keys);
			((IndexNode)this.parent).children.add(this.parent.keys.indexOf(key) + 1, right);
			right.setParent(this.parent);
		}
	}	
}
