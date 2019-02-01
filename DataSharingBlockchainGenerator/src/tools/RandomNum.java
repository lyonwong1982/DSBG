package tools;

import java.util.Random;

/***
 * Random number creator.
 * 
 * @author lyonwong
 *
 */
public class RandomNum {
	/***
	 * Get a random number between min and max.
	 * @param min
	 * @param max
	 * @return
	 */
	public static long getRandomInt(long min, long max) {
		Random random = new Random();
		long randomNum = min + (((long) (random.nextDouble() * (max - min))));
		return randomNum;
	}
	/***
	 * Get a random number set between min and max.
	 * @param min
	 * @param max
	 * @param size
	 * @return
	 */
	public static int[] getRandomIntSet(int min, int max, int size) {
		Random random = new Random();
		int[] randomNumSet = new int[size];
		for(int i=0; i<size; i++) {
			randomNumSet[i] = min + (((int) (random.nextDouble() * (max-min))));
			for(int j=0; j<i; j++) {
				if(randomNumSet[j] == randomNumSet[i]) {
					i--;
					break;
				}
			}
		}
		return randomNumSet;
	}
}
