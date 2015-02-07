package com.wizcodegroup.easytransport.adapter;

import java.util.ArrayList;


import com.wizcodegroup.easytransport.R;
import com.wizcodegroup.easytransport.dataMng.NavigationItem;
import com.wizcodegroup.easytransport.constanst.IWhereMyLocationConstants;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class NavigationListAdapter extends BaseAdapter implements IWhereMyLocationConstants{

	private Activity mActivity;
	private ArrayList<NavigationItem> navDrawerItems;
    private Typeface mTypefaceBold;
    private Typeface mTypefaceLight;

    ViewHolder holder = null;
	private LayoutInflater mInflater;
	
	public NavigationListAdapter(Activity mActivity, ArrayList<NavigationItem> navDrawerItems, Typeface mTypefaceBold, Typeface mTypefaceLight){
		this.mActivity = mActivity;
		this.navDrawerItems = navDrawerItems;
        this.mTypefaceBold=mTypefaceBold;
        this.mTypefaceLight=mTypefaceLight;
	}

	@Override
	public int getCount() {
		return navDrawerItems.size();
	}

	@Override
	public Object getItem(int position) {		
		return navDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
    /*@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder mHolder;
        LayoutInflater mInflater;
        if (convertView == null) {
            //convertView = mInflater.inflate(R.layout.drawer_list_item, null);
            mHolder = new ViewHolder(convertView);
            mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_list_item, null);
            convertView.setTag(mHolder);
        }
        else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        mHolder.title = (TextView) convertView.findViewById(R.id.drawerListItem);
        mHolder.mDevider = convertView.findViewById(R.id.devider);

        NavigationItem mDrawerObject = navDrawerItems.get(position);
        mHolder.title.setText(mDrawerObject.getTitle());
        if(mDrawerObject.isSelected()){
            mHolder.title.setTypeface(mTypefaceBold);
        }
        else{
            mHolder.title.setTypeface(mTypefaceLight);
        }
        mHolder.mDevider.setVisibility(position==navDrawerItems.size()-1 ? View.GONE:View.VISIBLE);
        //return convertView;
        mHolder.icon.setImageResource(navDrawerItems.get(position).getIcon());
        mHolder.title.setText(navDrawerItems.get(position).getTitle());
        return convertView;
    }*/

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        //final ViewHolder mHolder;
        //LayoutInflater mInflater;
		if (mInflater == null)
			mInflater = (LayoutInflater) mActivity
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
            convertView = mInflater.inflate(R.layout.drawer_list_item, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
        }else{
        	holder = (ViewHolder) convertView.getTag();
        }

        holder.title = (TextView) convertView.findViewById(R.id.drawerListItem);
        holder.mDevider = convertView.findViewById(R.id.devider);

        NavigationItem mDrawerObject = navDrawerItems.get(position);
        holder.title.setText(mDrawerObject.getTitle());
        if(mDrawerObject.isSelected()){
            holder.title.setTypeface(mTypefaceBold);
        }
        else{
            holder.title.setTypeface(mTypefaceLight);
        }
        holder.mDevider.setVisibility(position==navDrawerItems.size()-1 ? View.GONE:View.VISIBLE);
         
        holder.icon.setImageResource(navDrawerItems.get(position).getIcon());
        holder.title.setText(navDrawerItems.get(position).getTitle());
        return convertView;
	}

    public void setSelectedDrawer(int pos){
        if(pos<0 || pos>=navDrawerItems.size()){
            return;
        }
        if(pos==0){
            navDrawerItems.get(pos).setSelected(true);
        }
        for(NavigationItem mDrawerObject:navDrawerItems){
            mDrawerObject.setSelected(false);
        }
        navDrawerItems.get(pos).setSelected(true);
        notifyDataSetChanged();
    }

    public void defaultBold(){

        //pos=0;
            navDrawerItems.get(0).setSelected(true);

    }

    public ArrayList<NavigationItem> getListDrawerObjects() {
        return navDrawerItems;
    }

	private class ViewHolder{
		TextView title;
		ImageView icon;
        public View mDevider;
		
		ViewHolder(View v){
			this.icon = (ImageView) v.findViewById(R.id.drawerListItemIcon);
			this.title = (TextView) v.findViewById(R.id.drawerListItem);
		}
	}

    /*private static class ViewHolder {
        public TextView title;
        ImageView icon;
        public View mDevider;
    }*/
}
