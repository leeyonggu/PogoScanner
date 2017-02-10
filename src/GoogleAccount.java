import java.util.Scanner;
import java.util.Set;

import com.pokegoapi.api.PokemonGo;
import com.pokegoapi.api.map.pokemon.CatchablePokemon;
import com.pokegoapi.auth.GoogleAutoCredentialProvider;
import com.pokegoapi.auth.GoogleUserCredentialProvider;
import com.pokegoapi.auth.PtcCredentialProvider;
import com.pokegoapi.util.hash.HashProvider;

import okhttp3.OkHttpClient;

public class GoogleAccount {
	public static void getRefreshToken() {
		OkHttpClient http = new OkHttpClient();
		try {
			// google account login
			GoogleUserCredentialProvider provider = new GoogleUserCredentialProvider(http);
			System.out.println("Please go to " + GoogleUserCredentialProvider.LOGIN_URL);
			System.out.println("Enter authorization code:");
			Scanner sc = new Scanner(System.in);
			String access = sc.nextLine();
			provider.login(access);
			String refreshToken = provider.getRefreshToken();
			System.out.println("Refresh token:" + refreshToken);
			
		}
		//catch (LoginFailedException | RemoteServerException | CaptchaActivateException e) {
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void ptcLoginTest() {
		OkHttpClient http = new OkHttpClient();
		try {
			HashProvider hasher = Constants.getHashProvider();
			PokemonGo go = new PokemonGo(http);
			String id = "ptcpogotest1";
			String password = "Gkdlaos3#";
			go.login(new PtcCredentialProvider(http, id, password), hasher);
			
			go.setLocation(Constants.LATITUDE, Constants.LONGITUDE, 0.0);
			Set<CatchablePokemon> catchablePokemon = go.getMap().getMapObjects().getPokemon();
			System.out.println("Pokemon in area: " + catchablePokemon.size());
			System.out.println("Captcha: " + go.hasChallenge());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void autoLoginTest() {
		OkHttpClient http = new OkHttpClient();
		try {
			HashProvider hasher = Constants.getHashProvider();
			PokemonGo go = new PokemonGo(http);
			//go.login(new GoogleAutoCredentialProvider(http, "ygleepogotest1@gmail.com", "gkdlaos5"), hasher);
			go.login(new GoogleAutoCredentialProvider(http, "ygleepogotest3@gmail.com", "gkdlaos5"), hasher);
			
			go.setLocation(Constants.LATITUDE, Constants.LONGITUDE, 0.0);
			Set<CatchablePokemon> catchablePokemon = go.getMap().getMapObjects().getPokemon();
			System.out.println("Pokemon in area: " + catchablePokemon.size());
			System.out.println("Captcha: " + go.hasChallenge());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
