import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.Vector;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.Point;
import com.pokegoapi.api.map.fort.Pokestop;
import com.pokegoapi.api.map.fort.PokestopLootResult;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.api.map.pokemon.encounter.EncounterResult;
import com.pokegoapi.auth.GoogleAutoCredentialProvider;
import com.pokegoapi.auth.GoogleUserCredentialProvider;
import com.pokegoapi.exceptions.CaptchaActiveException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.NoSuchItemException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokegoapi.exceptions.hash.HashException;
import com.pokegoapi.util.path.Path;

import POGOProtos.Data.PokemonDataOuterClass.PokemonData;
import okhttp3.OkHttpClient;

public class ScannerAccount implements Runnable {
	public String googleAccountName;
	public String password;
	public Vector<MyPokemon> pokemons;
	private PokemonGo go;
	private int pokeStopStartIndex;
	private int pokeStopEndIndex;
	private int catchCount;
	
	public ScannerAccount(String googleAccountName, String password) {
		this.googleAccountName = googleAccountName;
		this.password = password;
		this.pokemons = new Vector<MyPokemon>();
		this.go = null;
		this.catchCount = 0;
	}
	
	public void Login() {
		System.out.println("Login Start, account: " + this.googleAccountName);
		
		OkHttpClient http = new OkHttpClient();
		try {
			go = new PokemonGo(http);
			go.login(new GoogleAutoCredentialProvider(http, googleAccountName, password), Constants.getHashProvider());
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Login End, account: " + this.googleAccountName);
	}
	
	public void SetPokestopIndex(int pokeStopStartIndex, int pokeStopEndIndex) {
		this.pokeStopStartIndex = pokeStopStartIndex;
		this.pokeStopEndIndex = pokeStopEndIndex;
	}
	
	private void lootPokestops()
		throws LoginFailedException, CaptchaActiveException, RemoteServerException, NoSuchItemException, InterruptedException, HashException {
		log("Start finding lootable pokestops");
		
		Set<Pokestop> pokestops = go.getMap().getMapObjects().getPokestops();
		Pokestop targetStop = null;
		for (Pokestop pokestop : pokestops) {
			if (pokestop.inRange() && pokestop.canLoot(true)) {
				targetStop = pokestop;
				break;
			}
		}
		if (null != targetStop) {
			log("lootable pokestop is found");
			PokestopLootResult result = targetStop.loot();
			log("pokestop loot result: " + result.getResult());
		}
	}
	
	private void getPokemonInfo() 
			throws LoginFailedException, CaptchaActiveException, RemoteServerException, NoSuchItemException, InterruptedException, HashException {
		Set<CatchablePokemon> catchablePokemon = go.getMap().getMapObjects().getPokemon();
		log("Pokemon in area: " + catchablePokemon.size());
		
		// get pokemons
		for (CatchablePokemon cp : catchablePokemon) {
			// Captcha Test
			if (false) {
				MyPokemon mp = new MyPokemon(cp.getPokemonIdValue() + "", cp.getPokemonId().toString(), 10, 10, 10);
				mp.SetLocation(cp.getLatitude(), cp.getLongitude());
				this.pokemons.add(mp);
				log(mp.toString());
			}
	
			if (true) {
				EncounterResult encResult = cp.encounterPokemon();
				if (encResult.wasSuccessful()) {
					PokemonData pokeData = encResult.getPokemonData();
					MyPokemon mp = new MyPokemon(cp.getPokemonIdValue() + "", cp.getPokemonId().toString(), pokeData.getIndividualAttack(), pokeData.getIndividualDefense(), pokeData.getIndividualStamina());
					mp.SetLocation(cp.getLatitude(), cp.getLongitude());
					this.pokemons.add(mp);
					log(mp.toString());
				}
				
				// Captcha Test
				//Thread.sleep(5000);
			}
		}
	}
	
	public void GetCatchablePokemons() 
			throws LoginFailedException, CaptchaActiveException, RemoteServerException, NoSuchItemException, InterruptedException, HashException {
		log("Start GetCatchablePokemons, stop-start-index: " + pokeStopStartIndex + ", stop-end-index: " + pokeStopEndIndex);
		
		// get pokemons for given pokestops
		this.pokemons.clear();
		for (int i = pokeStopStartIndex; i <= pokeStopEndIndex; ++i) {
			long start = System.nanoTime();
			
			// captcha check
			this.catchCount++;
			if (catchCount % 1 == 0) {
				if (go.hasChallenge()) {
					log("========== !!!!! Captcha True !!!!! ==========");	
					break;
				}
			}
			
			// set location
			PokeStop ps = Constants.pokeStops.get(i); 
			//PokeStop ps = Constants.pokeStops.get(0); // Captcha Test
			go.setLocation(ps.latitude, ps.longitude, ps.altitude);
			log("PokeStop: " + ps);

			// wait until map is updated
			log("Map awaiting started ...");
			long mapAwaitingStart = System.nanoTime();
			go.getMap().awaitUpdate();
			long mapAwaitingEnd = System.nanoTime();
			log("... Map awaiting finished, elapsed: " + (mapAwaitingEnd - mapAwaitingStart) / (double)1000000000 + "sec");
			//Thread.sleep(10000);
			
			this.lootPokestops();
			
			//this.getPokemonInfo();

			// Captcha Test
			//Thread.sleep(10000);
			
			long end = System.nanoTime();
			log("Pokestop processing time: " + (end - start) / (double)1000000000 + "sec");
		}
		
		log("End GetCatchablePokemons");
	}
	
	public void LootingPokestops() {
		log("Start Looting Pokestops");
		try {
			Point initialPoint = new Point(Constants.pokeStops.get(0).latitude, Constants.pokeStops.get(0).longitude);
			go.setLocation(initialPoint.getLatitude(), initialPoint.getLongitude(), 0.0);
			Thread.sleep(2000);
			
			for (int i = 1; i <= pokeStopEndIndex; ++i) {
				PokeStop dest = Constants.pokeStops.get(i);
				goToLocation(new Point(dest.latitude, dest.longitude));
				lootPokestops();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void goToLocation(Point dest) {
		Path path = new Path(go.getPoint(), dest, Constants.SPEED_KMPH);
		log("Traveling to " + dest + " at " + Constants.SPEED_KMPH + "KMPH!");
		path.start(go);
		try {
			while (!path.isComplete()) {
				Point point = path.calculateIntermediate(go);
				go.setLatitude(point.getLatitude());
				go.setLongitude(point.getLongitude());
				log("Time left: " + (int) (path.getTimeLeft(go) / 1000) + "seconds" );
				Thread.sleep(2000);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		log("Finished traveling to pokestop!");
	}
	
	private void log(String msg) {
		System.out.println("[ScannerAccount] " + this.googleAccountName + ", " + msg);
	}

	@Override
	public void run() {
		try {
			//this.GetCatchablePokemons();
			this.LootingPokestops();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
