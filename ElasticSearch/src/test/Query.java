package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Date;
import java.util.Vector;

public class Query {

	public static BufferedReader reader;
	public static Search search;

	public static Vector<Vector<String>> query(int type){
		Vector<Vector<String>> results = new Vector<Vector<String>>();
		try {
			reader = new BufferedReader(new FileReader("data/query.txt"));
			String line = "";

			while((line = reader.readLine()) != null){
				line = line.toLowerCase();
				String keywords[] = line.split(" ");
				line = reader.readLine();
				line = line.toLowerCase();
				String strs[] = line.split(" ");
				int topK = Integer.parseInt(strs[0]);
				double latitude = Double.parseDouble(strs[1]); 
				double longitude= Double.parseDouble(strs[2]);
				double degree_0 = Double.parseDouble(strs[3]);
				double degree_1 = Double.parseDouble(strs[4]);

				if (keywords[0].length() > 0){
					if (type == 0) 
						results.add(search.query(keywords, topK, latitude, longitude, degree_0, degree_1));
					if (type != 0)
						results.add(search.query_normal(keywords, topK, latitude, longitude, degree_0, degree_1));
				}
				else
					results.add(search.query_normal(keywords, topK, latitude, longitude, degree_0, degree_1));
			}

			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}
	
	public static void main(String[] args) {
		search = new Search();
		search.init();
		
		// search type: 0:fast, 1:normal
		String[] types = {"fast", "normal"};
		int type = 0;
		type = (args.length > 0) ? Integer.parseInt(args[0]) : type;
		System.out.println("search type: " + types[type]);
		
		// search query by query:
		long start = new Date().getTime();
		Vector<Vector<String>> resluts = query(type);
		int num = 0;
		for (Vector<String> result:resluts) {
			num++;
			System.out.println("\n\n- - - - - - The result of query " + num + ":");
			for (String string:result)
				System.out.println(string);
		}
		System.out.println(new Date().getTime() - start);
	}

}
