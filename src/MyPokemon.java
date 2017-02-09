
public class MyPokemon {
	public String id;
	public String name;
	public int ivAttack;
	public int ivDefense;
	public int ivStamina;
	public double latitude;
	public double longitude;
	public int ivPercent;
	
	public MyPokemon(String id, String name, int ivAttack, int ivDefense, int ivStamina) {
		this.id = id;
		this.name = name;
		this.ivAttack = ivAttack;
		this.ivDefense = ivDefense;
		this.ivStamina = ivStamina;
		this.latitude = 0.0;
		this.longitude = 0.0;
		this.ivPercent = this.GetIvInPercentage();
	}
	
	public void SetLocation(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public int GetIvInPercentage() {
		double ivRatio = (this.ivAttack + this.ivDefense + this.ivStamina) / 45.0;
		return (int)((Math.floor((ivRatio * 100) * 100)) / 100);
	}
	
	public String toString() {
		return "MyPokemon, id: " + id + ", name: " + name + ", IV: (" + this.ivAttack + ", " + this.ivDefense + ", " + this.ivStamina + "), "
				+ "IVPercent: " + this.GetIvInPercentage() + "%, latitude: " + this.latitude + ", longitude: " + this.longitude;
	}

}
