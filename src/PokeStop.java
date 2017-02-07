
public class PokeStop {
	public String id;
	public String name;
	public double latitude;
	public double longitude;
	public double altitude;
	
	public PokeStop(String id, String name, double latitude, double longitude, double altitude) {
		this.id = id;
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
	}
	
	public String toString() {
		return "pokestop, id=" + id + ", name=" + name + ", latitude=" + latitude + ", longitude=" + longitude + ", altitude=" + altitude;
	}
}
