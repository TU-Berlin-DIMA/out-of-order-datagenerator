package de.tub.dima.scotty.datagenerator;


import java.util.Random;

/**
 * Created by philipp grulich
 */
public class RandomGenerator {

	private final Random random;

	public RandomGenerator(int seed) {
		this.random = new Random(seed);
	}

	public long getInt(int bound) {
		return random.nextInt(bound);
	}


	public boolean getBoolean(final Integer persentage) {
		return random.nextInt(100) < persentage;
	}


}
