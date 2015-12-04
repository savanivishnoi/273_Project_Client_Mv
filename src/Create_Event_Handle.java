import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Future;

import javax.ws.rs.*;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.client.*;
import org.glassfish.jersey.internal.util.collection.StringKeyIgnoreCaseMultivaluedMap;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.glassfish.jersey.media.sse.OutboundEvent;
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
	static int pressure_min = -1;
	static int pressure_max = -1;
	static int pressure_step = -1;
	static int pressure_current = -1;
	static int pressure_prev = -1;
	static boolean observe = false;
	static String _id;
	static String client_id;
	static String object_id;
	
	
	public static void main(String args[]) throws InterruptedException, IOException{
		Client client = ClientBuilder.newBuilder().register(SseFeature.class).build();
		String[] arr_input;
		String inboundEN, inboundED;
		int pressure_status;
		MongoClient db_cl = new MongoClient("localhost", 27017);
		// read bootstrap data for initializing min max etc..
		WebTarget target = client.target("http://localhost:8080/273_Proj_Server/api/events/all");
		while(true) {
			Thread.sleep(1000);
		//	System.out.println("in while" +observe);
			if (observe == true) {
			//	System.out.println("observer true"+pressure_min);
				pressure_prev = pressure_current;
				pressure_current = device_get_current_pressure();
			//	System.out.println(pressure_current);
				if(pressure_min > 0 && (pressure_current < pressure_min)) {
					pressure_status = -1;
					send_notify(pressure_status, pressure_current);
				} 
				if (pressure_max > 0 && (pressure_current > pressure_max)) {
					pressure_status = 1;
					send_notify(pressure_status , pressure_current);
				}
				if (pressure_step > 0) {
				//	System.out.println(pressure_current + "," + pressure_prev + "," + pressure_step);
					if(Math.abs(pressure_current - pressure_prev) > pressure_step) {
						pressure_status = 100;
						send_notify(pressure_status, pressure_current);
					}
				}
			}
			EventInput eventInput = target.request().get(EventInput.class);
			while (!eventInput.isClosed()) {
				final InboundEvent inboundEvent = eventInput.read();
				if (inboundEvent != null) {
					inboundEN = inboundEvent.getName();
					inboundED = inboundEvent.readData(String.class);
					if(!inboundEN.equals("empty") && !inboundED.equals("empty")){
						System.out.println(inboundEvent.getName() + "; " + inboundEvent.readData(String.class));
					}
					if(inboundEvent.getName().equals("create")) {
			    		DBObject dbObject = (DBObject)JSON.parse(inboundEvent.readData(String.class));
			    		getDBCollection(db_cl).insert(dbObject);
					} else if(inboundEvent.getName().equals("read")) {
						String input = inboundEvent.readData(String.class);
						read_Call(input);
					
					} else if(inboundEvent.getName().equals("update")) {
			    		BasicDBObject object = new BasicDBObject();
			    		String input = inboundEvent.readData(String.class);
			    		arr_input = input.split(",");
			    		object.append("$set", new BasicDBObject().append("value", arr_input[1]));		
			     		BasicDBObject searchQuery = new BasicDBObject().append("_id", arr_input[0]);
			     		getDBCollection(db_cl).update(searchQuery, object );
					
					} else if(inboundEvent.getName().equals("delete")) {
			    		BasicDBObject query = new BasicDBObject();
			    		String input = inboundEvent.readData(String.class);
			    		query.put("_id" , input );
			    		getDBCollection(db_cl).remove(query);
					
					} else if(inboundEvent.getName().equals("observe")) {
						observe = true;
					
					} else if(inboundEvent.getName().equals("write_attributes")) {
			    		String input = inboundEvent.readData(String.class);
			    	//	System.out.println(input+"write attr");
			    		write_attributes_process(input);
					
					} else if(inboundEvent.getName().equals("execute")) {
			    		String input = inboundEvent.readData(String.class);
			    	//	System.out.println(input+"execute");
			    		execute_process(input, input);
					
					} else if(inboundEvent.getName().equals("discover")) {
			    		String input = inboundEvent.readData(String.class);
			    	//	System.out.println(input+"discover");
			    		handle_discover(input);
			    		//write_attributes_process(input);
					
					} else if(inboundEvent.getName().equals("readx")){
						String input = inboundEvent.readData(String.class);
						readx_call(input);
					}
					else if(inboundEvent.getName().equals("empty")){
						break;
					}
				} else {
					//System.out.println("event closed");
					break;
				}
			}
		}

    //System.out.println("connection closed");
	}
	
	static DBCollection getDBCollection(MongoClient db_cl) {
		return db_cl.getDB("database_name").getCollection("273_Client_Object_DB");	
	}
	
	static void read_Call(String input){
		input = input + "," + randomGen();;
		Client cl1 = ClientBuilder.newClient();
		WebTarget tar1 = cl1.target("http://localhost:8080/273_Proj_Server/api/events/read");
		tar1.request(MediaType.TEXT_PLAIN).put(Entity.text(input));
	}
	static void readx_call(String input){
		System.out.println("inside readx_call");
		input = input + ","+ randomGen();
		Client cl1 = ClientBuilder.newClient();
		WebTarget tar1 = cl1.target("http://localhost:8080/273_Proj_Server/api/events/readx");
		tar1.request(MediaType.TEXT_PLAIN).put(Entity.text(input));
	}
	
	static int device_get_current_pressure(){
		int press;
		press = 120;
		return press;
	}
	static void send_notify(int pressure_status, int press_current){
		//System.out.println("notify");
		String input =  _id + "," + client_id + "," + object_id+"," +press_current+ ","+ pressure_status;
		Client cl1 = ClientBuilder.newClient();
		final AsyncInvoker asyncInvoker = cl1.target("http://localhost:8080/273_Proj_Server/api/events/notify").request(MediaType.TEXT_PLAIN).async();
		final Future<Response> responseFuture = asyncInvoker.put(Entity.text(input));
		//WebTarget tar1 = cl1.target("http://localhost:8080/273_Proj_Server/api/events/notify");
		//tar1.request(MediaType.TEXT_PLAIN).put(Entity.text(input));
	}
	static void write_attributes_process(String input){
		String[] arr_input;
		arr_input = input.split(",");
		int len = arr_input.length;
		_id = arr_input[0];
		client_id = arr_input[1];
		object_id = arr_input[2];
	//	System.out.println("write att" + arr_input[3]);
		pressure_min = Integer.parseInt(arr_input[3]);
		pressure_max = Integer.parseInt(arr_input[4]);
		pressure_step = Integer.parseInt(arr_input[5]);
	//	System.out.println(pressure_min + "," + pressure_max + "," + pressure_step);
		if(len == 7){
			//System.out.println("len 6");
			observe = false;
		}
	}
	static void execute_process(String pressure_status, String press_current) throws InterruptedException, IOException {
		System.out.println("execute");
		//String input =  _id + "," + client_id + "," + object_id+"," +press_current+ ","+ pressure_status;
		//ProcessBuilder pb = new ProcessBuilder("echo", "Rebooting device");
		//pb.start().waitFor();
		Client cl1 = ClientBuilder.newClient();
		WebTarget tar1 = cl1.target("http://localhost:8080/273_Proj_Server/api/events/execute");
		tar1.request(MediaType.TEXT_PLAIN).put(Entity.text("Operation is not supported"));
	}
	static void handle_discover(String input) {
		 MongoClient db_cl1 = new MongoClient("localhost", 27017);
		 DBCollection collection = db_cl1.getDB("database_name").getCollection("273_Reg_Data");	
		 BasicDBObject dbo =  new BasicDBObject();
		 BasicDBObject fld = new BasicDBObject();
		   
		 dbo.put("_id", input);
		 fld.put("object", 1);
		 DBObject cursor1 = collection.findOne(dbo,fld);
		 System.out.println(input+"input of dicsover");

		
		String json_op = JSON.serialize(cursor1);
		System.out.println("reg data"+json_op);
		Client cl1 = ClientBuilder.newClient();
		WebTarget tar1 = cl1.target("http://localhost:8080/273_Proj_Server/api/events/discover");
		Response response = tar1.request(MediaType.TEXT_PLAIN).put(Entity.text(json_op));
	}
	static int randomGen(){
		Random lo_random = new Random();
		int start = 100;
		int end = 1000;
		return ( lo_random.nextInt(700) + start);
		
		
	}
}