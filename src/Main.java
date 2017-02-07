public class Main {
	public static void main(String[] args) {
		int refreshIntervalInSec = 10;
		PogoScanner ps = new PogoScanner(refreshIntervalInSec);
		ps.Initialize();
		ps.GetCatchablePokemons();
		
		//GoogleAccount.getRefreshToken();
	}
}