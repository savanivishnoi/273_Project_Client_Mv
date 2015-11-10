import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.client.*;
import org.glassfish.jersey.internal.util.collection.StringKeyIgnoreCaseMultivaluedMap;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.glassfish.jersey.media.sse.EventInput;
import org.glassfish.jersey.media.sse.EventListener;
import org.glassfish.jersey.media.sse.EventSource;
import org.glassfish.jersey.media.sse.SseFeature;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import org.glassfish.jersey.internal.util.Property;
public class Create_Event_Handle {
	public static void main(String args[]) throws InterruptedException{
		Client client = ClientBuilder.newBuilder().register(SseFeature.class).build();
		String[] arr_input;
		WebTarget target = client.target("http://localhost:8080/273_Proj_Server/api/events/all");
		while(true) {
			Thread.sleep(1000);
			EventInput eventInput = target.request().get(EventInput.class);
			while (!eventInput.isClosed()) {
				final InboundEvent inboundEvent = eventInput.read();
				if (inboundEvent != null) {
					System.out.println(inboundEvent.getName() + "; " + inboundEvent.readData(String.class));
					if(inboundEvent.getName().equals("create")) {
						MongoClient db_cl = new MongoClient("localhost", 27017);
			    		BasicDBObject object = new BasicDBObject();
			    		DBCollection collection = db_cl.getDB("database_name").getCollection("273_Client_Object_DB");	
			    		DBObject dbObject = (DBObject)JSON.parse(inboundEvent.readData(String.class));
			    		collection.insert(dbObject);
			    		System.out.println(args[0]);
					} else if(inboundEvent.getName().equals("read")) {
						String input = inboundEvent.readData(String.class);
						read_Call(input);
					
					} else if(inboundEvent.getName().equals("update")) {
						MongoClient db_cl = new MongoClient("localhost", 27017);
			    		BasicDBObject object = new BasicDBObject();
			    		DBCollection collection = db_cl.getDB("database_name").getCollection("273_Client_Object_DB");	
			    		String input = inboundEvent.readData(String.class);
			    		arr_input = input.split(",");
			    		object.append("$set", new BasicDBObject().append("value", arr_input[1]));		
			     		BasicDBObject searchQuery = new BasicDBObject().append("_id", arr_input[0]);
			     		collection.update(searchQuery, object );
					
					} else if(inboundEvent.getName().equals("delete")) {
						MongoClient db_cl = new MongoClient("localhost", 27017);
			    		BasicDBObject query = new BasicDBObject();
			    		DBCollection collection = db_cl.getDB("database_name").getCollection("273_Client_Object_DB");	
			    		String input = inboundEvent.readData(String.class);
			    		query.put("_id" , input );
			     	    collection.remove(query);
					
					} else if(inboundEvent.getName().equals("empty")){
						break;
					}
				} else {
					System.out.println("event closed");
					break;
				}
			}
		}

    //System.out.println("connection closed");
	}
	static void read_Call(String input){
		input = input + ", 1000";
		Client cl1 = ClientBuilder.newClient();
		WebTarget tar1 = cl1.target("http://localhost:8080/273_Proj_Server/api/events/read");
		Response response = tar1.request(MediaType.TEXT_PLAIN).put(Entity.text(input));
		
		
	}
}
	



