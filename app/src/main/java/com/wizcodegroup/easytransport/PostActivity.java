package com.wizcodegroup.easytransport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.wizcodegroup.easytransport.adapter.CommentsListAdapter;
import com.wizcodegroup.easytransport.app.AppConst;
import com.wizcodegroup.easytransport.app.AppController;
import com.wizcodegroup.easytransport.data.CommentsItem;
import com.wizcodegroup.easytransport.helpers.ImageCircular;
import com.wizcodegroup.easytransport.helpers.M;

public class PostActivity extends Activity implements OnClickListener {
	private int postID;
	private TextView name, timestamp, statusMsg, likesNum, viewsNum;
	private ImageView profilePic;
	private NetworkImageView feedImageView;
	private Boolean liked;
	private Intent mIntent;
	private JSONObject post;
	private ImageLoader mImageLoader = AppController.getInstance()
			.getImageLoader();
	private ImageButton shareBtn, likeBtn, submitComment;
	private EditText commentInput;
	private List<CommentsItem> mCommentsItem;
	private CommentsListAdapter mCommentsListAdapter;
	private ListView commentsList;
	private JSONArray comments;
	private String PostURL, CommentsURL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post);
		initializeView();
		mCommentsItem = new ArrayList<CommentsItem>();
		mCommentsListAdapter = new CommentsListAdapter(this, mCommentsItem);
		commentsList.setAdapter(mCommentsListAdapter);
		postID = getIntent().getExtras().getInt("postID");
		PostURL = M.parseURL(AppConst.POST_URL + postID, this);
		CommentsURL = M.parseURL(AppConst.COMMENTS_URL+postID, this);
		M.L(CommentsURL);
		M.L(PostURL);
		// POST Request
		if (M.isCached(PostURL)) {
			parseJson(M.cachedJSONObject(PostURL));
		} else {
			newRequest(PostURL);
		}
		// POST Request
		// POST Request
		if (M.isCached(CommentsURL)) {
			parseCommentsJson(M.cachedJSONObject(CommentsURL));
		} else {
			commentsRequest(CommentsURL);
		}
		// POST Request

	}

	public void initializeView() {
		name = (TextView) findViewById(R.id.postOwnerName);
		timestamp = (TextView) findViewById(R.id.postPublishDate);
		statusMsg = (TextView) findViewById(R.id.postStatus);
		likesNum = (TextView) findViewById(R.id.likesNum);
		viewsNum = (TextView) findViewById(R.id.viewsNum);
		profilePic = (ImageView) findViewById(R.id.postOwnerPicture);
		feedImageView = (NetworkImageView) findViewById(R.id.postImage);
		commentsList = (ListView) findViewById(R.id.commentsList);
		shareBtn = (ImageButton) findViewById(R.id.shareBtn);
		likeBtn = (ImageButton) findViewById(R.id.likeBtn);
		submitComment = (ImageButton) findViewById(R.id.submitComment);
		commentInput = (EditText) findViewById(R.id.commentInput);
		profilePic.setOnClickListener(this);
		name.setOnClickListener(this);
		likeBtn.setOnClickListener(this);
		shareBtn.setOnClickListener(this);
		submitComment.setOnClickListener(this);
	}

	public void commentsRequest(String url) {
		JsonObjectRequest jsonReq = new JsonObjectRequest(Method.GET, url,
				null, new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						if (response != null) {
							parseCommentsJson(response);
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
					}
				});
		AppController.getInstance().addToRequestQueue(jsonReq);
	}

	public void parseCommentsJson(JSONObject response) {
		try {
			if (!response.isNull("comments")) {
				comments = response.getJSONArray("comments");
				for (int i = 0; i < comments.length(); i++) {
					JSONObject comment = comments.getJSONObject(i);
					CommentsItem item = new CommentsItem();
					item.setOwnerName(comment.getString("ownerName"));
					item.setOwnerImage(comment.getString("ownerPicture"));
					item.setDate(comment.getString("date"));
					item.setText(comment.getString("text"));
					mCommentsItem.add(item);
				}
				mCommentsListAdapter.notifyDataSetChanged();
				setListViewHeightBasedOnChildren(commentsList);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void newRequest(String url) {
		M.showLoadingDialog(this);
		JsonObjectRequest jsonReq = new JsonObjectRequest(Method.GET, url,
				null, new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						if (response != null) {
							parseJson(response);
							M.hideLoadingDialog();
						}
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						M.hideLoadingDialog();
					}
				});
		AppController.getInstance().addToRequestQueue(jsonReq);
	}

	public void parseJson(JSONObject response) {
		try {
			post = response.getJSONObject("post");
			name.setText(post.getString("ownerName"));
			timestamp.setText(post.getString("date"));
			likesNum.setText(post.getInt("likes") + "");
			viewsNum.setText(post.getInt("views") + "");
			if (!TextUtils.isEmpty(post.getString("status"))) {
				statusMsg.setText(post.getString("status"));
				statusMsg.setVisibility(View.VISIBLE);
			} else {
				statusMsg.setVisibility(View.GONE);
			}
			if (!post.isNull("image")) {
				feedImageView
						.setImageUrl(post.getString("image"), mImageLoader);
			} else {
				feedImageView.setVisibility(View.GONE);
			}
			
			mImageLoader.get(post.getString("ownerPicture"),
					new ImageListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
						}

						@Override
						public void onResponse(ImageContainer response,
								boolean arg1) {
							if (response.getBitmap() != null) {
								ImageCircular bm = new ImageCircular(response
										.getBitmap());
								profilePic.setImageDrawable(bm);
							}
						}
					});
			liked = post.getBoolean("liked");
			if (liked == true) {
				//likeBtn.setBackground(getResources().getDrawable(
				//		R.drawable.bg_unlike_button));

                likeBtn.setBackground(getResources().getDrawable(
                        R.drawable.ic_likes));

			} else {
				//likeBtn.setBackground(getResources().getDrawable(
				//		R.drawable.bg_like_button));

                likeBtn.setBackground(getResources().getDrawable(
                        R.drawable.ic_likes));

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == profilePic.getId() || v.getId() == name.getId()) {
			try {
				mIntent = new Intent(this, ProfileActivity.class);
				mIntent.putExtra("userID", post.getInt("ownerID"));
				startActivity(mIntent);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (v.getId() == shareBtn.getId()) {
			try {
				if (!TextUtils.isEmpty(post.getString("status"))) {
					mIntent = new Intent(Intent.ACTION_SEND);
					mIntent.setType("text/plain");
					mIntent.putExtra(Intent.EXTRA_TEXT,
							post.getString("status"));
					startActivity(Intent.createChooser(mIntent, "Share via"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (v.getId() == likeBtn.getId()) {
			try {
				LikeHttpRequestTask likehttp = new LikeHttpRequestTask();
				String[] params;
				if (liked == true) {
					params = new String[] { AppConst.UNLIKE_URL
							+ post.getInt("id") };
					//v.setBackground(getResources().getDrawable(
					//		R.drawable.bg_like_button));
                    //v.setBackground(getResources().getDrawable(
                     //       R.drawable.ic_likes));
					liked = false;
				} else {
					params = new String[] { AppConst.LIKE_URL
							+ post.getInt("id") };
					//v.setBackground(getResources().getDrawable(
					//		R.drawable.bg_unlike_button));
                    //v.setBackground(getResources().getDrawable(
                    //        R.drawable.ic_likes));
					liked = true;
				}
				likehttp.execute(params);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (v.getId() == R.id.submitComment) {
			final String text = commentInput.getText().toString().trim();
			String url = AppConst.COMMENT_URL + postID;
			if (text.length() < 2) {
				commentInput.setError("Comment too short");
			} else {
				StringRequest req = new StringRequest(Method.POST, url.replace(
						":token:", M.getToken(this)),
						new Response.Listener<String>() {

							@Override
							public void onResponse(String response) {
								if (response.equals("done")) {
									commentInput.setText("");
									M.T(PostActivity.this,
											"Comment Added Successfully");
									M.removeCache(CommentsURL);
									mCommentsItem.clear();
									commentsRequest(CommentsURL);
									/*CommentsItem item = new CommentsItem();
									item.setOwnerName(comment.getString("ownerName"));
									item.setOwnerImage(comment.getString("ownerPicture"));
									item.setDate(comment.getString("date"));
									item.setText(comment.getString("text"));
									mCommentsItem.add(item);*/
								} else {
									M.T(PostActivity.this, response);
								}
							}
						}, new Response.ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {

							}
						}) {

					@Override
					protected Map<String, String> getParams() {
						Map<String, String> params = new HashMap<String, String>();
						params.put("comment", text);
						return params;
					}
				};
				AppController.getInstance().addToRequestQueue(req);
			}

		}
	}

	private class LikeHttpRequestTask extends AsyncTask<String, Void, String> {
		String data;

		@Override
		protected String doInBackground(String... params) {
			String url = params[0];
			try {
				data = M.simpleGETRequest(url.replace(":token:",
						M.getToken(PostActivity.this)));
			} catch (Exception e) {
				e.printStackTrace();
			}

			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			M.removeCache(PostURL);
			M.removeCache(M.parseURL(AppConst.POSTS_URL+1, PostActivity.this));
			M.removeCache(M.parseURL(AppConst.FAVORITES_URL, PostActivity.this));
		}
	}

	public static void setListViewHeightBasedOnChildren(ListView mListView) {
		ListAdapter listAdapter = mListView.getAdapter();
		if (listAdapter == null)
			return;

		int desiredWidth = MeasureSpec.makeMeasureSpec(mListView.getWidth(),
				MeasureSpec.UNSPECIFIED);
		int totalHeight = 0;
		View view = null;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			view = listAdapter.getView(i, view, mListView);
			if (i == 0)
				view.setLayoutParams(new LayoutParams(desiredWidth,
						LayoutParams.WRAP_CONTENT));

			view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
			totalHeight += view.getMeasuredHeight();
		}
		LayoutParams params = mListView.getLayoutParams();
		params.height = totalHeight
				+ (mListView.getDividerHeight() * (listAdapter.getCount() - 1));
		mListView.setLayoutParams(params);
		mListView.requestLayout();
	}
}
