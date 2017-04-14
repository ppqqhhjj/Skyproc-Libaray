package com.mypatcher.utility;

public class GeneralUtility {

	public static void checkNull(Object o, String message) {
		if (o == null) {
			throw new RuntimeException(message);
		}
	}
	
	public static boolean isOriginalMod(){
		return false;
	}

}
