package util;

import java.text.DecimalFormat;

public class Calculater {

	// format a double value:
	public static double formatDouble(double val, int k) {
		String formatter = "0.";
		for (int i = 0; i < k; i++) 
			formatter += "0";
		DecimalFormat df = new DecimalFormat(formatter);
		return Double.parseDouble(df.format(val));
	}	
	
	// whether the point is in Rect:(x0,y0),(x1,y1)
	public static boolean inRect(Point point, double x_0, double y_0, double x_1, double y_1){
		if (point.x < x_0 || point.x > x_1 || point.y < y_0 || point.y > y_1) {
			return false;
		}
		return true;
	}
	
	// whether the point is in node:
	public static boolean inNode(Point point, TreeNode node){
		double x_0 = node.point[0].x;
		double y_0 = node.point[0].y;
		double x_1 = node.point[1].x;
		double y_1 = node.point[1].y;
		return inRect(point, x_0, y_0, x_1, y_1);
	}

	// shortest distance: point & node
	public static double shortDis(Point point, TreeNode node){
		double dis = distance(point, node.point[0]);
		dis = Math.min(dis, distance(point, node.point[1]));
		dis = Math.min(dis, distance(point, new Point(node.point[0].x, node.point[1].y)));
		dis = Math.min(dis, distance(point, new Point(node.point[1].x, node.point[0].y)));
		
		if (point.x > Math.min(node.point[0].x, node.point[1].x) && point.x < Math.max(node.point[0].x, node.point[1].x)) {
			dis = Math.min(dis, distance(point, new Point(point.x, node.point[0].y)));
			dis = Math.min(dis, distance(point, new Point(point.x, node.point[1].y)));
		}
		if (point.y > Math.min(node.point[0].y, node.point[1].y) && point.y < Math.max(node.point[0].y, node.point[1].y)) {
			dis = Math.min(dis, distance(point, new Point(node.point[0].x, point.y)));
			dis = Math.min(dis, distance(point, new Point(node.point[1].x, point.y)));
		}
		return dis;
	}
	
	// distance: node & node
	public static double distance(Point p1, Point p2){
		double x = p1.x - p2.x;
		double y = p1.y - p2.y;
		return Math.sqrt(x * x + y * y);
	}
	
	// distance: node & node
	public static double distance_coord(double lat_1, double lon_1, double lat_2, double lon_2){
		Point point_1 = new Point(Mercator.lonLat2Mercator(lon_1, lat_1)[0], Mercator.lonLat2Mercator(lon_1, lat_1)[1]);
		Point point_2 = new Point(Mercator.lonLat2Mercator(lon_2, lat_2)[0], Mercator.lonLat2Mercator(lon_2, lat_2)[1]);

		return distance(point_1, point_2);
	}
	
	// degree: center_point & point
	public static double degree(Point center, Point point){
		double degree;
		double x = point.x - center.x;
		double y = point.y - center.y;
		
		// if coincide:
		if (x == 0 && y == 0)
			return -1;
		
		double offset = Math.PI;
		if (x >= 0 && y >= 0) 
			offset = Math.PI * 0;
		if (x < 0 && y > 0) 
			offset = Math.PI * 1;
		if (x < 0 && y < 0) 
			offset = Math.PI * 1;
		if (x >= 0 && y < 0) 
			offset = Math.PI * 2;	
		degree = Math.atan(y / x);
		degree += offset;
		degree = degree / (Math.PI) * 180;
		degree = Calculater.formatDouble(degree, 2);
		
		return degree;
	}
	
	// distance with degree:
	public static double disInDegree(Point point, TreeNode node, double A, double B){
		double x_0 = node.point[0].x;
		double y_0 = node.point[0].y;
		double x_1 = node.point[1].x;
		double y_1 = node.point[1].y;
		
		// if the point is in the node:
		if (point.x > x_0 && point.x < x_1 && point.y > y_0 && point.y < y_1) {
			return 0;
		}
		
		// the point is outside the node:
		double degrees[] = new double[4];
		degrees[0] = degree(point, new Point(x_0, y_0));
		degrees[1] = degree(point, new Point(x_0, y_1));
		degrees[2] = degree(point, new Point(x_1, y_0));
		degrees[3] = degree(point, new Point(x_1, y_1));
		
		double MAX = 0;
		double MIN = 360;
		for (int i = 0; i < degrees.length; i++) {
			MAX = Math.max(MAX, degrees[i]);
			MIN = Math.min(MIN, degrees[i]);
		}
		if (MIN == -1) {
			return 0;
		}

		if (MAX < A || MIN > B) {
			return Double.MAX_VALUE;
		}
		
		return shortDis(point, node);
	}
	
	public static void main(String argv[]){
		Point p0 = new Point(0,0);
		Point p1 = new Point(-1,1);
		Point p2 = new Point(2,2);
		TreeNode node = new TreeNode(p1, p2);
		
		System.out.println(disInDegree(p0, node, 0, 180));
	}
	
}
