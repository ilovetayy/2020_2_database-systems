package bptree;

import java.util.Scanner;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Program {
	
	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub

		Scanner scanner = new Scanner(System.in);
		
		String command, index_file, input_file, delete_file;
		int degree, search_key, start_key, end_key;
		ArrayList<Integer> delete_arr = new ArrayList<Integer>();
		
		BPlusTree tree = new BPlusTree();
		
		try {
			command = scanner.next();
			if(command.equals("-c")) {
				index_file = scanner.next();
				degree = Integer.parseInt(scanner.next());
				if(degree <= 1) {
					return;
				}
				
				BufferedWriter fw = new BufferedWriter(new FileWriter(index_file, false));
				fw.write(degree + "\n");
				fw.flush();
				fw.close();
			}
			else if(command.equals("-i")) {
				index_file = scanner.next();
				input_file = scanner.next();
				
				//index file 읽기
				tree = tree.readFile(index_file);
				
				BufferedReader fr = new BufferedReader(new FileReader(input_file));
				String line = "";
				String[] aline;
				while((line = fr.readLine()) != null) {
					aline = line.split(",");
					tree.insertIntoTree(Integer.parseInt(aline[0]), Integer.parseInt(aline[1]));//tree 생성
				}
				fr.close();
				
				//index file에 tree 기록
				tree.writeFile(index_file);	
			}
			else if(command.equals("-d")) {
				index_file = scanner.next();
				delete_file = scanner.next();
				
				//index file 읽으면서 트리 만들기
				tree = tree.readFile(index_file);
				
				//delete file 읽으면서 지울 키 저장 후 deleteKey()
				BufferedReader fr = new BufferedReader(new FileReader(delete_file));
				String line = "";
				while((line = fr.readLine()) != null) {
					delete_arr.add(Integer.parseInt(line));
				}
				fr.close();
				for(int i=0; i<delete_arr.size(); i++) {
					tree.deleteKey(delete_arr.get(i));
				}
				
				//index file에 tree 기록
				tree.writeFile(index_file);
			}
			else if(command.equals("-s")) {
				index_file = scanner.next();
				search_key = Integer.parseInt(scanner.next());
				
				//index file읽으면서 트리 만들기
				tree = tree.readFile(index_file);
				
				tree.singleSearch(search_key);
			}
			else if(command.equals("-r")) {
				index_file = scanner.next();
				start_key = Integer.parseInt(scanner.next());
				end_key = Integer.parseInt(scanner.next());
				
				//index file 읽으면서 트리 만들기
				tree = tree.readFile(index_file);
				
				tree.rangeSearch(start_key, end_key);
			}
			else return;
		}
		
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
