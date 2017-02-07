import java.util.Set;

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

public class ScannerAccount {
	public String googleAccountName;
	public String refreshToken;
	
	private PokemonGo go;
	
	public ScannerAccount(String googleAccountName, String refreshToken) {
		this.googleAccountName = googleAccountName;
		this.refreshToken = refreshToken;
		this.go = null;
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
		
		// set location
		go.setLocation(Constants.LATITUDE, Constants.LONGITUDE, Constants.ALTITUDE);
		
		System.out.println("Login End, account: " + this.googleAccountName);
	}
	
	public void GetCatchablePokemons() throws LoginFailedException, CaptchaActiveException, RemoteServerException, NoSuchItemException, InterruptedException, HashException {
		System.out.println("--- Start GetCatchablePokemons, acount: " + this.googleAccountName);
		
		// wait until map is updated
		go.getMap().awaitUpdate();
		System.out.println("--- map awaiting finished");
		
		Set<CatchablePokemon> catchablePokemon = go.getMap().getMapObjects().getPokemon();
		System.out.println("Pokemon in area: " + catchablePokemon.size());
		
		for (CatchablePokemon cp : catchablePokemon) {
			EncounterResult encResult = cp.encounterPokemon();
			if (encResult.wasSuccessful()) {
				PokemonData pokeData = encResult.getPokemonData();
				long pokemonId = pokeData.getId();
				int ivAttack = pokeData.getIndividualAttack();
				int ivDefense = pokeData.getIndividualDefense();
				int ivStamina = pokeData.getIndividualStamina();
				String nickName = pokeData.getNickname();
				System.out.println("nickname: " + cp.getPokemonId() + ", pokemon-id: " + cp.getPokemonIdValue() + ", IV: (" + ivAttack + ", " + ivDefense + ", " + ivStamina + ")");				
			}
		}
		System.out.println("--- End GetCatchablePokemons, account: " + this.googleAccountName);
	}
}
