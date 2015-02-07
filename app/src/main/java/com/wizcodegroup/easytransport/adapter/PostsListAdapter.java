package com.wizcodegroup.easytransport.adapter;


import java.util.List;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.NetworkImageView;
import com.wizcodegroup.easytransport.PostActivity;
import com.wizcodegroup.easytransport.ProfileActivity;
import com.wizcodegroup.easytransport.R;
import com.wizcodegroup.easytransport.app.AppConst;
import com.wizcodegroup.easytransport.app.AppController;
import com.wizcodegroup.easytransport.data.PostsItem;
import com.wizcodegroup.easytransport.helpers.ImageCircular;
import com.wizcodegroup.easytransport.helpers.M;

public class PostsListAdapter extends BaseAdapter implements OnClickListener{
	private Activity mActivity;
	private LayoutInflater mInflater;
	private List<PostsItem> mPostsItems;
	private Intent mIntent;
	ViewHolder holder = null;
	private ImageLoader mImageLoader;
	
	public PostsListAdapter(Activity mActivity, List<PostsItem> mPostsItems) {
		this.mActivity = mActivity;
		this.mPostsItems = mPostsItems;
	}
	@Override
	public int getCount() {
		return mPostsItems.size();
	}

	@Override
	public Object getItem(int location) {
		return mPostsItems.get(location);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (mInflater == null)
			mInflater = (LayoutInflater) mActivity
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (convertView == null){
			convertView = mInflater.inflate(R.layout.posts_item, null);
			holder = new ViewHolder(convertView);
			//holder.profilePic.setOnClickListener(this);
			//holder.name.setOnClickListener(this);
			//holder.shareBtn.setOnClickListener(this);
			//holder.likeBtn.setOnClickListener(this);
			//holder.postBody.setOnClickListener(this);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}

		PostsItem item = (PostsItem) getItem(position);
		holder.shareBtn.setTag(R.id.TAG_OBJECT,item);
		holder.likeBtn.setTag(R.id.TAG_OBJECT,item);
		holder.shareBtn.setTag(R.id.TAG_VIEW,holder.feedImageView);
		holder.likeBtn.setTag(R.id.TAG_OBJECT,item);
		holder.postBody.setTag(R.id.TAG_OBJECT,item);
		holder.name.setText(item.getName());
		holder.name.setTag(item.getOwnerID());
		holder.profilePic.setTag(item.getOwnerID());
		holder.likesNum.setText(item.getLikes()+"");
		holder.viewsNum.setText(item.getViews()+"");
		holder.timestamp.setText(item.getTimeStamp());
		if (!TextUtils.isEmpty(item.getStatus())) {
			holder.statusMsg.setText(item.getStatus());
			holder.statusMsg.setVisibility(View.VISIBLE);
		} else {
			holder.statusMsg.setVisibility(View.GONE);
		}
		if(item.isLiked() == true){
			holder.likeBtn.setBackground(mActivity.getResources().getDrawable(R.drawable.bg_unlike_button));

		}else{
			holder.likeBtn.setBackground(mActivity.getResources().getDrawable(R.drawable.bg_like_button));

		}
		mImageLoader = AppController.getInstance().getImageLoader();
		mImageLoader.get(item.getProfilePic(), new ImageListener() {
			@Override
			public void onErrorResponse(VolleyError error) {}
			@Override
			public void onResponse(ImageContainer response, boolean arg1) {
				if (response.getBitmap() != null) {
					ImageCircular bm = new ImageCircular(response.getBitmap());
					holder.profilePic.setImageDrawable(bm);
				}
			}
		});

		if (item.getImge() != null) {
			holder.feedImageView.setImageUrl(item.getImge(), mImageLoader);
			holder.shareBtn.setTag(R.id.TAG_VIEW, holder.feedImageView);
		}else{
			holder.feedImageView.setVisibility(View.GONE);
		}
		return convertView;
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == holder.profilePic.getId() || v.getId() == holder.name.getId()){
			mIntent = new Intent(mActivity, ProfileActivity.class);
			int id = (Integer) v.getTag();
			mIntent.putExtra("userID", id);
			mActivity.startActivity(mIntent);
		}else if(v.getId() == holder.shareBtn.getId()){
			PostsItem item = (PostsItem) v.getTag(R.id.TAG_OBJECT);
			NetworkImageView postImage = (NetworkImageView) v.getTag(R.id.TAG_VIEW);
			if(!TextUtils.isEmpty(item.getStatus()) && item.getImge() == null){
				mIntent = new Intent(Intent.ACTION_SEND);
				mIntent.setType("text/plain");
				mIntent.putExtra(Intent.EXTRA_TEXT, item.getStatus());
				mActivity.startActivity(Intent.createChooser(mIntent, "Share via"));
			}else if(!TextUtils.isEmpty(item.getStatus()) &&
					item.getImge() != null){
				//Bitmap m= Bitmap.
				//M.L(postImage.getDrawable());
			}
		}else if(v.getId() == holder.likeBtn.getId()){
			PostsItem item = (PostsItem) v.getTag(R.id.TAG_OBJECT);
			LikeHttpRequestTask likehttp = new LikeHttpRequestTask();
			String[] params;
			if(item.isLiked() == true){
				params = new String[]{AppConst.UNLIKE_URL+item.getId()};
				v.setBackground(mActivity.getResources().getDrawable(R.drawable.bg_like_button));
				item.setLiked(false);
			}else{
				params = new String[]{AppConst.LIKE_URL+item.getId()};
				v.setBackground(mActivity.getResources().getDrawable(R.drawable.bg_unlike_button));
				item.setLiked(true);
			}
			likehttp.execute(params);
		}else if(v.getId() == R.id.postBody){
			mIntent = new Intent(mActivity, PostActivity.class);
			PostsItem item = (PostsItem) v.getTag(R.id.TAG_OBJECT);
			mIntent.putExtra("postID", item.getId());
			mActivity.startActivity(mIntent);
		}
	} 

	private class ViewHolder{
		TextView name,timestamp,statusMsg,likesNum,viewsNum;
		ImageView profilePic;
		NetworkImageView feedImageView;
		ImageButton shareBtn,likeBtn;
		LinearLayout postBody;
		ViewHolder(View v){
			name = (TextView) v.findViewById(R.id.postOwnerName);
			timestamp = (TextView) v
					.findViewById(R.id.postPublishDate);
			statusMsg = (TextView) v
					.findViewById(R.id.postStatus);
			likesNum = (TextView) v.findViewById(R.id.likesNum);
			viewsNum = (TextView) v.findViewById(R.id.viewsNum);
			profilePic = (ImageView) v.findViewById(R.id.postOwnerPicture);
			feedImageView = (NetworkImageView) v
					.findViewById(R.id.postImage);
			shareBtn = (ImageButton) v.findViewById(R.id.shareBtn);
			likeBtn = (ImageButton) v.findViewById(R.id.likeBtn);
			postBody = (LinearLayout) v.findViewById(R.id.postBody);
		}
	}
	private class LikeHttpRequestTask extends AsyncTask<String, Void, String> {
		String data;
		@Override
		protected String doInBackground(String... params) {
			String url = params[0];
			try {
				data = M.simpleGETRequest(M.parseURL(url, mActivity));
			} catch (Exception e) {
				e.printStackTrace();
			}

			return data;
		}
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			M.removeCache(M.parseURL(AppConst.POSTS_URL+1, mActivity));
			M.removeCache(M.parseURL(AppConst.FAVORITES_URL+1, mActivity));
		}
	}
}
