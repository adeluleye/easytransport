package com.wizcodegroup.easytransport;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.wizcodegroup.easytransport.adapter.PostsListAdapter;
import com.wizcodegroup.easytransport.app.AppConst;
import com.wizcodegroup.easytransport.app.AppController;
import com.wizcodegroup.easytransport.data.PostsItem;
import com.wizcodegroup.easytransport.helpers.ImageCircular;
import com.wizcodegroup.easytransport.helpers.M;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ProfileActivity extends Activity implements OnClickListener, OnScrollListener {
	private ListView postsList;
	private List<PostsItem> mPostsItems;
	private PostsListAdapter mPostsListAdapter;
	int current_page = 1;
	int totalPages = 0;
	public View footerView, headerView;
	private TextView profileName, profileJob, profileAddress, followersNum,
			postsNum;
	private Button followBtn;
	private ImageView profilePicture;
	private int userID = 0;
	private int mLastFirstVisibleItem;
	private boolean mIsScrollingUp;
	private LinearLayout mProfileHeader;
	boolean followed;
	//private AdView mAdView;
	private ImageLoader mImageLoader = AppController.getInstance().getImageLoader();
	private Button loadMorebtn;
	private ImageCircular mImageCircular;
	private String url;
	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		if (getIntent().hasExtra("userID")) {
			userID = getIntent().getExtras().getInt("userID");
		}

		initializeView();
		mPostsItems = new ArrayList<PostsItem>();
		mPostsListAdapter = new PostsListAdapter(this, mPostsItems);
		footerView = ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.posts_list_footer, null);
		headerView = ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.posts_list_header, null);
		loadMorebtn = (Button) footerView.findViewById(R.id.loadMorebtn);
		loadMorebtn.setOnClickListener(this);
		postsList.addHeaderView(headerView);
		postsList.setAdapter(mPostsListAdapter);
		postsList.setOnScrollListener(this);
		url = M.parseURL(AppConst.USERS_URL + userID, this);
