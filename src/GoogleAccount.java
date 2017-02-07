import java.util.Scanner;
import com.pokegoapi.auth.GoogleUserCredentialProvider;
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
}
