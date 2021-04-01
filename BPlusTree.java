package bptree;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.BufferedReader;

public class BPlusTree {
	
	private static int degree;
	public Node root;
	private HashMap<Integer, Integer> pairs;//key-value pairs
	
	public BPlusTree() {
		degree = 0;
		root = null;
		pairs = null;
	}
	
	public BPlusTree(int d) {
		degree = d;
		root = new LeafNode(d);
		pairs = new HashMap<Integer, Integer>();
	}
	
 	public void insertIntoTree(int key, int val) {
		if(pairs.containsKey(key) == true) {
			return;
		}
		pairs.put(key, val);
		
		if(root instanceof LeafNode) {
			if(root.keys.size() < root.maxSize) {
				root.insertKey(key, val);
			}
			else if(root.keys.size() == root.maxSize) {
				root.insertKey(key, val);
				root = root.findRoot();
			}
		}
		else if(root instanceof IndexNode) {
			Node curnode = root;
			int index;
			while(curnode instanceof IndexNode) {//leaf면 멈추기
				index = binarySearch(key, curnode);
				curnode = ((IndexNode)curnode).children.get(index);
			}
			curnode.insertKey(key, val);
			root = root.findRoot();
		}
	}
	
 	//return children index to find which node to insert
	private int binarySearch(int k, Node node) {
		int res = -1;
		int mid;
		int left = 0;
		int right = node.keys.size() - 1;
		
		if(node.keys.size() == 1) {
			if(k < node.keys.get(0)) {
				res = 0;
			}
			else {
				res = 1;
			}
			return res;
		}
		
		if(k < node.keys.get(0)) {//가장 왼쪽이랑 비교
			res = 0;
		}
		else if( k >= node.keys.get(right)) {//가장 오른쪽이랑 비교
			res = right + 1;
		}
		else {
			while(right >= left) {
				mid = (right+ left) / 2;
				if(k >= node.keys.get(mid) && k < node.keys.get(mid + 1)) {
					res = mid + 1;
					break;
				}
				if(k < node.keys.get(mid)) {
					right = mid - 1;
				}
				else {
					left = mid + 1;
				}
			}
		}
		return res;
	}
	
	public void deleteKey(int key) {
		Node leaf = findLeaf(key);
		if(leaf.keys.contains(key) == false) {//leaf에 key가 없는 경우
			return;
		}
		else {//leaf에 key가 있는 경우
			this.pairs.remove(key);
			
			if(leaf.keys.size() > (degree - 1) / 2) {//underflow X
				leaf.keys.remove(leaf.keys.get(key));
			}
			else {//underflow O
				LeafNode node = ((LeafNode)leaf).borrow_node();
				if(node != null) {//빌릴수 있는 경우
					node.borrow(node);
				}
				else {//못빌리는 경우
					//이걸 내가 할 수 있을까...?
				}
			}
		}
		root = root.findRoot();
	}
	
	public void printValues(Node node) {//check(temporary)
		for(int i=0; i<node.keys.size(); i++) {
			System.out.println(node.keys.get(i)+"-"+pairs.get(node.keys.get(i)));
		}
	}
	
	public void singleSearch(int key) {
		Node curnode = root;
		int index;
		while(curnode instanceof IndexNode) {//leaf면 멈추기
			for(int i=0; i<curnode.keys.size()-1; i++) {
				System.out.print(curnode.keys.get(i)+",");
			}
			System.out.println(curnode.keys.get(curnode.keys.size()-1));
			index = binarySearch(key, curnode);
			curnode = ((IndexNode)curnode).children.get(index);
		}
		//leaf에 도달
		if(curnode.keys.contains(key) == false) {
			System.out.println("NOT FOUND");
		}
		else {
			System.out.println(pairs.get(key));
		}
	}
	
	public LeafNode findLeaf(int key) {//key가 있는 leafnode 찾기(key가 없을 수도 있음)
		Node curnode = root;
		int index;
		while(curnode instanceof IndexNode) {//leaf면 멈추기
			index = binarySearch(key, curnode);
			curnode = ((IndexNode)curnode).children.get(index);
		}
		return (LeafNode)curnode;//leaf node
	}
	
