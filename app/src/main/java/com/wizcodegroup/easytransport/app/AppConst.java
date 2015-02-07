package com.wizcodegroup.easytransport.app;

public class AppConst {
	public static final String TAG = "atouch";
	public static final String MAIN = "http://wizcodetech.com/easytransport/";
	public static final String BASE_URL = MAIN + "api.php?token=:token:";
	public static final String POSTS_URL = BASE_URL + "&cmd=posts&page=";
	public static final String USERS_URL = BASE_URL + "&cmd=users&id=";
	public static final String PUBLISH_URL = BASE_URL + "&cmd=publish";
	public static final String LIKE_URL = BASE_URL + "&cmd=like&id=";
	public static final String UNLIKE_URL = BASE_URL + "&cmd=unlike&id=";
	public static final String FOLLOW_URL = BASE_URL + "&cmd=follow&id=";
	public static final String UNFOLLOW_URL = BASE_URL + "&cmd=unfollow&id=";
	public static final String FOLLOWING_URL = BASE_URL + "&cmd=getFollowing";
	public static final String FAVORITES_URL = BASE_URL + "&cmd=liked&page=";
	public static final String SIMPLEURINFO_URL = BASE_URL
			+ "&cmd=simpleUserInfo&id=";
	public static final String IMAGE_TEST = "http://10.0.2.2/Android/network/cp/test.php";
	public static final String LOGIN_URL = MAIN + "api.php?cmd=login";
	public static final int SELECT_PICTURE = 1;
	public static final String REGISTER_URL = MAIN + "api.php?cmd=register";
	public static final String POST_URL = BASE_URL + "&cmd=post&id=";
	public static final String COMMENT_URL = BASE_URL + "&cmd=comment&id=";
	public static final String COMMENTS_URL = BASE_URL + "&cmd=comments&id=";
	public static final String PROFILE_URL = BASE_URL + "&cmd=getProfile";
	public static final String UPDATE_URL = BASE_URL + "&cmd=updateProfile";

}