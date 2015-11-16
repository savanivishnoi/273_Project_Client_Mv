
enum ResourceId {
	MANUFACTURER ("manufacturer", 11001),
	CURRPRESSURE ("currpressure", 11002),
	MINPRESSURE ("minpressure", 11003),
	MAXPRESSURE ("maxpressure", 11004),
	HARDWARE_VERSION ("hardware version", 5);
	
	private final String name;
	private final Integer value;
	
	ResourceId(String name, Integer value) {
        this.name = name;
        this.value = value;
    }
	
    public String nameOf() { return name; }
    public Integer valueOf() { return value; }
};

public class Resource {
	public String name;
	public ResourceId id;
	public String desc; 
	public String value;
	public Integer instance_id = 1;
	    
	Resource(ResourceId i) {
		id = i;
		name = i.nameOf();
	}
};
