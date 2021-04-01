package bptree;

import java.util.Collections;//sorting keys : Cllections.sort(list);
import java.util.HashMap;

public class LeafNode extends Node{

	protected LeafNode nextNode;
	
	public LeafNode(int d) {
		super(d);
		nextNode = null;
	}
	
	public void setNextNode(LeafNode next) {
		this.nextNode = next;
	}
	
	public LeafNode getNextNode() {
		return nextNode;
	}
	
	protected void split(int key, int val) {
		int cur_split;//split location, index 0부터
		if(maxSize % 2 == 0) {
			cur_split = maxSize / 2;
		}
		else {
			cur_split = (maxSize + 1) / 2;
		}
		
		this.keys.add(key);
		Collections.sort(this.keys);
		
		//create new leaf node
		LeafNode right = new LeafNode(maxSize + 1);
		int size = keys.size();
		for(int i = cur_split; i < size; i++) {
			right.keys.add(keys.get(i));
		}
		this.keys.removeIf(k -> (keys.indexOf(k) >= cur_split));		
		
		//link
		right.setNextNode(this.getNextNode());
		this.setNextNode(right);
		
		this.changeParent_i(right.keys.get(0), right);
	}
	
	protected LeafNode borrow_node() {//빌릴 형제 노드
		return null;
	}
	
	protected void borrow(LeafNode node) {//빌리고 부모set까지하기
		
	}
	
}
