
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;

//import BootStrap.ClientInput;

import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.*;

public class Client1 {
	Device dev1;
	Client1() {
		dev1 = new Device();
	}
	
	/*public void factory_bootstrap(String client_id) {
		MongoClient mongo1;
		String client_name = "INTLS1";
		dev1.manufacturer.value = "INTEL";
		dev1.temperature.value = "100";
		dev1.flow_rate.value = "150";
		dev1.pressure.value = "200";
		dev1.hardware_ver.value = "1.1";
		mongo1 = new MongoClient("localhost", 27017);
		DB db = mongo1.getDB("database_name");
		DBCollection table = db.getCollection("273_Client_assgn1");
		BasicDBObject document = new BasicDBObject();
		document.put("_id", client_id);
		document.put("client_name", client_name);
		document.put("manufacturer_id",dev1.manufacturer.value);
	    document.put("pressure",dev1.pressure.value);
   	    document.put("temperature",dev1.temperature.value);
	    document.put("flow_rate", dev1.flow_rate.value);
	    document.put("hardware_ver", dev1.hardware_ver.value); 
	    table.insert(document);
		System.out.println("Factory Bootstrapping Done");
		/*dev1.min_temp.value = "80";
		dev1.max_temp.value = "100";
		dev1.min_pressure.value = "180";
		dev1.max_pressure.value = "250";*/

	//n}
	
	public void client_init_bootstrap(String client_id, String client_name, String manufacturer_id){
		MongoClient mongo1;
		try {
		//	System.out.println("client_id");
			String obj_json = "{\"client_name\":\""+client_name+"\",\"manufacturer_id\":\""+manufacturer_id+"\"}";
			mongo1 = new MongoClient("localhost", 27017);
			DB db = mongo1.getDB("database_name");
			DBCollection table = db.getCollection("273_Client_Bootstrap");
			BasicDBObject document = new BasicDBObject();
			Client cl1 = ClientBuilder.newClient();
			WebTarget tar1 = cl1.target("http://localhost:8080/273_Assgn_1_Server/api/Bootstrap");
			Response response = tar1.request(MediaType.APPLICATION_JSON_TYPE).post(Entity.json(obj_json));
			ObjectMapper objMap = new ObjectMapper();
			objMap.setVisibility(PropertyAccessor.ALL, Visibility.ANY);
			BootStrap_Data clRecord = objMap.readValue(response.readEntity(String.class), BootStrap_Data.class); //deserialize JSON
			document.put("_id", client_id);
			document.put("client_name", client_name);
			document.put("manufacturer_id",manufacturer_id);
		    document.put("minpressure", clRecord.minpressure);
		    document.put("maxpressure", clRecord.maxpressure);
		    document.put("currpressure", clRecord.currpressure);
		    document.put("hardware_ver", clRecord.hardware_ver); 
		    table.insert(document);
		    dev1.manufacturer.value = manufacturer_id;
			
			dev1.currpressure.value = clRecord.currpressure;
			dev1.hardware_ver.value = clRecord.hardware_ver;
			dev1.minpressure.value = clRecord.minpressure;
			dev1.maxpressure.value = clRecord.maxpressure;
			
		    register(client_id);
		    
			System.out.println("Bootstrapping Done for"+client_id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

public void register(String client_id) throws UnknownHostException{
	String reg_input;
	String objects = dev1.objInstances();
	
	reg_input = "{";   //\"client_name\":\""+client_id
	reg_input = reg_input+Jsonstr("_id", client_id);
	reg_input = reg_input+","+Jsonstr("lifetime", "90000");
	reg_input = reg_input+","+Jsonstr("version", "1.1");
	reg_input = reg_input+","+Jsonstr("binding_mode","U");	
	reg_input = reg_input+","+Jsonstr("objects",objects);
	reg_input = reg_input+"}";
	Client cl1 = ClientBuilder.newClient();
	WebTarget tar1 = cl1.target("http://localhost:8080/273_Assgn_1_Server/api/register");
	Response response = tar1.request(MediaType.APPLICATION_JSON_TYPE).
		 			    		post(Entity.json(reg_input));
	System.out.println("Input to the Server:"+reg_input);
	System.out.println("Registered-"+response.readEntity(String.class));
 	
}
public void register_update(String client_id, String update_str){
	String upd_str = "{"+Jsonstr("client_id", client_id)+","+Jsonstr("lifetime", update_str)+"}";
	Client cl1 = ClientBuilder.newClient();
	WebTarget tar1 = cl1.target("http://localhost:8080/273_Assgn_1_Server/api/update_reg");
	Response response = tar1.request(MediaType.APPLICATION_JSON_TYPE).
	    		put(Entity.json(upd_str));
	System.out.println("Input to the Server:"+upd_str);
	System.out.println("Updated-"+response.readEntity(String.class));
	
}

public void deregister(String client_id){
	
	Client cl1 = ClientBuilder.newClient();
	WebTarget tar1 = cl1.target("http://localhost:8080/273_Assgn_1_Server/api/deregister");
	Response response = tar1.request(MediaType.APPLICATION_JSON_TYPE).
    		put(Entity.json("{"+Jsonstr("_id", client_id)+"}"));
	System.out.println("Input to the Server:"+"{"+Jsonstr("_id", client_id)+"}");
	System.out.println("De-register-"+response.readEntity(String.class));
	
}

public String Jsonstr(String key, String value) {
	String str = "";
	str = "\""+key+"\":"+"\""+value+"\"";
	return str;
}

}
@JsonIgnoreProperties(ignoreUnknown = true)
class BootStrap_Data{
	public String client_id = "SV";
	public String client_name = "SV";
	public String manufacturer_id = "SV";
	public String minpressure = "SV";
	public String maxpressure = "SV";
	public String currpressure = "ee";
	public String hardware_ver = "SV";
//	public String min_temp = "SV";
//	public String max_temp = "SV";
	
}
