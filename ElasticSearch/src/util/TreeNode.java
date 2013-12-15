package util;

import java.util.Vector;

public class TreeNode {

	/*
	 * location and size:
	 * 
	 * point[0] is the left bottom point, 
	 * point[1] is the right top point
	 * * * * * * * * * * * * * * * * * * * * * */
	public Point point[] = new Point[2];
	
	/* clockwise:
	 *    0 1
	 *    3 2
	 * * * * * * * * * * * * * */
	public Vector<TreeNode> children = new Vector<TreeNode>();
	
	public int depth;
	public double width;
	public double height;
	public boolean isLeaf;
	public String path;
	
	// used in the priorityQueue to estimate the distance:
	public double valuation;

	public TreeNode(){}
	
	public TreeNode(Point point_0, Point point_1){
		point[0] = new Point(point_0);
		point[1] = new Point(point_1);
		width = point[1].x - point[0].x;
		height = point[1].y - point[0].y;
	}

}
