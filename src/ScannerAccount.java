import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.Vector;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.api.map.pokemon.encounter.EncounterResult;
import com.pokegoapi.auth.GoogleUserCredentialProvider;
import com.pokegoapi.exceptions.CaptchaActiveException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.NoSuchItemException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokegoapi.exceptions.hash.HashException;

import POGOProtos.Data.PokemonDataOuterClass.PokemonData;
import okhttp3.OkHttpClient;

public class ScannerAccount implements Runnable {
	public String googleAccountName;
	public String refreshToken;
	public Vector<MyPokemon> pokemons;
	private PokemonGo go;
	private int pokeStopStartIndex;
	private int pokeStopEndIndex;
	private int catchCount;
	
	public ScannerAccount(String googleAccountName, String refreshToken) {
		this.googleAccountName = googleAccountName;
		this.refreshToken = refreshToken;
		this.pokemons = new Vector<MyPokemon>();
		this.go = null;
		this.catchCount = 0;
	}
	
	public void Login() {
		System.out.println("Login Start, account: " + this.googleAccountName);
		
		OkHttpClient http = new OkHttpClient();
		try {
			go = new PokemonGo(http);
			go.login(new GoogleUserCredentialProvider(http, refreshToken), Constants.getHashProvider());
			
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
				log("========== Captcha: " + go.hasChallenge());				
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


			Set<CatchablePokemon> catchablePokemon = go.getMap().getMapObjects().getPokemon();
			log("Pokemon in area: " + catchablePokemon.size());
			
			// get pokemons
			for (CatchablePokemon cp : catchablePokemon) {
				// Captcha Test
				/*
				MyPokemon mp = new MyPokemon(cp.getPokemonIdValue() + "", cp.getPokemonId().toString(), 10, 10, 10);
				mp.SetLocation(cp.getLatitude(), cp.getLongitude());
				this.pokemons.add(mp);
				log(mp.toString());
				*/
		
				if (true) {
					EncounterResult encResult = cp.encounterPokemon();
					if (encResult.wasSuccessful()) {
						PokemonData pokeData = encResult.getPokemonData();
						MyPokemon mp = new MyPokemon(cp.getPokemonIdValue() + "", cp.getPokemonId().toString(), pokeData.getIndividualAttack(), pokeData.getIndividualDefense(), pokeData.getIndividualStamina());
						mp.SetLocation(cp.getLatitude(), cp.getLongitude());
						this.pokemons.add(mp);
						log(mp.toString());
					}
				}
			}
			
			// Captcha Test
			Thread.sleep(3000);
			
			long end = System.nanoTime();
			long elapsedInMs = (end - start) / 1000000;
			log("Pokestop processing time: " + (double)elapsedInMs/1000 + "sec");
		}
		
		log("End GetCatchablePokemons");
	}
	
	private void log(String msg) {
		System.out.println("[ScannerAccount] " + this.googleAccountName + ", " + msg);
	}

	@Override
	public void run() {
		try {
			this.GetCatchablePokemons();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
