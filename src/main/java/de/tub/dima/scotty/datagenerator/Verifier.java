package de.tub.dima.scotty.datagenerator;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * Created by philipp on 6/13/17.
 */
public class Verifier {

	public static void main(String[] args) throws Exception {

		String path = args[0];
		int timeIndex = Integer.valueOf(args[1]);

		long numberOfEvents = 0;
		long numberOfOutOfOrderEvents = 0;
		long maxDelay = 0;
		long maxTS = 0;

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
		String[] tuple = readNextTuple(reader);
		while (tuple != null) {
			numberOfEvents++;
			Long ts = Long.valueOf(tuple[timeIndex]);
			maxTS = Math.max(maxTS, ts);
			if (ts < maxTS) {
				//out Of order
				numberOfOutOfOrderEvents++;
				maxDelay = Math.max(maxDelay, maxTS - ts);
			}
			tuple = readNextTuple(reader);
		}

		System.out.println("events : " + numberOfEvents);
		System.out.println("outOfOrderEvents : " + numberOfOutOfOrderEvents);
		System.out.println("outOfOrder % : " + ((100.0 / numberOfEvents) * numberOfOutOfOrderEvents));
		System.out.println("maxDelay: " + maxDelay);


	}

	private static String[] readNextTuple(BufferedReader reader) throws Exception {
		final String line = reader.readLine();
		if (line == null)
			return null;
		return line.split(",");
	}

}
