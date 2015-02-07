package com.wizcodegroup.easytransport.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Cache.Entry;
import com.wizcodegroup.easytransport.app.AppConst;
import com.wizcodegroup.easytransport.app.AppController;

public class M {
	private static Cache mCache = AppController.getInstance().getRequestQueue()
			.getCache();
	private static Entry mEntry;
	static ProgressDialog pDialog;
	private static File mFile = null;
	private static FileBody mFileBody = null;
	private static MultipartEntityBuilder builder = null;
	private static SharedPreferences mSharedPreferences;
	private static HttpClient client = new DefaultHttpClient();

	public static void showLoadingDialog(Context mContext) {
		pDialog = new ProgressDialog(mContext);
		pDialog.setMessage("Please wait...");
		pDialog.setIndeterminate(true);
		pDialog.setCancelable(false);
		pDialog.show();
	}

	public static void hideLoadingDialog() {
		pDialog.dismiss();
	}

	public static void T(Context mContext, String Message) {
		Toast.makeText(mContext, Message, Toast.LENGTH_SHORT).show();
	}

	public static void L(String Message) {
		Log.e(AppConst.TAG, Message);
	}

	public static String simpleGETRequest(String url) throws Exception {
		HttpGet get = new HttpGet(url);
		HttpResponse response = client.execute(get);
		return getContent(response);
	}

	public static String publishStatus(String url, String filePath,
			String status, String privacy) throws Exception {
		builder = MultipartEntityBuilder
				.create();
		HttpPost post = new HttpPost(url);
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		if (filePath != null) {
			mFile = new File(filePath);
			mFileBody = new FileBody(mFile);
			builder.addPart("image", mFileBody);
		}
		if (status != null) {
			builder.addTextBody("status", status);
		}
		builder.addTextBody("privacy", privacy);
		return excuteRequest(builder, post);
	}
	public static String excuteRequest(MultipartEntityBuilder builder,
			HttpPost http) throws Exception {
		ProgressiveEntity mEntity = new ProgressiveEntity(builder);
		http.setEntity(mEntity);
		HttpResponse response = client.execute(http);
		return getContent(response);
	}

	public static String getContent(HttpResponse response) throws IOException {
		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));
		String body = "";
		String content = "";
		while ((body = rd.readLine()) != null) {
			content += body + "\n";
		}
		return content.trim();
	}

	public static boolean setToken(String token, Context mContext) {
		mSharedPreferences = mContext.getSharedPreferences("settings", 0);
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putString("token", token);
		return editor.commit();
	}

	public static String getToken(Context mContext) {
		mSharedPreferences = mContext.getSharedPreferences("settings", 0);
		return mSharedPreferences.getString("token", "null");
	}

	public static String parseURL(String url, Context mContext) {
		return url.replace(":token:", getToken(mContext));
	}

	public static String register(String registerUrl, String filePath,
			String username, String password, String email) throws Exception {
		builder = MultipartEntityBuilder
				.create();
		HttpPost post = new HttpPost(registerUrl);
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		if (filePath != null) {
			mFile = new File(filePath);
			mFileBody = new FileBody(mFile);
			builder.addPart("image", mFileBody);
		}
		builder.addTextBody("username", username);
		builder.addTextBody("email", email);
		builder.addTextBody("password", password);
		return excuteRequest(builder, post);
	}

	public static String update(String url, String image, String username,
			String password, String email, String name, String job,
			String address) throws Exception {
		builder = MultipartEntityBuilder
				.create();
		HttpPost post = new HttpPost(url);
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		if (image != null) {
			mFile = new File(image);
			mFileBody = new FileBody(mFile);
			builder.addPart("image", mFileBody);
		}
		builder.addTextBody("username", username);
		builder.addTextBody("email", email);
		builder.addTextBody("password", password);
		builder.addTextBody("name", name);
		builder.addTextBody("job", job);
		builder.addTextBody("address", address);
		return excuteRequest(builder, post);
	}

	public static boolean isCached(String url) {
		mEntry = mCache.get(url);
		if (mEntry != null) {
			return true;
		} else {
			return false;
		}
	}

	public static void removeCache(String url) {
		AppController.getInstance().getRequestQueue().getCache()
				.remove(url);
	}

	public static String getCache(String url) {
		mEntry = mCache.get(url);
		String data = null;
		if (mEntry != null) {
			try {
				data = new String(mEntry.data, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else {
			return data;
		}
		return data;
	}
	public static JSONArray stringToJSONArray(String data){
		JSONArray json = null;
		try {
			json = new JSONArray(data);
		} catch (JSONException e) {

			e.printStackTrace();
		}
		return json;
	}
	public static JSONObject stringToJSONObject(String data){
		JSONObject json = null;
		try {
			json = new JSONObject(data);
		} catch (JSONException e) {

			e.printStackTrace();
		}
		return json;
	}
	public static JSONArray cachedJSONArray(String url){
		return stringToJSONArray(getCache(url));
	}
	public static JSONObject cachedJSONObject(String url){
		return stringToJSONObject(getCache(url));
		
	}
}
