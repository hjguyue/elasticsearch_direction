package util;

public class POI {
	public String ID;
	public String name;
	public Point point;
	
	// used in the priorityQueue to estimate the distance:
	public double valuation;
	
	public POI(String ID, String name, Point point){
		this.ID = ID;
		this.name = name;
		this.point = new Point(point);
	}

	public POI(POI poi){
		this.ID = poi.ID;
		this.name = poi.name;
		this.point = new Point(poi.point);
		this.valuation = poi.valuation;
	}
	
}
