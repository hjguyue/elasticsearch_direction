package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import setup.Property;
import util.*;

public class Search {
	
	public Tree tree;
	public Client client;
	public QueryBuilder queryBuilder;
	public SearchResponse searchResponse;
	public String indexName;
	
	// normal search bound:
	double upperBound = 10;
	double initBound = 0.001;  // about 100 meters in the map
	
	// search bound:
	double distanceBound;
	
	public PriorityQueue<TreeNode> minQueue = new PriorityQueue<TreeNode>(1, new Comparator<TreeNode>() {
		public int compare(TreeNode o1, TreeNode o2) {
			if (o1.valuation > o2.valuation)
				return 1;
			return -1;
		}
	});
	public PriorityQueue<POI> maxQueue = new PriorityQueue<POI>(1, new Comparator<POI>() {
		public int compare(POI o1, POI o2) {
			if (o1.valuation > o2.valuation)
				return -1;
			return 1;
		}
	});
	
			
	public void init(){
		Property.load();
		System.out.println("initializing...");
		client = new TransportClient().addTransportAddress(new InetSocketTransportAddress(Property.masterIP, 9300));
		tree = new LoadIndex().initial("data/indexLocal.csv");
		indexName = Property.indexName;
		distanceBound = Double.parseDouble(Property.disBound);
		System.out.println("initialized !");
	}
	
	// test: not finished
	public Vector<String> query_normal(String[] keywords, int topK, double latitude, double longitude, double A, double B){
		boolean Haskey = true;
		if (keywords[0].length() == 0)
			Haskey = false;
		if (!Haskey)
			return null;
		
		double XY[] = Mercator.lonLat2Mercator(longitude, latitude);
		double X = XY[0];
		double Y = XY[1];
		Point point = new Point(X, Y);

		double bound = initBound;
		Vector<String> result = new Vector<String>();
		HashSet<String> idSet = new HashSet<String>();
		while (bound <= upperBound){
			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
			for (String key:keywords) {
				if (key.length() > 0)
					boolQueryBuilder.must(QueryBuilders.termQuery("name", key));
			}
			boolQueryBuilder.must(QueryBuilders.rangeQuery("latitude").gt(latitude - bound).lt(latitude + bound));
			boolQueryBuilder.must(QueryBuilders.rangeQuery("longitude").gt(longitude - bound).lt(longitude + bound));
			
			queryBuilder = boolQueryBuilder;
			searchResponse = client.prepareSearch(indexName).setTypes("poi").setQuery(queryBuilder)
					.addSort(SortBuilders.geoDistanceSort("location").point(latitude, longitude).order(SortOrder.ASC))
					.setSize(20000).execute().actionGet();
			
			int N = (int) searchResponse.getHits().totalHits();
			
			for (int i = 0; i < N; i++){
				String idString = searchResponse.getHits().getAt(i).getSource().get("id").toString();
				String string = searchResponse.getHits().getAt(i).getSource().get("name").toString();
				String lat = searchResponse.getHits().getAt(i).getSource().get("latitude").toString();
				String lon = searchResponse.getHits().getAt(i).getSource().get("longitude").toString();
				double xy[] = Mercator.lonLat2Mercator(Double.parseDouble(lon), Double.parseDouble(lat));
				double x = xy[0];
				double y = xy[1];
				Point tempPoint = new Point(x, y);
				double degree = Calculater.degree(point, tempPoint);

				if (degree >= A && degree <= B){
					if (idSet.contains(idString))
						continue;
					idSet.add(idString);
					if (Calculater.distance(point, tempPoint) > distanceBound)
						return result;
					result.add("poi id: " + idString + ", location:(" + lat + ", " + lon + "), distance: " + Calculater.distance(point, tempPoint) + ", name: " + string);
				}
				if (result.size() == topK)
					return result;
			}
			bound *= 2;
		}
		return result;
	}
	