	public void rangeSearch(int start, int end) {
		if(start == end) {
			singleSearch(start);
		}
		else if(start > end) {
			return;
		}
		else {// start < end
			LeafNode inStart = findLeaf(start);
			LeafNode inEnd = findLeaf(end);
			
			int i, j;//i:맨 처음 출력할 키의 인덱스(inStart node)
			
			if(inStart.equals(inEnd)) {//동일 노드
				if(inStart.keys.contains(start) == true) {
					i = inStart.keys.indexOf(start);
				}
				else {
					i =binarySearch(start, inStart);
				}
				if(i >= inStart.keys.size()) {
					return;
				}
				while(inStart.keys.get(i) <= end) {
					System.out.println(inStart.keys.get(i)+","+pairs.get(inStart.keys.get(i)));
					i++;
					if(i >= inStart.keys.size()) {
						break;
					}
				}
			}
			else {//서로 다른 노드
				if(inStart.keys.contains(start) == true) {
					i = inStart.keys.indexOf(start);
				}
				else {
					i = binarySearch(start, inStart);
					if(i > inStart.keys.size() - 1) {
						i = 0;
						inStart = ((LeafNode)inStart).getNextNode();
					}
				}
				if(inStart.equals(inEnd)) {
					j = 0;
					while(inEnd.keys.get(j) <= end) {
						System.out.println(inEnd.keys.get(j)+","+pairs.get(inEnd.keys.get(j)));
						j++;
						if(j >= inStart.keys.size()) {
							break;
						}
					}
					return;
				}
				
				for(j=i; j<inStart.keys.size(); j++) {//첫 노드 출력
					System.out.println(inStart.keys.get(j)+","+pairs.get(inStart.keys.get(j)));
				}
				inStart = inStart.getNextNode();//옆 노드로 이동		
				while(inStart.equals(inEnd) == false) {//마지막 노드를 만나기 전까지
					for(j=0; j<inStart.keys.size(); j++) {
						System.out.println(inStart.keys.get(j)+","+pairs.get(inStart.keys.get(j)));
					}
					inStart = inStart.getNextNode();
				}
				//마지막 노드를 만남
				j = 0;
				while(inEnd.keys.get(j) <= end) {
					System.out.println(inEnd.keys.get(j)+","+pairs.get(inEnd.keys.get(j)));
					j++;
					if(j >= inStart.keys.size()) {
						break;
					}
				}
			}
		}
	}
	
	interface writeTree {
		void traverse(Node r);
	}
	public void printTree() {//check(temporary)
		traverse(root);
	}
	
	private void traverse(Node r) {
		System.out.print(r.keys+":"+r);
		if(r instanceof LeafNode && ((LeafNode)r).getNextNode() != null) System.out.println("--->"+((LeafNode)r).getNextNode());
		else System.out.println();
		
		if(r instanceof LeafNode) {
			return;
		}
		System.out.println();
		int i = 0;
		while(i < ((IndexNode)r).children.size()) {//leaf까지
			traverse(((IndexNode)r).children.get(i));
			i++;
		}
		System.out.println();
	}
	
	//index file에  트리 저장
	public void writeFile(String file) throws IOException {
		PrintWriter fw = new PrintWriter(new BufferedWriter(new FileWriter(file, false)));
		fw.println(degree);
		
		writeTree tree = new writeTree() {
			public void traverse(Node r) {
				int i, j;
				
				if(r instanceof LeafNode) {
					return;
				}
				
				i = 0;
				while(i < ((IndexNode)r).children.size()) {//leaf까지
					
					if(((IndexNode)r).children.get(i) instanceof LeafNode && i == 0) {
						fw.print("leaf");
					}
					for(j=0; j<((IndexNode)r).children.get(i).keys.size()-1;j++) {
						fw.print(((IndexNode)r).children.get(i).keys.get(j)+",");
					}
					fw.print(((IndexNode)r).children.get(i).keys.get(((IndexNode)r).children.get(i).keys.size()-1));
					if(((IndexNode)r).children.get(i) instanceof IndexNode) {
						fw.println();
					}
					else {
						fw.write("/");
					}
					traverse(((IndexNode)r).children.get(i));
					i++;
				}
				if(((IndexNode)r).children.get(0) instanceof LeafNode) {
					fw.println();
				}
			};
		};
		
		//root print
		if(root instanceof LeafNode) {
			fw.print("leaf");
		}
		for(int i=0; i < root.keys.size()-1; i++) {
			fw.print(root.keys.get(i)+",");
		}
		fw.println(root.keys.get(root.keys.size()-1));
		
		tree.traverse(root);
		
		//"key-val"
		fw.println();
		for(int key : pairs.keySet()) {
			int val = pairs.get(key);
			fw.print(key+"-"+val+",");
		}
		fw.flush();
		fw.close();
	}
	
