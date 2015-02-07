package com.wizcodegroup.easytransport.adapter;

import java.util.List;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.wizcodegroup.easytransport.R;
import com.wizcodegroup.easytransport.app.AppController;
import com.wizcodegroup.easytransport.data.CommentsItem;
import com.wizcodegroup.easytransport.helpers.ImageCircular;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("InflateParams")
public class CommentsListAdapter extends BaseAdapter {
	private Activity mActivity;
	private LayoutInflater mInflater;
	private List<CommentsItem> mCommentsItem;
	ViewHolder holder = null;
	private ImageLoader mImageLoader;
	private ImageCircular mImageCircular;
	public CommentsListAdapter(Activity mActivity, List<CommentsItem> mCommentsItem){
		this.mActivity = mActivity;
		this.mCommentsItem = mCommentsItem;
	}
	@Override
	public int getCount() {
		return mCommentsItem.size();
	}

	@Override
	public Object getItem(int location) {
		return mCommentsItem.get(location);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (mInflater == null)
			mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null){
			convertView = mInflater.inflate(R.layout.comment_item, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		CommentsItem item = (CommentsItem) getItem(position);
		holder.name.setText(item.getOwnerName());
		holder.date.setText(item.getDate());
		holder.text.setText(item.getText());
		mImageLoader = AppController.getInstance().getImageLoader();
		mImageLoader.get(item.getOwnerImage(), new ImageListener() {
			@Override
			public void onErrorResponse(VolleyError error) {}
			@Override
			public void onResponse(ImageContainer response, boolean arg1) {
				if (response.getBitmap() != null) {
					mImageCircular = new ImageCircular(response.getBitmap());
					holder.picture.setImageDrawable(mImageCircular);
				}
			}
		});
		return convertView;
	}
	private class ViewHolder{
		TextView name, date, text;
		ImageView picture;
		public ViewHolder(View v) {
			name = (TextView) v.findViewById(R.id.name);
			date = (TextView) v.findViewById(R.id.date);
			text = (TextView) v.findViewById(R.id.text);
			picture = (ImageView) v.findViewById(R.id.picture);
		}
		
	}

}
