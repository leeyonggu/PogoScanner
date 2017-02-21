import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.StringTokenizer;
import java.util.Vector;

class MyPokemonComparator implements Comparator {
	public int compare(Object a, Object b) {
		return ((MyPokemon)a).ivPercent < ((MyPokemon)b).ivPercent ? 1 : -1; 
	}
}

public class PogoScanner {
	Vector<PokeStop> pokeStops;
	Vector<ScannerAccount> accounts;
	Vector<MyPokemon> pokemons;
	int pokeStopsPerAccount;
	int scanCount;
	
	public PogoScanner() {
		pokeStops = new Vector<PokeStop>();
		accounts = new Vector<ScannerAccount>();
		pokemons = new Vector<MyPokemon>();
		this.pokeStopsPerAccount = 0;
		this.scanCount = 0;
	}
	
	public void Initialize() {
		// load data from csv
		Constants.Initialize();
		this.loadScannerAccounts();
		this.pokeStopsPerAccount = Constants.pokeStops.size() / accounts.size();
		
		// login
		for (int i = 0; i < accounts.size(); ++i) {
			ScannerAccount sa = accounts.get(i);
			sa.Login();
			sa.SetPokestopIndex(this.pokeStopsPerAccount * i, this.pokeStopsPerAccount * (i + 1) - 1);
		}
	}
	
	public void StartScanning() {
		while (true) {
			++this.scanCount;
			//if (this.scanCount == 2) break;
			System.out.println("\nScan Count: " + this.scanCount);
			
			try {
				long start = System.nanoTime();
				
				this.GetCatchablePokemons();
				this.PrintCatchablePokemons();
				
				long end = System.nanoTime();
				double elapsedInSec = (end - start) / 1000000000;
				log(">>>>> Scan Time Elapsed: " + elapsedInSec);
				
				Thread.sleep(Constants.REFRESH_INTERVAL_SEC * 1000);	
			}
			catch (Exception e) {
				e.printStackTrace();
			}	
		}
	}
	
	public void GetCatchablePokemons() {
		log("===== Start GetCatchablePokemons");
		this.pokemons.clear();
		
		// account별로 thread를 만들어서 처리한다
		ArrayList<Thread> threadList = new ArrayList<Thread>();
		for (int i = 0; i < accounts.size(); ++i) {
			ScannerAccount sa = accounts.get(i);
			Thread thread = new Thread(sa);
			thread.start();
			threadList.add(thread);
		}
		
		// 모든 thread가 작업을 완료할 때까지 기다린다
		for (Thread thread : threadList) {
			try {
				thread.join();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		// 각 account의 포켓몬을 하나로 모은다
		for (ScannerAccount sa : accounts) {
			this.pokemons.addAll(0, sa.pokemons);
		}
		
		// sort by iv percent
		Collections.sort(this.pokemons, new MyPokemonComparator());
		
		log("===== End GetCatchablePokemons");
	}
	
	public void PrintCatchablePokemons() {
		for (MyPokemon mp : this.pokemons) {
			System.out.println(mp);
		}
	}
	
	private void loadScannerAccounts() {
		//System.out.println("load scanner account info, current dir: " + System.getProperty("user.dir"));
		String scannerAccountsFilePath = "./data/google_accounts.csv";
		try {
			BufferedReader in = new BufferedReader(new FileReader(scannerAccountsFilePath));
			String line;
			int numOfAccounts = 0;
			while((line = in.readLine()) != null) {
				if (line.startsWith("#")) {
					continue;
				}
				
				String[] fields = line.split(",");
				ScannerAccount account = new ScannerAccount(fields[0], fields[1]);
				accounts.add(account);
				numOfAccounts++;
				
				//if (1 == numOfAccounts) break; // Captcha Test
			}
			in.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void log(String msg) {
		System.out.println("[PogoScanner] " + msg);
	}
}
