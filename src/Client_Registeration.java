
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import javax.ws.rs.client.Entity;
public class Client_Registeration {
   static String client_id, 
   				 lwm2m_ver;
   
	
	public static void main(String[] args) throws UnknownHostException {
		// TODO Auto-generated method stub
		   // client_id = args[0];
		    lwm2m_ver = "1.2";
		    
		    InetAddress address = InetAddress.getLocalHost();
		     InetSocketAddress registrationAddress = InetSocketAddress.createUnresolved("localhost", 5683);
		     registrationAddress = InetSocketAddress.createUnresolved("localhost", 5683);
		    System.out.println(registrationAddress);
		    String ape = "ehj";
		   String reg_input = "{ \"client_name\":"+ape+"}";
		   
		   System.out.println(Entity.json(reg_input));

	}

}
