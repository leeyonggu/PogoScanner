import okhttp3.OkHttpClient;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.api.pokemon.Pokemon;
import com.pokegoapi.auth.GoogleUserCredentialProvider;
import com.pokegoapi.exceptions.CaptchaActiveException;
import com.pokegoapi.exceptions.LoginFailedException;
import com.pokegoapi.exceptions.NoSuchItemException;
import com.pokegoapi.exceptions.RemoteServerException;
import com.pokegoapi.util.hash.pokehash.PokeHashProvider;

import java.util.Scanner;
import java.util.Set;

public class Main {
	private static void getCatchablePokemons(PokemonGo go) throws LoginFailedException, CaptchaActiveException, RemoteServerException, NoSuchItemException, InterruptedException {
		System.out.println("--- Start getCatchablePokemons");
		
		// wait until map is updated
		go.getMap().awaitUpdate();
		System.out.println("--- map awaiting finished");
		
		Set<CatchablePokemon> catchablePokemon = go.getMap().getMapObjects().getPokemon();
		System.out.println("Pokemon in area: " + catchablePokemon.size());
		
		for (CatchablePokemon cp : catchablePokemon) {
			System.out.println("catcable pokemon: " + cp.toString());
		}
		System.out.println("--- End getCatchablePokemons");

	}
	
	public static void main(String[] args) {
		OkHttpClient http = new OkHttpClient();
		try {
			// google account login
			/*
			GoogleUserCredentialProvider provider = new GoogleUserCredentialProvider(http);
			System.out.println("Please go to " + GoogleUserCredentialProvider.LOGIN_URL);
			System.out.println("Enter authorization code:");
			Scanner sc = new Scanner(System.in);
			String access = sc.nextLine();
			provider.login(access);
			String refreshToken = provider.getRefreshToken();
			System.out.println("Refresh token:" + refreshToken);
			*/
			
			String refreshToken = "1/bQrhquBUelo1_XI-MU-Kza4asTkCxWwbF1K3Wgd6Y1c";
			String POKEHASH_KEY = "";

			// pogo login
			PokemonGo go = new PokemonGo(http);
			go.login(new GoogleUserCredentialProvider(http, refreshToken), Constants.getHashProvider());
			
			// set location
			go.setLocation(Constants.LATITUDE, Constants.LONGITUDE, Constants.ALTITUDE);
			
			// get catchable pokemons in this area
			getCatchablePokemons(go);
		}
		//catch (LoginFailedException | RemoteServerException | CaptchaActivateException e) {
		catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("End");
	}
}
