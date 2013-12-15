package setup;

import java.io.*;

import util.Calculater;
import util.Mercator;

public class GetMercator {

	private BufferedReader reader;
	private PrintStream printer;
	
	public void getMercator(){ 
		try {
			reader = new BufferedReader(new FileReader("data/poi.csv"));
			printer = new PrintStream("data/poiXY.csv");
			String line = "";

			while ((line = reader.readLine()) != null){
				// id, latitude, longitude, name
				String[] strs = line.split("\t");
				if (strs.length != 4)
					continue;
				
				// use Mercator:
				double latitude = Double.parseDouble(strs[1]);
				double longitude = Double.parseDouble(strs[2]);
				double XY[] = Mercator.lonLat2Mercator(longitude, latitude);
				XY[0] = Calculater.formatDouble(XY[0],1);
				XY[1] = Calculater.formatDouble(XY[1],1);
				
				// output the new data:
				printer.println(strs[0] + "\t" + XY[0] + "\t" + XY[1] + "\t" + strs[3].toLowerCase());
			}
			
			printer.flush();
			printer.close();
			reader.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new GetMercator().getMercator();
	}

}
