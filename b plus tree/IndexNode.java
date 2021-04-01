package bptree;

import java.util.ArrayList;
import java.util.Collections;

public class IndexNode extends Node{
	
	public ArrayList<Node> children;
	
	public IndexNode(int d) {
		super(d);
		children = new ArrayList<Node>(d);
	}
	
	protected void split(int key, Node left, Node right) {
		int cur_split, cur_insert, mid;
		if(maxSize % 2 == 0) {
			cur_split = maxSize / 2;
		}
		else {
			cur_split = (maxSize + 1) / 2;
		}
		
		this.keys.add(key);
		Collections.sort(this.keys);
		cur_insert = this.keys.indexOf(key);
		
		mid = this.keys.remove(cur_split);
		
		IndexNode newright = new IndexNode(maxSize + 1);
		int size = this.keys.size();
		for(int i = cur_split; i < size; i++) {
			newright.keys.add(this.keys.get(i));
		}
		
		this.keys.removeIf(k -> (keys.indexOf(k) >= cur_split));
		
		if(cur_insert < cur_split) {
			left.setParent(this);
			right.setParent(this);
			
			int i = newright.keys.size();
			int j = this.children.size() - 1;
			while(i >= 0) {
				newright.children.add(0, this.children.get(j));
				this.children.get(j).setParent(newright);
				this.children.remove(j);
				j--;
				i--;
			}
			
			this.children.add(cur_insert + 1, right);
		}
		else if(cur_insert > cur_split) {
			left.setParent(newright);
			right.setParent(newright);
	
			for(int i=this.keys.size() + 1; i<this.children.size(); i++) {
				if(i == cur_insert) {
					continue;
				}
				newright.children.add(this.children.get(i));
				this.children.get(i).setParent(newright);
			}
			newright.children.add(cur_insert - this.keys.size() - 1, left);
			newright.children.add(cur_insert - this.keys.size(), right);
			
			this.children.removeIf(k -> (this.children.indexOf(k) > this.keys.size()));
			
		}
		else {
			left.setParent(this);
			right.setParent(newright);
			
			for(int i=this.keys.size()+1; i<this.children.size(); i++) {
				newright.children.add(this.children.get(i));
				this.children.get(i).setParent(newright);
			}
			newright.children.add(0, right);
			
			this.children.removeIf(k -> (this.children.indexOf(k) > this.keys.size()));
		}
		
		this.changeParent_i(mid, newright);
	}
}
