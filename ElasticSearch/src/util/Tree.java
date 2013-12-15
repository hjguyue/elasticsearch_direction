package util;

import java.util.HashMap;
import java.util.HashSet;

public class Tree {

	public TreeNode rootNode;
	
	public static double Max_Depth = 10;
	public static double Max_Width = 6e7;
	public static double Max_Height = 6e7;
	
	public static HashMap<String, HashSet<String>> keys = new HashMap<String, HashSet<String>>();
	
	public Tree(){
		Point point_0 = new Point(-3e7,-3e7);
		Point point_1 = new Point(3e7,3e7);
		rootNode = new TreeNode(point_0, point_1);
		rootNode.depth = 0;
		rootNode.path = "0";
	}
	
	
	
}
