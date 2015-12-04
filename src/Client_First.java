import java.net.UnknownHostException;

public class Client_First {
	
		String client_name;// Serial number end pt client name
		String client_id;
		String sup_bind_modes;
		
		public static void main(String[] args) throws UnknownHostException, InterruptedException { 
			// TODO Auto-generated method stub
		 // client_id = "ID1";
			Client1 cl = new Client1();
			//if(args[0] .equals("1"))
			{
				String endpt = "urn:esn:"+"Clyyy772hj";
				cl.client_init_bootstrap(endpt, "INTLS1", "INTEL");
			/*	Thread.sleep(2000);
				cl.register(endpt);
				Thread.sleep(5000);
				System.out.println("Update lifetime to 74566");
				cl.register_update(endpt, "74566");
				Thread.sleep(5000);
				cl.deregister(endpt);*/
			}
		//	else if(args[0].equals("2")){
			//	cl.b
		//	}
		}

	}


