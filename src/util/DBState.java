package util;

public class DBState {
	private static boolean active = false;
	
	public static void set() {
		active = true;

	}
	
	public static boolean get() {
		return active;
	}


}