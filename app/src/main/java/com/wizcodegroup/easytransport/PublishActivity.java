package com.wizcodegroup.easytransport;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.wizcodegroup.easytransport.app.AppConst;
import com.wizcodegroup.easytransport.app.AppController;
import com.wizcodegroup.easytransport.helpers.ImageCircular;
import com.wizcodegroup.easytransport.helpers.M;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PublishActivity extends Activity implements OnClickListener {

	private ImageView mImagePreview;
	private String selectedImagePath = null;
	private String status = null;
	private ImageButton addPhoto, sendStatus, changePrivacy;
	private EditText input;
	private TextView profileName, postPrivacy;
	private ImageView profilePicture;
	private String privacy = "public";
	private Bitmap mBitmap;
	private Uri selectedImageUri = null;
	private ImageLoader mImageLoader = AppController.getInstance()
			.getImageLoader();
	private Intent mIntent;
	private String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_publish);
		initializeView();
		url = M.parseURL(AppConst.SIMPLEURINFO_URL + 0, this);
		if (M.isCached(url)) {
			parseJson(M.cachedJSONObject(url));
		} else {
			newRequest(url);
		}
	}

	public void initializeView() {
		addPhoto = (ImageButton) findViewById(R.id.addPhoto);
		sendStatus = (ImageButton) findViewById(R.id.sendStatus);
		changePrivacy = (ImageButton) findViewById(R.id.changePrivacy);
		mImagePreview = (ImageView) findViewById(R.id.imagePreview);
		input = (EditText) findViewById(R.id.statusEdittext);
		sendStatus.setOnClickListener(this);
		addPhoto.setOnClickListener(this);
		changePrivacy.setOnClickListener(this);
		profilePicture = (ImageView) findViewById(R.id.postOwnerImage);
		profileName = (TextView) findViewById(R.id.postOwnerName);
		postPrivacy = (TextView) findViewById(R.id.postPrivacy);
	}

	private void newRequest(String url) {
		JsonObjectRequest jsonReq = new JsonObjectRequest(Method.GET, url,
				null, new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						if (response != null) {
							parseJson(response);
						}
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
					}
				});
		AppController.getInstance().addToRequestQueue(jsonReq);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == AppConst.SELECT_PICTURE) {
				selectedImageUri = data.getData();
				if (Build.VERSION.SDK_INT < 19) {
					selectedImagePath = getPath(selectedImageUri);
					mBitmap = BitmapFactory.decodeFile(selectedImagePath);
					mImagePreview.setImageBitmap(mBitmap);
				} else {
					selectedImagePath = getImagePathForKitKat(selectedImageUri);
					ParcelFileDescriptor parcelFileDescriptor;
					try {
						parcelFileDescriptor = getContentResolver()
								.openFileDescriptor(selectedImageUri, "r");
						FileDescriptor fileDescriptor = parcelFileDescriptor
								.getFileDescriptor();
						mBitmap = BitmapFactory
								.decodeFileDescriptor(fileDescriptor);
						parcelFileDescriptor.close();
						mImagePreview.setImageBitmap(mBitmap);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (mImagePreview.getVisibility() != View.VISIBLE) {
					mImagePreview.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	public String getImagePathForKitKat(Uri uri) {
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		cursor.moveToFirst();
		String document_id = cursor.getString(0);
		document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
		cursor.close();

		cursor = getContentResolver().query(
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				null, MediaStore.Images.Media._ID + " = ? ",
				new String[] { document_id }, null);
		cursor.moveToFirst();
		String path = cursor.getString(cursor
				.getColumnIndex(MediaStore.Images.Media.DATA));
		cursor.close();

		return path;
	}

	public String getPath(Uri uri) {
		if (uri == null) {
			return null;
		}
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = getContentResolver().query(uri, projection, null, null,
				null);
		if (cursor != null) {
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		}
		return uri.getPath();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == addPhoto.getId()) {
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(
					Intent.createChooser(intent, "Select Picture"),
					AppConst.SELECT_PICTURE);
		} else if (v.getId() == sendStatus.getId()) {

			String statusText = input.getText().toString().trim();
			if (statusText.isEmpty()) {
				status = null;
			} else {
				status = statusText;
			}
			M.L("S:"+status+" m:"+selectedImagePath );
			if (status == null && selectedImagePath == null) {
				M.T(PublishActivity.this,
						"You must choose an image or at least insert some text");
			} else {
				SendHttpRequestTask t = new SendHttpRequestTask();
				String[] params = new String[] { selectedImagePath, status,
						privacy };
				t.execute(params);
			}
		} else if (v.getId() == changePrivacy.getId()) {
			if (privacy.equals("public")) {
				postPrivacy.setText(R.string.privatePrivacy);
				privacy = "private";
				M.T(this, "Privacy changed to Followers");
			} else {
				postPrivacy.setText(R.string.publicPrivacy);
				privacy = "public";
				M.T(this, "Privacy changed to Public");
			}
		}
	}

	private class SendHttpRequestTask extends AsyncTask<String, Void, String> {
		String data;

		@Override
		protected String doInBackground(String... params) {
			String filePath = params[0];
			String status = params[1];
			String privacy = params[2];
			try {
				data = M.publishStatus(
						M.parseURL(AppConst.PUBLISH_URL, PublishActivity.this),
						filePath, status, privacy);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result.equals("done")) {
				url = M.parseURL(AppConst.POSTS_URL + 1, PublishActivity.this);
				M.removeCache(url);
				M.T(PublishActivity.this, "Post added successfully");
				mIntent = new Intent(PublishActivity.this, MainActivity.class);
				startActivity(mIntent);
			}
		}
	}

	public void parseJson(JSONObject response) {
		try {
			profileName.setText(response.getString("name"));
			mImageLoader.get(response.getString("picture"),
					new ImageListener() {
						@Override
						public void onErrorResponse(VolleyError arg0) {
						}

						@Override
						public void onResponse(ImageContainer response,
								boolean arg1) {
							if (response.getBitmap() != null) {
								ImageCircular bm = new ImageCircular(response
										.getBitmap());
								profilePicture.setImageDrawable(bm);
							}
						}
					});

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
