import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

public class PogoScanner {
	public int refreshIntervalInSec;
	Vector<PokeStop> pokeStops;
	Vector<ScannerAccount> accounts;
	
	public PogoScanner(int refreshIntervalInSec) {
		this.refreshIntervalInSec = refreshIntervalInSec;
		pokeStops = new Vector<PokeStop>();
		accounts = new Vector<ScannerAccount>();
	}
	
	public void Initialize() {
		// load data from csv
		this.loadPokeStopInfo();
		this.loadScannerAccountInfo();
		
		// login
		for (ScannerAccount sa : accounts) {
			sa.Login();
		}
	}
	
	public void GetCatchablePokemons() {
		for (ScannerAccount sa : accounts) {
			try {
				sa.GetCatchablePokemons();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}		
	}
	
	private void loadPokeStopInfo() {
		//System.out.println("load pokestop info, current dir: " + System.getProperty("user.dir"));
		
		String pokeStopsFilePath = "./data/pokestops.csv";
		try {
			BufferedReader in = new BufferedReader(new FileReader(pokeStopsFilePath));
			String line;
			while((line = in.readLine()) != null) {
				String[] fields = line.split(",");
				PokeStop pstop = new PokeStop(fields[0], fields[1], Double.parseDouble(fields[2]), Double.parseDouble(fields[3]), Double.parseDouble(fields[4]));
				pokeStops.addElement(pstop);
			}
			in.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void printPokeStops() {
		for (PokeStop ps : pokeStops) {
			System.out.println(ps);
		}
	}
	
	private void loadScannerAccountInfo() {
		//System.out.println("load scanner account info, current dir: " + System.getProperty("user.dir"));
		String scannerAccountsFilePath = "./data/google_accounts.csv";
		try {
			BufferedReader in = new BufferedReader(new FileReader(scannerAccountsFilePath));
			String line;
			while((line = in.readLine()) != null) {
				//System.out.println("line: " + line);
				
				String[] fields = line.split(",");
				ScannerAccount account = new ScannerAccount(fields[0], fields[1]);
				accounts.addElement(account);
			}
			in.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
