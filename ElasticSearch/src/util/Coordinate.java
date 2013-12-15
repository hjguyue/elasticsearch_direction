package util;

public class Coordinate {
	public double x;
	public double y;
	
	public Coordinate(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public Coordinate(Coordinate c){
		this.x = c.x;
		this.y = c.y;
	}

}
