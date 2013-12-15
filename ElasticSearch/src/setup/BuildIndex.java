package setup;

import java.io.*;
import java.util.*;
import util.Calculater;
import util.POI;
import util.Point;
import util.Tree;
import util.TreeNode;

public class BuildIndex {

	public BufferedReader reader;
	public PrintStream printer;
	public PrintStream printer_tree;
	public Tree tree = new Tree();
	public HashMap<String, Vector<POI>> nodeToPoi = new HashMap<String, Vector<POI>>();
	
	
	/**
	 *  build the tree during inserting the pois, then
	 *  traverse the tree to output the index
	 * * * * * * * * * * * * * * * * * * * * */
	public void buildIndex(){
		try {
			reader = new BufferedReader(new FileReader("data/poiXY.csv"));
			printer = new PrintStream("data/indexUpload.csv");
			printer_tree = new PrintStream("data/indexLocal.csv");
			
			Tree.Max_Depth = 10;

			String line = "";
			while((line = reader.readLine()) != null){
				// line: id, x, y, name
				String strs[] = line.split("\t");
				String ID = strs[0];
				String name = strs[3];
				double x = Double.parseDouble(strs[1]);
				double y = Double.parseDouble(strs[2]);
				Point point = new Point(x, y);
				POI poi = new POI(ID, name, point);
				insertTree(poi);
			}
			
			traverse(tree.rootNode);
			
			printer.close();
			printer_tree.close();
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 *   traverse the tree: output the index
	 * * * * * * * * * * * * * * * * * * * * */
	public void traverse(TreeNode node){
		if (!node.isLeaf) {
			for(TreeNode child:node.children)
				traverse(child);
			return;
		}
		
		// if the node is leaf, store the index:
		Vector<POI> pois = nodeToPoi.get(node.path);
		HashMap<String, Vector<String>> map = new HashMap<String, Vector<String>>();
		for (POI poi:pois) {
			String name = poi.name;
			name = name.replaceAll("'", " ");
			name = name.replaceAll("\\.", " ");
			name = name.replaceAll("-", " ");
			String strs[] = name.split("\\s");
			
			// remove duplicates:
			HashSet<String> set = new HashSet<String>();
			set.addAll(Arrays.asList(strs));
			
			for (String str:set){
				if (str.length() == 0)
					continue;
				if (!map.containsKey(str))
					map.put(str, new Vector<String>());
				map.get(str).add(poi.ID + "," + poi.point.x + "," + poi.point.y);
			}
		}
		printer_tree.print(node.path);
		String keys = "";
		for (String key:map.keySet()) {
			String line = "";
			for (String fragment:map.get(key)) {
				line += fragment + "_";
			}
			keys += "\t" + key;
			printer.println(node.path + "\t" + key + "\t" + line);
		}
		printer_tree.println(keys);
	}
	
	public void insertTree(POI poi){
//		Point point, String ID, String name
		Point point = poi.point;
		TreeNode cursor = tree.rootNode;
		while (true){
			if (cursor.children.size() != 0){
				cursor = chooseChild(point, cursor);
				continue;
			}
			else{
				// split:
				if (cursor.depth < Tree.Max_Depth) {
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
					
					for (int i = 0; i < 4; i++) {
						TreeNode child = cursor.children.get(i);
						child.depth = cursor.depth + 1;
						child.path = cursor.path + "_" + i;
					}
					cursor = chooseChild(point, cursor);
				}
				
				// reach the max depth: can not split anymore
				else{
					if (!nodeToPoi.containsKey(cursor.path))
						nodeToPoi.put(cursor.path, new Vector<POI>());
					cursor.isLeaf = true;
					nodeToPoi.get(cursor.path).add(poi);
					break;
				}
			}
		}
	}
	
	public static TreeNode chooseChild(Point point, TreeNode node){
		for (TreeNode child:node.children)
			if (Calculater.inNode(point, child))
				return child;
		return null;
	}
	
	public static void main(String[] args) {
		new BuildIndex().buildIndex();
	}

}
