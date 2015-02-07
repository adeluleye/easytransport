package com.wizcodegroup.easytransport.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;
import com.wizcodegroup.easytransport.R;
import com.wizcodegroup.easytransport.adapter.PostsListAdapter;
import com.wizcodegroup.easytransport.app.AppConst;
import com.wizcodegroup.easytransport.app.AppController;
import com.wizcodegroup.easytransport.data.PostsItem;
import com.wizcodegroup.easytransport.helpers.M;

import android.os.Bundle;
import android.annotation.SuppressLint;
//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class HomeFragment extends Fragment implements OnClickListener {
	private ListView postsList;
	private List<PostsItem> mPostsItems;
	private PostsListAdapter mPostsListAdapter;
	int current_page = 1;
	int totalPages = 0;
	public View footerView, headerView;
	public View mView;
	//private AdView mAdView;
	private Context mContext;
	private Button loadMorebtn;
	private String url;
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.mContext = getActivity().getApplicationContext();
		mView = inflater.inflate(R.layout.fragment_post, container, false);
		initializeView();
		mPostsItems = new ArrayList<PostsItem>();
		mPostsListAdapter = new PostsListAdapter(getActivity(), mPostsItems);
		footerView = inflater.inflate(R.layout.posts_list_footer, null);
		headerView = inflater.inflate(R.layout.posts_list_header, null);

		loadMorebtn = (Button) footerView.findViewById(R.id.loadMorebtn);
		loadMorebtn.setOnClickListener(this);
		
		postsList.addHeaderView(headerView);
		postsList.setAdapter(mPostsListAdapter);
		url = M.parseURL(AppConst.POSTS_URL + current_page, mContext);
		if(M.isCached(url)){
			parseJson(M.cachedJSONObject(url));
		}else{
			newRequest(url);
		}		
		return mView;
	}

	private void initializeView() {
		postsList = (ListView) mView.findViewById(R.id.postsList);
		// ADMOB
		/*mAdView = (AdView) mView.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);
		*/
		// ADMOB
	}

	public void newRequest(String url) {
		M.showLoadingDialog(getActivity());
		JsonObjectRequest jsonReq = new JsonObjectRequest(Method.GET, url,
				null, new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						if (response != null) {
							M.hideLoadingDialog();
							parseJson(response);
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
		if (postsList.getFooterViewsCount() == 0) {
			postsList.addFooterView(footerView);
		}
		try {
			JSONArray feedArray = response.getJSONArray("posts");
			if (totalPages == 0) {
				totalPages = response.getInt("pages");
			}
			if (current_page == totalPages) {
				postsList.removeFooterView(footerView);
			}
			for (int i = 0; i < feedArray.length(); i++) {
				JSONObject postObj = (JSONObject) feedArray.get(i);
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
		if (v.getId() == R.id.loadMorebtn) {
			current_page += 1;
			if (current_page > totalPages) {
				M.T(getActivity(), "No more posts");
			} else {
				url = M.parseURL(AppConst.POSTS_URL + current_page,
						mContext);
				//if(M.isCached(url)){
					//parseJson(M.cachedJSONObject(url));
				//}else{
					newRequest(url);
				//}				
			}
		}
	}
}