	public Vector<String> query(String[] keywords, int topK, double latitude, double longitude, double A, double B){
		boolean Haskey = true;
		if (keywords[0].length() == 0)
			Haskey = false;
		
		Vector<String> result = new Vector<String>();
		double XY[] = Mercator.lonLat2Mercator(longitude, latitude);
		double X = XY[0];
		double Y = XY[1];
		Point point = new Point(X, Y);
		
		maxQueue.clear();
		minQueue.clear();
		
		tree.rootNode.valuation = Calculater.disInDegree(point, tree.rootNode, A, B);
		minQueue.add(tree.rootNode);
		while(minQueue.size() != 0){
			TreeNode topNode = minQueue.poll();
			// not the leaf node:
			if (!topNode.isLeaf) {
				for (TreeNode child:topNode.children){
					child.valuation = Calculater.disInDegree(point, child, A, B);
//					System.out.println(point.x + ", " + point.y);
//					System.out.println(child.path + ", " + child.valuation + ", " + 
//					child.point[0].x + ", " + child.point[0].y + ", " + child.point[1].x + ", " + child.point[1].y);
					minQueue.add(child);
				}
				continue;
			}
			// leaf node:
			if (Haskey) {
				boolean KEY_VALUE = true;
				for (String key:keywords) {
					if (!Tree.keys.get(topNode.path).contains(key))
						KEY_VALUE = false;
				}
				if (!KEY_VALUE)
					continue;	
			}

			QueryBuilder queryBuilder_0 = QueryBuilders.termQuery("poiIndex.path", topNode.path);
			QueryBuilder queryBuilder_1 = QueryBuilders.inQuery("poiIndex.keyword", keywords);
			
			if (Haskey)
				queryBuilder = QueryBuilders.boolQuery().must(queryBuilder_0).must(queryBuilder_1);
			if (!Haskey)
				queryBuilder = QueryBuilders.boolQuery().must(queryBuilder_0);
			
			searchResponse = client.prepareSearch(indexName).setTypes("poiIndex").setQuery(queryBuilder).setSize(50).execute().actionGet();
			
			HashMap<String, Integer> map = new HashMap<String, Integer>();
			int N = (int) searchResponse.getHits().totalHits();
			if (N > 50) {
				N = 50;
			}
			
			for (int i = 0; i < N; i++){
				String idString = searchResponse.getHits().getAt(i).getSource().get("id").toString();
				
				String ids[] = idString.split("_");
				for (String id:ids){
					if (!map.containsKey(id))
						map.put(id, 0);
					map.put(id, map.get(id)+1);
				}
			}
			for (String key:map.keySet()){
				if (Haskey && map.get(key) != keywords.length)
					continue;
				
				String strs[] = key.split(",");
				Point tempPoint = new Point(Double.parseDouble(strs[1]), Double.parseDouble(strs[2]));
				POI poi = new POI(strs[0], "", tempPoint);
				poi.valuation = Calculater.distance(point, tempPoint);
				double degree = Calculater.degree(point, tempPoint);
				if (degree < A || degree > B)
					continue;
				// id the queue hasn't reach topK 
				if (maxQueue.size() < topK) {
					maxQueue.add(new POI(poi));
				}
				else if (maxQueue.peek().valuation > poi.valuation) {
					maxQueue.poll();
					maxQueue.add(new POI(poi));
				}
			}
			// judge the bound:
			if (maxQueue.size() == topK && maxQueue.peek().valuation < minQueue.peek().valuation) {
				break;
			}
			if (minQueue.peek().valuation > distanceBound) {
				break;
			}
		}
//		int count = maxQueue.size();
		Vector<String> tempVector = new Vector<String>();
		Vector<String> idVector = new Vector<String>();
		for (int i = 0; i < topK; i++) {
			if (maxQueue.size() == 0) {
				break;
			}
			POI poi = maxQueue.poll();
			if (poi.valuation > distanceBound)
				continue;
			
			Point poiPoint = poi.point;
			double ll[] = Mercator.Mercator2lonLat(poiPoint.x, poiPoint.y);
			tempVector.add("poi id: " + poi.ID + ", location:(" + ll[1] + ", " + ll[0] + "), distance:" + Calculater.distance(point, poiPoint));
			idVector.add(poi.ID);
		}
		
		//get the names of the pois:
		HashMap<String, String> idNameMap = new HashMap<String, String>();
		QueryBuilder queryID = QueryBuilders.inQuery("poi.id", idVector);
		
		searchResponse = client.prepareSearch(indexName).setTypes("poi").setQuery(queryID).setSize(50).execute().actionGet();

		int N = (int) searchResponse.getHits().totalHits();
		
		for (int i = 0; i < N && i < tempVector.size(); i++){
			String idString = searchResponse.getHits().getAt(i).getSource().get("id").toString();
			String nameString = searchResponse.getHits().getAt(i).getSource().get("name").toString();
			idNameMap.put(idString, nameString);
		}
		for (int i = tempVector.size() - 1; i >= 0; i--) {
			if (idNameMap.get(idVector.elementAt(i)) == null)
				continue;
			result.add(tempVector.elementAt(i) + ", name: " + idNameMap.get(idVector.elementAt(i)));
		}
		
		return result;
	}
	
	public static BufferedReader reader;

	public void test(){
		try {
			reader = new BufferedReader(new FileReader("data/query.txt"));
			String line = "";

			while((line = reader.readLine()) != null){
				String keywords[] = line.split(" ");
				line = reader.readLine();
				String strs[] = line.split(" ");
				int topK = Integer.parseInt(strs[0]);
				double latitude = Double.parseDouble(strs[1]); 
				double longitude= Double.parseDouble(strs[2]);
				double degree_0 = Double.parseDouble(strs[3]);
				double degree_1 = Double.parseDouble(strs[4]);

				query(keywords, topK, latitude, longitude, degree_0, degree_1);
			}

			reader.close();
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public static void main(String argv[]){
		Search search = new Search();
		search.init();
		search.test();
	}
}