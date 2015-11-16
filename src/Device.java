
public class Device {
	public int object_id = 3; /* from OMA specification */
	public int instance_id = 1;
	public Resource manufacturer;
		//public Resource flow_rate;
	public Resource currpressure;
	public Resource hardware_ver;
	//public Resource min_temp;
	//public Resource max_temp;
	public Resource minpressure;
	public Resource maxpressure;
	
	Device() {
		manufacturer = new Resource(ResourceId.MANUFACTURER);
		currpressure = new Resource(ResourceId.CURRPRESSURE);
		minpressure = new Resource(ResourceId.MINPRESSURE);
		maxpressure = new Resource(ResourceId.MAXPRESSURE);
		hardware_ver = new Resource(ResourceId.HARDWARE_VERSION);
	
	}
	
	public String objInstances(){
		String objinst;
		objinst = objid(manufacturer.id.valueOf());
		objinst = objinst + ","+objid(currpressure.id.valueOf());
		objinst = objinst + ","+objid(minpressure.id.valueOf());
		objinst = objinst + ","+objid(maxpressure.id.valueOf());
		//objinst = objinst + ","+objid(min_temp.id.valueOf());
		//objinst = objinst + ","+objid(max_temp.id.valueOf());
		return objinst;
	}
	
	public String objid(Integer res){
		String objs = "<"+object_id+"/"+instance_id+"/"+res+">";
		return objs;
		
	}
}