//		if(M.isCached(url)){
//			parseJson(M.cachedJSONObject(url), false);
//		}else{
			newRequest(url, false);
		//}
		
	}

	public void initializeView() {
		postsList = (ListView) findViewById(R.id.postsList);
		profilePicture = (ImageView) findViewById(R.id.profilePicture);
		profileName = (TextView) findViewById(R.id.profileName);
		profileJob = (TextView) findViewById(R.id.profileJob);
		profileAddress = (TextView) findViewById(R.id.profileAdress);
		followersNum = (TextView) findViewById(R.id.followersNum);
		postsNum = (TextView) findViewById(R.id.postsNum);
		mProfileHeader = (LinearLayout) findViewById(R.id.profileHeader);
		followBtn = (Button) findViewById(R.id.followBtn);
		followBtn.setOnClickListener(this);
		// ADMOB
		/*mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);*/
		// ADMOB
	}

	public void newRequest(String url, final boolean loadMore) {
		M.showLoadingDialog(this);
		JsonObjectRequest jsonReq = new JsonObjectRequest(Method.GET, url,
				null, new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						if (response != null) {
							parseJson(response, loadMore);
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

	public void parseJson(JSONObject response, boolean loadMore) {
		try {

			if (loadMore == false) {
				JSONObject profile = response.getJSONObject("profile");
				boolean mine = profile.getBoolean("mine");
				followed = profile.getBoolean("followed");
				if (mine == false) {
					followBtn.setVisibility(View.VISIBLE);
				} else {
					followBtn.setVisibility(View.GONE);
				}
				if (followed == true) {
					followBtn.setText("Unfollow");
				} else {
					followBtn.setText("Follow");
				}
				profileName.setText(profile.getString("name"));
				getActionBar().setTitle(profile.getString("name"));
				profileJob.setText(profile.getString("job"));
				profileAddress.setText(profile.getString("address"));
				followersNum.setText(profile.getInt("totalFollowers") + "");
				postsNum.setText(profile.getInt("totalPosts") + "");
				mImageLoader.get(profile.getString("picture"),
						new ImageListener() {
							@Override
							public void onErrorResponse(VolleyError arg0) {
							}

							@Override
							public void onResponse(ImageContainer response,
									boolean arg1) {
								if (response.getBitmap() != null) {
									mImageCircular = new ImageCircular(
											response.getBitmap());
									getActionBar().setIcon(mImageCircular);
									profilePicture.setImageDrawable(mImageCircular);
								}
							}
						});
			}

			JSONObject userPosts = response.getJSONObject("userPosts");
			JSONArray posts = userPosts.getJSONArray("posts");
			if (totalPages == 0) {
				totalPages = userPosts.getInt("pages");
			}
			if (postsList.getFooterViewsCount() == 0) {
				postsList.addFooterView(footerView);
			}
			if (current_page == totalPages) {
				postsList.removeFooterView(footerView);
			}
			for (int i = 0; i < posts.length(); i++) {
				JSONObject postObj = (JSONObject) posts.get(i);
				PostsItem item = new PostsItem();
				item.setId(postObj.getInt("id"));
				item.setOwnerID(postObj.getInt("ownerID"));
				item.setViews(postObj.getInt("views"));
				item.setLikes(postObj.getInt("likes"));
				item.setName(postObj.getString("ownerName"));
				String image = postObj.isNull("image") ? null : postObj
						.getString("image");
				item.setImge(image);
				item.setStatus(postObj.getString("status"));
				item.setProfilePic(postObj.getString("ownerPicture"));
				item.setTimeStamp(postObj.getString("date"));
				item.setLiked(postObj.getBoolean("liked"));
				mPostsItems.add(item);
			}
			mPostsListAdapter.notifyDataSetChanged();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == followBtn.getId()) {
			
			FollowHttpRequestTask followTask = new FollowHttpRequestTask();
			try {
				String[] params;
				if (followed == true) {
					followBtn.setText("Follow");
					followed = false;
					params = new String[] { AppConst.UNFOLLOW_URL + userID };
				} else {
					followBtn.setText("Unfollow");
					followed = true;
					params = new String[] { AppConst.FOLLOW_URL + userID };
				}
				followTask.execute(params);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (v.getId() == R.id.loadMorebtn) {
			current_page += 1;
			if (current_page > totalPages) {
				M.T(this, "No more posts");
			} else {
				url = M.parseURL(AppConst.USERS_URL + userID + "&page="
						+ current_page, this);
				if(M.isCached(url)){
					parseJson(M.cachedJSONObject(url), true);
				}else{
					newRequest(url, true);
				}				
			}
		}
	}

	private class FollowHttpRequestTask extends AsyncTask<String, Void, String> {
		

		@Override
		protected String doInBackground(String... params) {
			String result = null;
			try {
				result = M.simpleGETRequest(M.parseURL(params[0], ProfileActivity.this));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			M.removeCache(M.parseURL(AppConst.FOLLOWING_URL, ProfileActivity.this));
//			if (result.equals("followed")) {
//				followBtn.setText("Unfollow");
//				followed = true;
//			} else {
//				followBtn.setText("Follow");
//				followed = false;
//			}
		}
	}

	@Override
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {}

	@Override
	public void onScrollStateChanged(AbsListView v, int scrollState) {
		final int currentFirstVisibleItem = v.getFirstVisiblePosition();

		if (currentFirstVisibleItem > mLastFirstVisibleItem) {
			mIsScrollingUp = false;
		} else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
			mIsScrollingUp = true;
		}
		if (mIsScrollingUp == true && mLastFirstVisibleItem <= 1) {
			if (mProfileHeader.getVisibility() == View.GONE) {
				Animation anim = AnimationUtils.loadAnimation(
						ProfileActivity.this, R.anim.slide_down);
				mProfileHeader.startAnimation(anim);
				mProfileHeader.setVisibility(View.VISIBLE);
			}
		} else if (mIsScrollingUp == false
				&& mLastFirstVisibleItem >= 1) {
			if (mProfileHeader.getVisibility() == View.VISIBLE) {
				Animation anim = AnimationUtils.loadAnimation(
						ProfileActivity.this, R.anim.slide_up);
				mProfileHeader.startAnimation(anim);
				mProfileHeader.setVisibility(View.GONE);
			}
		}
		mLastFirstVisibleItem = currentFirstVisibleItem;
	}
}
