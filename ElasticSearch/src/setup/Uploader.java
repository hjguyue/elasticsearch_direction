package setup;

import java.io.BufferedReader;
import java.io.FileReader;

import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;


public class Uploader {

	BufferedReader reader;
	
	public void upload(){
		try {
			Property.load();
			String index = Property.indexName;
			Client client = new TransportClient().addTransportAddress(new InetSocketTransportAddress(Property.masterIP, 9300));

			BulkRequestBuilder bulkRequest = client.prepareBulk();
			XContentBuilder builder = null;
			
			reader = new BufferedReader(new FileReader("data/indexUpload.csv"));
			String line = "";
			int count = 0;
			while ((line = reader.readLine()) != null){
				String strs[] = line.split("\t");
				
				builder = XContentFactory.jsonBuilder().startObject().field("path", strs[0]).field("keyword", strs[1])
						.field("id",strs[2]).endObject();
				
				// bulk add index:
				bulkRequest.add(client.prepareIndex(index, "poiIndex").setSource(builder));
				
				count++;
				if (count % 10000 == 0) {
					bulkRequest.execute().actionGet();
					bulkRequest = client.prepareBulk();
				}
			}
			if (count % 10000 != 0) {
				bulkRequest.execute().actionGet();
				bulkRequest = client.prepareBulk();
			}
			
			XContentBuilder mapping = XContentFactory.jsonBuilder()  
				       .startObject()
				         .startObject("poi")  
				          .startObject("properties")         
				           .startObject("id").field("type", "string").endObject()    
				           .startObject("location").field("type", "geo_point").endObject()
				           .startObject("latitude").field("type", "double").endObject()
				           .startObject("longitude").field("type", "double").endObject()
				           .startObject("name").field("type", "string").endObject()
				          .endObject() 
				        .endObject()  
				      .endObject();  
			PutMappingRequest mappingRequest = Requests.putMappingRequest("desks").type("poi").source(mapping);  
			client.admin().indices().putMapping(mappingRequest).actionGet();  
			
			reader = new BufferedReader(new FileReader("data/poi.csv"));
			count = 0;
			while ((line = reader.readLine()) != null){
				String strs[] = line.split("\t");
				
				builder = XContentFactory.jsonBuilder().startObject()
						.field("id", strs[0])
						.field("location", strs[1] + ","+ strs[2])
						.field("latitude", Double.parseDouble(strs[1]))
						.field("longitude",Double.parseDouble(strs[2]))
						.field("name", strs[3])
						.endObject();
				
				// bulk add index:
				bulkRequest.add(client.prepareIndex(index, "poi").setSource(builder));
				
				count++;
				if (count % 10000 == 0) {
					bulkRequest.execute().actionGet();
					bulkRequest = client.prepareBulk();
				}
			}
			
			if (count % 10000 != 0) {
				bulkRequest.execute().actionGet();
				bulkRequest = client.prepareBulk();
			}
			
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String argv[]){
		new Uploader().upload();
	}

}
