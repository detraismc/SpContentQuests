package dark.detraismc.detraisequipment.utils;

public class MathUtils {

	
	public boolean getChance(int chance, int per) {
		int woe = ((int) (Math.random()*(per - 1))) + 1;
		  if (woe < chance) {
			  return true;
		  } else {
			  return false;
		  }
	}
	
	public int getRandomNumber(int min, int max) {
		int woe = ((int) (Math.random()*(max - min))) + min;
		return woe;
	}
	
}
