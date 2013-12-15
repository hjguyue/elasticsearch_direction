package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;

import util.Point;
import util.Tree;
import util.TreeNode;

public class LoadIndex {
	public Tree tree = new Tree();
	public BufferedReader reader;
	
	public Tree initial(String indexFile){
		try {
			reader = new BufferedReader(new FileReader(indexFile));
			String line = "";
			
			while((line = reader.readLine()) != null){
				String strs[] =  line.split("\t");
				String path = strs[0];
				Tree.keys.put(path, new HashSet<String>());
				for (int i = 1; i < strs.length; i++) {
					Tree.keys.get(path).add(strs[i]);
				}
				insert(path);
			}
			
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tree;
	}
	
	public void insert(String path){
		String paths[] = path.split("_");
		TreeNode cursor = tree.rootNode;

		for (int i = 1; i < paths.length; i++) {
			int k = Integer.parseInt(paths[i]);
			// already split:
			if (cursor.children.size() != 0) {
				cursor = cursor.children.get(k);
				continue;
			}
			// need split:
			double x_0 = cursor.point[0].x;
			double y_0 = cursor.point[0].y;
			double x_1 = cursor.point[1].x;
			double y_1 = cursor.point[1].y;
			double midx = (x_0 + x_1)/2;
			double midy = (y_0 + y_1)/2;
			
			cursor.children.add(new TreeNode(new Point(x_0, midy), new Point(midx, y_1)));
			cursor.children.add(new TreeNode(new Point(midx, midy), new Point(x_1, y_1)));
			cursor.children.add(new TreeNode(new Point(midx, y_0), new Point(x_1, midy)));
			cursor.children.add(new TreeNode(new Point(x_0, y_0), new Point(midx, midy)));
			
			for (int j = 0; j < 4; j++) {
				TreeNode child = cursor.children.get(j);
				child.depth = cursor.depth + 1;
				child.path = cursor.path + "_" + j;
			}
			cursor = cursor.children.get(k);
		}
		cursor.isLeaf = true;
	}
	
	
	public static void main(String argv[]){
		new LoadIndex().initial("data/indexLocal.csv");
	}
}
