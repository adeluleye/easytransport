package com.wizcodegroup.easytransport.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.wizcodegroup.easytransport.MainActivity;
import com.wizcodegroup.easytransport.R;
import com.wizcodegroup.easytransport.adapter.TestAdapter;
import com.wizcodegroup.easytransport.constanst.IWhereMyLocationConstants;


public class ReportAccidentFragment extends Fragment implements IWhereMyLocationConstants {


    private static Context con;

    public static final String TAG = TestFragment.class.getSimpleName();

    private View mRootView;

    private MainActivity mContext;

    private boolean isFindView;
    private GridView mGridView;

    //private ArrayList<HomeSearchObject> mListHomeObjects;
    private TestAdapter mHomeAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_test, container, false);
        return mRootView;

    }

    @Override
    public void onStart() {
        super.onStart();
        if(!isFindView){
            isFindView=true;
            this.findView();
            //iUI();
        }
    }


    private void findView(){
        this.mContext = (MainActivity) getActivity();


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mHomeAdapter!=null){
            mHomeAdapter.onDestroy();
        }
    }
}
