package com.wizcodegroup.easytransport.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.wizcodegroup.easytransport.MainActivity;
import com.wizcodegroup.easytransport.MainSearchActivity;
import com.wizcodegroup.easytransport.R;
import com.wizcodegroup.easytransport.adapter.HomeAdapter;
import com.wizcodegroup.easytransport.constanst.IWhereMyLocationConstants;
import com.wizcodegroup.easytransport.dataMng.TotalDataManager;
import com.wizcodegroup.easytransport.object.HomeSearchObject;
import com.ypyproductions.utils.DirectionUtils;

import java.util.ArrayList;

public class FragmentHome extends Fragment implements IWhereMyLocationConstants {
	
	public static final String TAG = FragmentHome.class.getSimpleName();
	
	private View mRootView;

	private MainActivity mContext;

	private boolean isFindView;
	private GridView mGridView;

	private ArrayList<HomeSearchObject> mListHomeObjects;
	private HomeAdapter mHomeAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_home, container, false);
		return mRootView;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		if(!isFindView){
			isFindView=true;
			this.findView();
		}
	}
	
	private void findView(){
		this.mContext = (MainActivity) getActivity();
		this.mGridView =(GridView) mRootView.findViewById(R.id.gridview);
		this.mListHomeObjects = TotalDataManager.getInstance().getListHomeSearchObjects();
		if(mListHomeObjects!=null){
			int size = mListHomeObjects.size();
			ArrayList<HomeSearchObject> listHomeSearchObjects = new ArrayList<HomeSearchObject>();
			for(int i=0;i<size;i++){
				if(i!=0){
					listHomeSearchObjects.add(mListHomeObjects.get(i));
				}
			}
			this.mHomeAdapter = new HomeAdapter(mContext, listHomeSearchObjects, mContext.mTypeFaceRobotoLight, mContext.mImgFetcher);
			this.mGridView.setAdapter(mHomeAdapter);
			mGridView.setOnItemClickListener(new OnItemClickListener() {
				
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
					mHomeAdapter.setSelected(position);
					TotalDataManager.getInstance().setCurrentLocation(null);
					Intent mIntent = new Intent(mContext, MainSearchActivity.class);
					DirectionUtils.changeActivity(mContext, R.anim.slide_in_from_right, R.anim.slide_out_to_left, true, mIntent);
				}
			});
		}
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mHomeAdapter!=null){
			mHomeAdapter.onDestroy();
		}
	}
}
