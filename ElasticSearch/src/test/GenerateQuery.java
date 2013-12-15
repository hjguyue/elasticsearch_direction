package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.Vector;

import util.Point;

public class GenerateQuery {

	public static BufferedReader reader;
	public static PrintStream printer;
	
	public static void main(String[] args) throws Exception{
		reader = new BufferedReader(new FileReader("data/generateQuery/geo.txt"));
		String line = "";
		
		Vector<Point> geos = new Vector<Point>();
		while ((line = reader.readLine()) != null){
			String strs[] = line.split("\t");
			
		}
	}

}
