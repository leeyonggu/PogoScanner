import com.pokegoapi.util.hash.HashProvider;
import com.pokegoapi.util.hash.legacy.LegacyHashProvider;
import com.pokegoapi.util.hash.pokehash.PokeHashProvider;

public class Constants {
	public static final String LOGIN = "";
	public static final String PASSWORD = "";
	
	// 광성교회
	public static final double LATITUDE = 37.477380;
	public static final double LONGITUDE = 126.963337;

	// 해양청소년단
	/*
	public static final double LATITUDE = 37.477042;
	public static final double LONGITUDE = 126.964217;
	*/
	
	// 참평안교회
	/*
	public static final double LATITUDE = 37.476708;
	public static final double LONGITUDE = 126.965117;
	*/
	
	public static final double ALTITUDE = 0.0;
	public static final String POKEHASH_KEY = "";

	/**
	 * Creates the appropriate hash provider, based on if the POKEHASH_KEY property is set or not
	 * @return a hash provider
	 */
	public static HashProvider getHashProvider() {
		boolean hasKey = POKEHASH_KEY != null && POKEHASH_KEY.length() > 0;
		if (hasKey) {
			return new PokeHashProvider(POKEHASH_KEY);
		} else {
			return new LegacyHashProvider();
		}
	}
}
