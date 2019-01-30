package tools;

import java.util.Random;

/***
 * Random number creator.
 * 
 * @author lyonwong
 *
 */
public class RandomNum {
	public static long getRandomInt(long min, long max) {
		Random random = new Random();
		long randomNum = min + (((long) (random.nextDouble() * (max - min))));
		return randomNum;
	}
}
