package com.wizcodegroup.easytransport;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
//import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.GoogleMap;
import com.wizcodegroup.easytransport.constanst.IWhereMyLocationConstants;
import com.wizcodegroup.easytransport.dataMng.TotalDataManager;
import com.wizcodegroup.easytransport.fragment.FragmentTotalLocation;
import com.wizcodegroup.easytransport.object.HomeSearchObject;
import com.ypyproductions.bitmap.ImageCache.ImageCacheParams;
import com.ypyproductions.bitmap.ImageFetcher;
import com.ypyproductions.utils.DirectionUtils;
import com.ypyproductions.utils.StringUtils;

import java.util.Locale;


public class TotalLocationInMapActivity extends ActionBarActivity implements IWhereMyLocationConstants,PopupMenu.OnMenuItemClickListener {

	public static final String TAG = TotalLocationInMapActivity.class.getSimpleName();
	public ImageFetcher mImgFetcher;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	
	private CharSequence mTitle;
	
	public Typeface mTypeFaceRobotoBold;
	public Typeface mTypeFaceRobotoLight;
	public int mResIcon=R.drawable.ic_launcher;
	private AdView adView;
    private Toolbar toolbar;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		this.setContentView(R.layout.activity_all_location);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		getActionBar().setDisplayHomeAsUpEnabled(false);
		getActionBar().setHomeButtonEnabled(false);
		
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

			@Override
			public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
			}

			@Override
			public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		ImageCacheParams cacheParams = new ImageCacheParams(this, "wheremylocation");
		float percent = 0.25f;
		cacheParams.setMemCacheSizePercent(percent);
		cacheParams.compressQuality = 100;

		mImgFetcher = new ImageFetcher(this, 240, 240);
		mImgFetcher.setLoadingImage(R.drawable.icon_location_default);
		mImgFetcher.addImageCache(getSupportFragmentManager(), cacheParams);
		
		this.initTypeFace();
		this.setUpTitle();
		//this.setUpLayoutAdmob();
	}
	
	private void setUpTitle(){
		HomeSearchObject mHomeSearchObject = TotalDataManager.getInstance().getHomeSearchSelected();
		if (mHomeSearchObject != null) {
			String name = mHomeSearchObject.getName();
			if(name.equals(getString(R.string.title_custom_search))){
				name= mHomeSearchObject.getRealName();
				if(StringUtils.isStringEmpty(name)){
					name= mHomeSearchObject.getKeyword().toUpperCase(Locale.US).replaceAll("\\_+", " ");
				}
			}
			setTitle(name);
		}
		mResIcon = TotalDataManager.getInstance().getResIconMapPin(this, mHomeSearchObject.getImg());
	}
	
	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			backToHome();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void initTypeFace() {
		mTypeFaceRobotoBold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");
		mTypeFaceRobotoLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
	}

	private void backToHome() {
		Intent mIntent = new Intent(this, MainSearchActivity.class);
		DirectionUtils.changeActivity(this, R.anim.slide_in_from_top, R.anim.slide_out_to_bottom, true, mIntent);
	}
	
	public void goToDetail(int indexLocation){
		Intent mIntent = new Intent(this, DetailLocationAcitivity.class);
		mIntent.putExtra(DetailLocationAcitivity.KEY_INDEX_LOCATION, indexLocation);
		mIntent.putExtra(KEY_START_FROM, START_FROM_TOTAL_PLACE);
		startActivity(mIntent);
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_my_location, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_menu:
			//showMenu(R.id.action_menu, R.menu.total_location_options,this);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (adView != null) {
			adView.destroy();
		}
		if (mImgFetcher != null) {
			mImgFetcher.setExitTasksEarly(true);
			mImgFetcher.closeCache();
			mImgFetcher = null;
		}
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_terrain:
			setViewModeForFragmentLocation(GoogleMap.MAP_TYPE_TERRAIN, getString(R.string.menu_location_terrain));
			return true;
		case R.id.action_satellite:
			setViewModeForFragmentLocation(GoogleMap.MAP_TYPE_SATELLITE, getString(R.string.menu_location_satellite));
			return true;
		case R.id.action_traffic:
			setViewModeForFragmentLocation(GoogleMap.MAP_TYPE_HYBRID, getString(R.string.menu_location_traffic));
			return true;
		default:
			return false;
		}
	}
	public void setViewModeForFragmentLocation(int viewMode, String mStrName) {
		FragmentTotalLocation mFragmentTotalLocation = (FragmentTotalLocation)getSupportFragmentManager().findFragmentById(R.id.fragment_location_home);
		mFragmentTotalLocation.setMapType(viewMode, mStrName);
	}
	

}
