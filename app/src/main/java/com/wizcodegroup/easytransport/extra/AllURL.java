 package com.wizcodegroup.easytransport.extra;

public class AllURL {

	/***
	 * 
	 * Login URL
	 */
	public static String loginURL(String email, String password) {
		return BaseURL.HTTP + "login.php?EmailAddress=" + email + "&Password="
				+ password;
	}

	
	/***
	 * 
	 * View NearBy List
	 */

	public static String nearByURL(String UPLat,String UPLng,String query,String apiKey) {
		return BaseURL.HTTP + "nearbysearch/json?location="+UPLat+","+UPLng+"&rankby=distance&types="+query+"&sensor=false&key="+apiKey;
	}

	
	/***
	 * 
	 *Search by Radius
	 */

//	public static String nearByURL(String UPLat,String UPLng,String query,String apiKey,String radius) {
//		return BaseURL.HTTP + "nearbysearch/json?location="+UPLat+","+UPLng+"&radius="+radius+"&types="+query+"&sensor=false&key="+apiKey;
//	}

	
	
	
	
	/***
	 * 
	 * View CityGuide List
	 */

	public static String cityGuideURL(String query,String apiKey) {
		return BaseURL.HTTP + "textsearch/json?query="+query+"+in+"+AllConstants.cityName+"&sensor=true&key="+apiKey;
	}

	/***
	 * 
	 * View CityGuide Details
	 */

	public static String cityGuideDetailsURL(String reference, String apiKey) {
		return BaseURL.HTTP + "details/json?reference="+reference+"&sensor=true&key="+apiKey;
	}


}
