import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import com.pokegoapi.util.hash.HashProvider;
import com.pokegoapi.util.hash.legacy.LegacyHashProvider;
import com.pokegoapi.util.hash.pokehash.PokeHashKey;
import com.pokegoapi.util.hash.pokehash.PokeHashProvider;


public class Constants {
	public static final int REFRESH_INTERVAL_SEC = 10;
	public static final String POKEHASH_KEY = "2I4F3V7X7F6A0S7K9D6P";
	
	// 광성교회
	public static final double LATITUDE = 37.477380;
	public static final double LONGITUDE = 126.963337;
	public static final double SPEED_KMPH = 28.0;
	
	public static Vector<PokeStop> pokeStops;
	
	public static void Initialize() {
		pokeStops = new Vector<PokeStop>();
		loadPokeStops();
		//printPokeStops();
	}
	
	private static void loadPokeStops() {
		//System.out.println("load pokestop info, current dir: " + System.getProperty("user.dir"));
		String pokeStopsFilePath = "./data/pokestop_raw/pokestops.csv";
		try {
			// read all lines
			ArrayList<String> lines = new ArrayList<String>();
			BufferedReader in = new BufferedReader(new FileReader(pokeStopsFilePath));
			String line;
			while((line = in.readLine()) != null) {
				if (line.startsWith("#")) {
					continue; // skip annotation
				}
				lines.add(line);
			}
			in.close();
			int numOfLines = lines.size();
			
			// sampling
			Random rand = new Random();
			int numOfSamples = numOfLines;
			for (int i = 0; i < numOfSamples; ++i) {
				/*
				// random sampling
				int sampleIndex = rand.nextInt(numOfLines - i);
				String temp = lines.get(i + sampleIndex);
				lines.add(i + sampleIndex, lines.get(i));
				lines.add(i, temp);
				*/
				
				// add select pokestop
				String sampledLine = lines.get(i);
				String[] fields = sampledLine.split(",");
				PokeStop pstop = new PokeStop(fields[0], fields[1], Double.parseDouble(fields[2]), Double.parseDouble(fields[3]), Double.parseDouble(fields[4]));
				pokeStops.addElement(pstop);	
			}
			
			System.out.println("Stops Loading Finished, # of stops: " + pokeStops.size());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void printPokeStops() {
		for (PokeStop ps : pokeStops) {
			System.out.println(ps);
		}
	}

	/**
	 * Creates the appropriate hash provider, based on if the POKEHASH_KEY property is set or not
	 * @return a hash provider
	 */
	public static HashProvider getHashProvider() {
		boolean hasKey = POKEHASH_KEY != null && POKEHASH_KEY.length() > 0;
		if (hasKey) {
			PokeHashProvider hash = new PokeHashProvider(PokeHashKey.from(POKEHASH_KEY), true);
			return hash;
		} else {
			return new LegacyHashProvider();
		}
	}
}