	private void addValue(int key, int val) {
		this.pairs.put(key, val);
	}

	public synchronized void traverse_read(Node r, String line, BufferedReader fr, BPlusTree t) throws IOException {
		int i, j;
		String[] aline;
		
		line = fr.readLine();
		
		if(line.substring(0, 1).equals("l")) {
			aline = line.split("/");
			aline[0] = aline[0].substring(4,aline[0].length());
			i = 0;
			String[] leafKeys, nextKeys;
			
			while(i < r.keys.size() + 1) {						
				LeafNode tmp = new LeafNode(t.degree);
				//tmp.setParent(r);
				
				leafKeys = aline[i].split(",");
				for(j=0; j<leafKeys.length; j++) {
					tmp.keys.add(Integer.parseInt(leafKeys[j]));
				}
		
				((IndexNode)r).children.add(tmp);
				((IndexNode)r).children.get(i).setParent(r);
				
				LeafNode next = new LeafNode(t.degree);
				if(i == r.keys.size()) {
					((LeafNode)((IndexNode)r).children.get(i)).setNextNode(null);
					//tmp.setNextNode(null);
				}
				else {
					nextKeys = aline[i+1].split(",");
					for(j=0; j<nextKeys.length;j++) {
						next.keys.add(Integer.parseInt(nextKeys[j]));
					}
					//tmp.setNextNode(next);
					((LeafNode)((IndexNode)r).children.get(i)).setNextNode(next);
				}
				i++;
			}
			return;
		}
		
		i = 0;
		while(i < r.keys.size() + 1) {	
			IndexNode tmp = new IndexNode(t.degree);
			//tmp.setParent(r);
			
			if(i != 0) {
				line = fr.readLine();
			}
			aline = line.split(",");
			for(j=0; j<aline.length; j++) {
				tmp.keys.add(Integer.parseInt(aline[j]));
			}
			((IndexNode)r).children.add(tmp);
			((IndexNode)r).children.get(i).setParent(r);
			traverse_read(((IndexNode)r).children.get(i), line, fr, t);
			i++;
		}
	}
	
	public synchronized void traverse_leaf(ArrayList<LeafNode> leaves, Node r) {
		if(r instanceof LeafNode) {
			leaves.add((LeafNode)r);
			return;
		}
		int i = 0;
		while(i < ((IndexNode)r).children.size()) {//leaf까지
			traverse_leaf(leaves, ((IndexNode)r).children.get(i));
			i++;
		}
	}
	
	public BPlusTree readFile(String file) throws IOException {//index file 읽으면서 트리 가져오기
		BufferedReader fr = new BufferedReader(new FileReader(file));
		String line = "";
		String[] aline;
		
		//degree 읽고 tree 생성
		degree = Integer.parseInt(fr.readLine());
		BPlusTree t = new BPlusTree(degree);
		
		//root
		line = fr.readLine();
		if(line == null) {
			t.root = new LeafNode(degree);
			return t;
		}

		aline = line.split(",");
		//루트가 리프인지 아닌지 구별하기
		if(aline[0].substring(0,1).equals("l")) {
			t.root = new LeafNode(degree);
			aline[0] = aline[0].substring(4,aline[0].length());
			for(int i=0; i<aline.length; i++) {
				t.root.keys.add(Integer.parseInt(aline[i]));
			}
		}
		else {
			t.root = new IndexNode(degree);
			for(int i=0; i<aline.length; i++) {
				t.root.keys.add(Integer.parseInt(aline[i]));
			}
			traverse_read(t.root, line, fr, t);
		}
		
		//leaf 연결
		ArrayList<LeafNode> leaves = new ArrayList<>();
		t.traverse_leaf(leaves, t.root);
		for(int i=0; i<leaves.size()-1; i++) {
			leaves.get(i).setNextNode(leaves.get(i+1));
		}
		
		//key-val 저장 
		line = fr.readLine();
		line = fr.readLine();
		aline = line.split(",");
		int size = aline.length;//key의 개수
		int i = 0, key, val;
		String[] pair;
		while(i < size) {
			pair = aline[i].split("-");
			key = Integer.parseInt(pair[0]);
			val = Integer.parseInt(pair[1]);
			t.addValue(key, val);
			i++;
		}
		
		fr.close();
		
		return t;
	}
}

