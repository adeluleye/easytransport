package com.wizcodegroup.easytransport;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.wizcodegroup.easytransport.adapter.DrawerSearchAdapter;
import com.wizcodegroup.easytransport.adapter.PlaceAdapter;
import com.wizcodegroup.easytransport.constanst.IWhereMyLocationConstants;
import com.wizcodegroup.easytransport.dataMng.TotalDataManager;
import com.wizcodegroup.easytransport.dataMng.YPYNetUtils;
import com.wizcodegroup.easytransport.object.HomeSearchObject;
import com.wizcodegroup.easytransport.object.KeywordObject;
import com.wizcodegroup.easytransport.object.PlaceObject;
import com.wizcodegroup.easytransport.object.ResponcePlaceResult;
import com.wizcodegroup.easytransport.provider.MySuggestionDAO;
import com.wizcodegroup.easytransport.settings.SettingManager;
import com.ypyproductions.bitmap.ImageCache.ImageCacheParams;
import com.ypyproductions.bitmap.ImageFetcher;
import com.ypyproductions.location.utils.LocationUtils;
import com.ypyproductions.net.task.DBTask;
import com.ypyproductions.net.task.IDBCallback;
import com.ypyproductions.net.task.IDBTaskListener;
import com.ypyproductions.utils.ApplicationUtils;
import com.ypyproductions.utils.DBListExcuteAction;
import com.ypyproductions.utils.DBLog;
import com.ypyproductions.utils.DirectionUtils;
import com.ypyproductions.utils.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class MainSearchActivity extends DBFragmentActivity implements IWhereMyLocationConstants, PopupMenu.OnMenuItemClickListener, OnScrollListener {

	public static final String TAG = MainSearchActivity.class.getSimpleName();

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerListView;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private ListView mLocationListView;

	private CharSequence mTitle;

	private DrawerSearchAdapter mDrawerAdapter;

	public Typeface mTypeFaceRobotoBold;

	public Typeface mTypeFaceRobotoLight;

	public ImageFetcher mImgFetcher;

	private ArrayList<HomeSearchObject> mListHomeObjects;

	private TrackLocationBroadcast mTrackLocationReceiver;

	//private TrackRecordServiceController mTrackingController;

	private DBTask mDBTask;

	private TotalDataManager mTotalMng;
	private Handler mHandler = new Handler();

	private boolean isAllowRefresh;
	private Location mCurrentLocation;

	private PlaceAdapter mPlaceAdapter;

	private TextView mTvResult;
	private boolean isAllowDestroyAll = true;

	private AdView adView;

	private boolean isAllowAddPage = false;
	private boolean isStartAddingPage;

	private View mFooterView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_location);

		mTitle = mDrawerTitle = getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerListView = (ListView) findViewById(R.id.left_drawer);
		mLocationListView = (ListView) findViewById(R.id.list_detail_search);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		mTvResult = (TextView) findViewById(R.id.tv_no_result);
		
		this.mFooterView = findViewById(R.id.layout_footer);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
			@Override
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu();
			}
			@Override
			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		ImageCacheParams cacheParams = new ImageCacheParams(this, "cache_search");
		float percent = 0.25f;
		cacheParams.setMemCacheSizePercent(percent);
		cacheParams.compressQuality = 100;

		mImgFetcher = new ImageFetcher(this, 240, 240);
		mImgFetcher.setLoadingImage(R.drawable.icon_location_default);
		mImgFetcher.addImageCache(getSupportFragmentManager(), cacheParams);

		mTotalMng = TotalDataManager.getInstance();
		mCurrentLocation = mTotalMng.getCurrentLocation();

		this.initTypeFace();
		this.setUpDrawer();
		//this.setUpLayoutAdmob();
		this.handleIntent(getIntent());
		
	}


	private void showFooterView() {
		if(mFooterView.getVisibility()!= View.VISIBLE){
			this.mFooterView.setVisibility(View.VISIBLE);
		}
	}
	
	private void hideFooterView(){
		if(mFooterView.getVisibility()== View.VISIBLE){
			this.mFooterView.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!ApplicationUtils.isOnline(this)) {
			showDialogFragment(DIALOG_LOSE_CONNECTION);
			return;
		}
		else {
			/*if (mTrackingController == null) {
				//mTrackingController = TrackRecordServiceController.getInstance();
				//mTrackingController.bindTrackingService(this, null);
				//mTrackingController.startTracking();
				registerBroadCast();
				startFindLocation();
			}*/
			isAllowRefresh = true;
		}
	}

	private synchronized void startFindLocation() {
		if (mCurrentLocation != null) {
			mTvResult.setVisibility(View.GONE);
			final HomeSearchObject mHomeSearchObject = mTotalMng.getHomeSearchSelected();
			if (mHomeSearchObject != null) {
				ResponcePlaceResult mResponcePlaceResult = mHomeSearchObject.getResponcePlaceResult();
				if (mResponcePlaceResult != null) {
					ArrayList<PlaceObject> mListPlaceObjects = mResponcePlaceResult.getListPlaceObjects();
					if (mListPlaceObjects == null) {
						mListPlaceObjects = new ArrayList<PlaceObject>();
					}
					updateOrCreatePlaceAdapter(mResponcePlaceResult,mListPlaceObjects);
					return;
				}
				if(mPlaceAdapter!=null){
					mLocationListView.post(new Runnable() {
						@Override
						public void run() {
							mLocationListView.setSelection(0);
							hideFooterView();
						}
					});
				}
				mDBTask = new DBTask(new IDBTaskListener() {
					private boolean isSuccess;

					@Override
					public void onPreExcute() {
						String name = mHomeSearchObject.getName();
						if(name.equals(getString(R.string.title_custom_search))){
							name= mHomeSearchObject.getRealName();
							if(StringUtils.isStringEmpty(name)){
								name= mHomeSearchObject.getKeyword().toUpperCase(Locale.US).replaceAll("\\_+", " ");
							}
						}
						else{
							name=mHomeSearchObject.getRealName();
						}
						String message = String.format(getString(R.string.info_format_process_find_location), name);
						showProgressDialog(message);
					}

					@Override
					public void onDoInBackground() {
						ResponcePlaceResult mResponcePlaceResult = null;
						if (mHomeSearchObject.getType() == TYPE_SEARCH_BY_TYPES) {
							mResponcePlaceResult = YPYNetUtils.getListPlacesBaseOnType(MainSearchActivity.this, mCurrentLocation.getLongitude(), mCurrentLocation.getLatitude(),
									mHomeSearchObject.getKeyword());
						}
						else {
							mResponcePlaceResult = YPYNetUtils.getListPlacesBaseOnText(MainSearchActivity.this, mCurrentLocation.getLongitude(), mCurrentLocation.getLatitude(),
									mHomeSearchObject.getKeyword());
						}
						if (mResponcePlaceResult != null) {
							String status = mResponcePlaceResult.getStatus();
							DBLog.d(TAG, "==============>status=" + status);
							if (!StringUtils.isStringEmpty(status)) {
								mHomeSearchObject.setResponcePlaceResult(mResponcePlaceResult);
								ArrayList<PlaceObject> mListPlaceObjects = mResponcePlaceResult.getListPlaceObjects();
								if (mListPlaceObjects != null && mListPlaceObjects.size() > 0) {
									for (PlaceObject mPlaceObject : mListPlaceObjects) {
										float distance = LocationUtils.calculateDistance(mCurrentLocation, mPlaceObject.getLocation()) / 1000f;
										mPlaceObject.setDistance(distance);
										mPlaceObject.setCategory(mHomeSearchObject.getImg());
									}
									String typeSorting = SettingManager.getPiority(MainSearchActivity.this);
									if (typeSorting.equals(PIORITY_DISTANCE)) {
										sortingBy(mListPlaceObjects, PIORITY_DISTANCE);
									}
									else if (typeSorting.equals(PIORITY_RATING)) {
										sortingBy(mListPlaceObjects, PIORITY_RATING);
									}
								}
								isSuccess = true;
							}
						}
					}

					@Override
					public void onPostExcute() {
						dimissProgressDialog();
						if (!isSuccess) {
							Toast.makeText(MainSearchActivity.this, R.string.info_server_error, Toast.LENGTH_LONG).show();
							isAllowAddPage = false;
						}
						else {
							ResponcePlaceResult mResponcePlaceResult = mHomeSearchObject.getResponcePlaceResult();
							ArrayList<PlaceObject> mListPlaceObjects = mResponcePlaceResult.getListPlaceObjects();
							if (mListPlaceObjects == null) {
								mListPlaceObjects = new ArrayList<PlaceObject>();
							}
							updateOrCreatePlaceAdapter(mResponcePlaceResult,mListPlaceObjects);
						}
					}
				});
				mDBTask.execute();
			}
		}
		else {
			showProgressDialog(R.string.info_process_find_location);
		}
	}

	private void updateOrCreatePlaceAdapter(ResponcePlaceResult mResponcePlaceResult,final ArrayList<PlaceObject> mListPlaceObjects) {
		if (mListPlaceObjects != null) {
			if (mListPlaceObjects.size() == 0) {
				mTvResult.setVisibility(View.VISIBLE);
			}
			if(!StringUtils.isStringEmpty(mResponcePlaceResult.getPageToken())){
				isAllowAddPage = true;
				this.mLocationListView.setOnScrollListener(this);
			}
			else{
				isAllowAddPage = false;
				this.mLocationListView.setOnScrollListener(null);
				hideFooterView();
			}
			if (mPlaceAdapter == null) {
				mPlaceAdapter = new PlaceAdapter(MainSearchActivity.this, mListPlaceObjects, mTypeFaceRobotoBold, mTypeFaceRobotoLight, mImgFetcher);
				mLocationListView.setAdapter(mPlaceAdapter);
				mLocationListView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
						if(isStartAddingPage){
							Toast.makeText(MainSearchActivity.this, R.string.info_loading_more, Toast.LENGTH_SHORT).show();
							return;
						}
						isAllowDestroyAll = false;
						Intent mIntent = new Intent(MainSearchActivity.this, DetailLocationAcitivity.class);
						mIntent.putExtra(DetailLocationAcitivity.KEY_INDEX_LOCATION, position);
						mIntent.putExtra(KEY_START_FROM, START_FROM_SEARCH);
						onDestroyTrackingService();
						DirectionUtils.changeActivity(MainSearchActivity.this, R.anim.slide_in_from_right, R.anim.slide_out_to_left, true, mIntent);
//						startActivity(mIntent);
//						finish();
					}
				});
			}
			else {
				mPlaceAdapter.setListPlaceObjects(mListPlaceObjects);
				mLocationListView.post(new Runnable() {
					@Override
					public void run() {
						mLocationListView.setSelection(0);
					}
				});
			}
			return;
		}
	}

	private void initTypeFace() {
		mTypeFaceRobotoBold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");
		mTypeFaceRobotoLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
	}

	private void setUpDrawer() {
		mTotalMng = TotalDataManager.getInstance();
		mListHomeObjects = mTotalMng.getListHomeSearchObjects();
		if (mListHomeObjects != null && mListHomeObjects.size() > 0) {
			int size = mListHomeObjects.size();
			final ArrayList<HomeSearchObject> listHomeSearchObjects = new ArrayList<HomeSearchObject>();
			for(int i=0;i<size;i++){
				if(i!=0){
					listHomeSearchObjects.add(mListHomeObjects.get(i));
				}
			}
			mDrawerAdapter = new DrawerSearchAdapter(this, listHomeSearchObjects, mTypeFaceRobotoBold, mTypeFaceRobotoLight, mImgFetcher);
			mDrawerListView.setAdapter(mDrawerAdapter);
			mDrawerListView.setOnItemClickListener(new ListView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
					if(isStartAddingPage){
						Toast.makeText(MainSearchActivity.this, R.string.info_loading_more, Toast.LENGTH_SHORT).show();
						mDrawerLayout.closeDrawer(mDrawerListView);
						return;
					}
					setTitle(listHomeSearchObjects.get(position).getName());
					mDrawerAdapter.setSelected(position);
					mDrawerLayout.closeDrawer(mDrawerListView);
					DBListExcuteAction.getInstance().queueAction(new IDBCallback() {
						@Override
						public void onAction() {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									startFindLocation();
								}
							});
						}
					});
				}
			});
			HomeSearchObject mHomeSearchObject = mTotalMng.getHomeSearchSelected();
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
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main_search, menu);
		
		MenuItem menuItem = menu.findItem(R.id.action_search);
		SearchView searchView = (SearchView) menuItem.getActionView();
		searchView.setSubmitButtonEnabled(true);
		searchView.setQueryHint(Html.fromHtml("<font color = #ffffff>" + getResources().getString(R.string.title_search) + "</font>"));

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		switch (item.getItemId()) {
		case R.id.action_menu:
			if(isStartAddingPage){
				Toast.makeText(this, R.string.info_loading_more, Toast.LENGTH_SHORT).show();
				return true;
			}
			showMenu(R.id.action_menu, R.menu.menu_sort_by);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		if (mDrawerToggle != null) {
			mDrawerToggle.syncState();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (mDrawerToggle != null) {
			mDrawerToggle.onConfigurationChanged(newConfig);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (adView != null) {
			adView.destroy();
		}
		if(mDrawerAdapter!=null){
			mDrawerAdapter.onDestroy();
		}
		if(mDBTask!=null){
			mDBTask.cancel(true);
		}
		DBListExcuteAction.getInstance().onDestroy();
		if (mImgFetcher != null) {
			mImgFetcher.setExitTasksEarly(true);
			mImgFetcher.closeCache();
			mImgFetcher = null;
		}
		mHandler.removeCallbacksAndMessages(null);
		if (isAllowDestroyAll) {
			mCurrentLocation = null;
			mTotalMng.setCurrentLocation(null);
			mTotalMng.onResetResultSearch(true);
		}
		unRegisterBroadCast();
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	public void showMenu(int resMenuId, int resId) {
		View mView = findViewById(resMenuId);
		PopupMenu popup = new PopupMenu(this, mView);
		try {
			Field[] fields = popup.getClass().getDeclaredFields();
			for (Field field : fields) {
				if ("mPopup".equals(field.getName())) {
					field.setAccessible(true);
					Object menuPopupHelper = field.get(popup);
					Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
					Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
					setForceIcons.invoke(menuPopupHelper, true);
					break;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		MenuInflater inflater = popup.getMenuInflater();
		inflater.inflate(resId, popup.getMenu());
		popup.setOnMenuItemClickListener(this);
		popup.show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent mIntent = new Intent(this, MainActivity.class);
			mIntent.putExtra(KEY_START_FROM, START_FROM_SEARCH);
			
			onDestroyTrackingService();
			DirectionUtils.changeActivity(this, R.anim.slide_in_from_left, R.anim.slide_out_to_right, true, mIntent);
//			startActivity(mIntent);
//			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void registerBroadCast() {
		if (mTrackLocationReceiver == null) {
			try {
				mTrackLocationReceiver = new TrackLocationBroadcast();
				IntentFilter mIntentFilter = new IntentFilter();
				//mIntentFilter.addAction(TrackRecordingService.ACTION_UPDATE_LOCATION);
				registerReceiver(mTrackLocationReceiver, mIntentFilter);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void onDestroyTrackingService() {
		try {
			/*if (mTrackingController != null) {
				//mTrackingController.stopTracking();
				//mTrackingController.unbindTrackingServiceAndStop(this);
				//mTrackingController.onDestroy();
				//mTrackingController = null;
			}*/
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void unRegisterBroadCast() {
		try {
			if (mTrackLocationReceiver != null) {
				unregisterReceiver(mTrackLocationReceiver);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class TrackLocationBroadcast extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				if (intent != null) {
					String action = intent.getAction();
					/*if (!StringUtils.isStringEmpty(action) && action.equals(TrackRecordingService.ACTION_UPDATE_LOCATION)) {
						//final double mLastLocLat = intent.getDoubleExtra(TrackRecordingService.KEY_LAT, 0);
						//final double mLastLocLng = intent.getDoubleExtra(TrackRecordingService.KEY_LNG, 0);
						DBListExcuteAction.getInstance().queueAction(new IDBCallback() {
							@Override
							public void onAction() {
								boolean isNeedRefresh = false;
								if (mCurrentLocation == null) {
									if (mLastLocLat != INVALID_VALUE && mLastLocLng != INVALID_VALUE) {
										//mCurrentLocation = new Location(LocationManager.GPS_PROVIDER);
										//mCurrentLocation.setLongitude(mLastLocLng);
										//mCurrentLocation.setLatitude(mLastLocLat);
										isNeedRefresh = true;
									}
								}
								else {
									if (mLastLocLat != INVALID_VALUE && mLastLocLng != INVALID_VALUE) {
										Location mNewLocation = new Location(LocationManager.GPS_PROVIDER);
										//mNewLocation.setLongitude(mLastLocLng);
										//mNewLocation.setLatitude(mLastLocLat);

										float distance = LocationUtils.calculateDistance(mCurrentLocation, mNewLocation);
										DBLog.d(TAG, "============>distance=" + distance);
										if (distance > MAX_DISTANCE_TO_UPDATE) {
											isNeedRefresh = true;
										}
										mCurrentLocation = null;
										mCurrentLocation = mNewLocation;
									}
								}
								DBLog.d(TAG, "============>isNeedRefresh=" + isNeedRefresh + "=======>isAllowRefresh=" + isAllowRefresh);
								if (isNeedRefresh && isAllowRefresh) {
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											mTotalMng.setCurrentLocation(mCurrentLocation);
											mTotalMng.onResetResultSearch(false);
											startFindLocation();
										}
									});
								}
							}
						});
					}*/
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.sort_by_distance:
			if (mPlaceAdapter != null) {
				ArrayList<PlaceObject> mListPlaceObjects = mPlaceAdapter.getListPlaceObjects();
				if (mListPlaceObjects != null && mListPlaceObjects.size() > 0) {
					sortingBy(mListPlaceObjects, PIORITY_DISTANCE);
					mPlaceAdapter.notifyDataSetChanged();
					SettingManager.setPiority(this, PIORITY_DISTANCE);
				}
			}
			return true;
		case R.id.sort_by_rating:
			if (mPlaceAdapter != null) {
				ArrayList<PlaceObject> mListPlaceObjects = mPlaceAdapter.getListPlaceObjects();
				if (mListPlaceObjects != null && mListPlaceObjects.size() > 0) {
					sortingBy(mListPlaceObjects, PIORITY_RATING);
					mPlaceAdapter.notifyDataSetChanged();
					SettingManager.setPiority(this, PIORITY_RATING);
				}
			}
			return true;
		case R.id.action_map:
			if(isStartAddingPage){
				Toast.makeText(this, R.string.info_loading_more, Toast.LENGTH_SHORT).show();
				return true;
			}
			final HomeSearchObject mHomeSearchObject = mTotalMng.getHomeSearchSelected();
			if (mHomeSearchObject != null) {
				ResponcePlaceResult mResponcePlaceResult = mHomeSearchObject.getResponcePlaceResult();
				if (mResponcePlaceResult == null || mResponcePlaceResult.getListPlaceObjects() == null || mResponcePlaceResult.getListPlaceObjects().size() == 0) {
					Toast.makeText(this, R.string.info_no_location, Toast.LENGTH_LONG).show();
					return super.onOptionsItemSelected(item);
				}
			}
			isAllowDestroyAll = false;
			Intent mIntent = new Intent(this, TotalLocationInMapActivity.class);
			onDestroyTrackingService();
			DirectionUtils.changeActivity(this, R.anim.slide_in_from_bottom, R.anim.slide_out_to_top, true, mIntent);
//			startActivity(mIntent);
//			finish();
			return true;
		case R.id.action_refresh:
			if(isStartAddingPage){
				Toast.makeText(this, R.string.info_loading_more, Toast.LENGTH_SHORT).show();
				return true;
			}
			isAllowAddPage=false;
			isStartAddingPage=false;
			final HomeSearchObject mHomeSearchObject1 = mTotalMng.getHomeSearchSelected();
			if (mHomeSearchObject1 != null) {
				mHomeSearchObject1.setResponcePlaceResult(null);
				startFindLocation();
			}
			return true;
		default:
			return false;
		}
	}

	private void sortingBy(ArrayList<PlaceObject> mListPlaceObjects, final String type) {
		if (mListPlaceObjects != null && mListPlaceObjects.size() > 0) {
			Collections.sort(mListPlaceObjects, new Comparator<PlaceObject>() {
                @Override
                public int compare(PlaceObject lhs, PlaceObject rhs) {
                    try {
                        if (type.equals(PIORITY_RATING)) {
                            float dis1 = lhs.getRating();
                            float dis2 = rhs.getRating();
                            if (dis1 < dis2) {
                                return 1;
                            } else {
                                return -1;
                            }
                        } else if (type.equals(PIORITY_DISTANCE)) {
                            float dis1 = lhs.getDistance();
                            float dis2 = rhs.getDistance();
                            if (dis1 < dis2) {
                                return -1;
                            } else {
                                return 1;
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            });
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (mPlaceAdapter != null) {
			if (isAllowAddPage) {
				int size = mPlaceAdapter.getCount();
				if (mLocationListView.getLastVisiblePosition() == size - 1) {
					if (ApplicationUtils.isOnline(this)) {
						showFooterView();
						if (!isStartAddingPage) {
							isStartAddingPage = true;
							onLoadNextPlaceObject();
						}
					}
				}
				else{
					if(!isStartAddingPage){
						hideFooterView();
					}
				}
			}
			else {
				hideFooterView();
			}
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	private void onLoadNextPlaceObject() {
		final HomeSearchObject mHomeSearchObject = mTotalMng.getHomeSearchSelected();
		if (mHomeSearchObject != null) {
			final ResponcePlaceResult mResponcePlaceResult = mHomeSearchObject.getResponcePlaceResult();
			if (mResponcePlaceResult != null) {
				final ArrayList<PlaceObject> mListPlaceObjects = mResponcePlaceResult.getListPlaceObjects();
				if (mListPlaceObjects != null && mListPlaceObjects.size() > 0) {
					mDBTask = new DBTask(new IDBTaskListener() {
						private boolean isSuccess;
						private ResponcePlaceResult mNewResponcePlaceResult;

						@Override
						public void onPreExcute() {
						}

						@Override
						public void onDoInBackground() {
							if (mHomeSearchObject.getType() == TYPE_SEARCH_BY_TYPES) {
								mNewResponcePlaceResult = YPYNetUtils.getListNextPlacesBaseOnType(MainSearchActivity.this, mCurrentLocation.getLongitude(),
										mCurrentLocation.getLatitude(), mHomeSearchObject.getKeyword(), mResponcePlaceResult.getPageToken());
							}
							else {
								mNewResponcePlaceResult = YPYNetUtils.getListNextPlacesBaseOnText(MainSearchActivity.this, mCurrentLocation.getLongitude(),
										mCurrentLocation.getLatitude(), mHomeSearchObject.getKeyword(), mResponcePlaceResult.getPageToken());
							}
							if (mResponcePlaceResult != null) {
								String status = mResponcePlaceResult.getStatus();
								DBLog.d(TAG, "==============>status=" + status);
								if (!StringUtils.isStringEmpty(status) && mNewResponcePlaceResult!=null) {
									ArrayList<PlaceObject> mListNewPlaceObjects = mNewResponcePlaceResult.getListPlaceObjects();
									if (mListPlaceObjects != null && mListPlaceObjects.size() > 0) {
										mResponcePlaceResult.setPageToken(mNewResponcePlaceResult.getPageToken());
										for (PlaceObject mPlaceObject : mListNewPlaceObjects) {
											float distance = LocationUtils.calculateDistance(mCurrentLocation, mPlaceObject.getLocation()) / 1000f;
											mPlaceObject.setDistance(distance);
											mPlaceObject.setCategory(mHomeSearchObject.getImg());
										}
										String typeSorting = SettingManager.getPiority(MainSearchActivity.this);
										if (typeSorting.equals(PIORITY_DISTANCE)) {
											sortingBy(mListNewPlaceObjects, PIORITY_DISTANCE);
										}
										else if (typeSorting.equals(PIORITY_RATING)) {
											sortingBy(mListNewPlaceObjects, PIORITY_RATING);
										}
										for (PlaceObject mPlaceObject : mListNewPlaceObjects) {
											mListPlaceObjects.add(mPlaceObject);
										}
										mListNewPlaceObjects.clear();
									}
									isSuccess = true;
								}
							}
						}

						@Override
						public void onPostExcute() {
							if (!isSuccess) {
								Toast.makeText(MainSearchActivity.this, R.string.info_server_error, Toast.LENGTH_LONG).show();
								isStartAddingPage = false;
							}
							else {
								if (mPlaceAdapter != null) {
									mPlaceAdapter.notifyDataSetChanged();
								}
								isStartAddingPage = false;
								isAllowAddPage=false;
								if(mNewResponcePlaceResult!=null){
									if(!StringUtils.isStringEmpty(mNewResponcePlaceResult.getPageToken())){
										isAllowAddPage=true;
										mLocationListView.setOnScrollListener(MainSearchActivity.this);
									}
									else{
										mLocationListView.setOnScrollListener(null);
										hideFooterView();
									}
								}
							}
						}
					});
					mDBTask.execute();
				}
				else {
					isAllowAddPage = false;
				}
			}
		}

	}
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			DBLog.d(TAG, "===============>ACTION_SEARCH =" + query);
			processSearchData(TYPE_SEARCH_BY_TEXT, query,query,true);
		}
		else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			Uri mQueryUri = intent.getData();
			if(mQueryUri!=null){
				DBLog.d(TAG, "===============>mQueryUri=" + mQueryUri.toString());
				String keyword = intent.getStringExtra(SearchManager.EXTRA_DATA_KEY);
				KeywordObject mKeywordObject = TotalDataManager.getInstance().getKeyWordObject(keyword);
				String realName="";
				if(mKeywordObject!=null){
					realName=mKeywordObject.getName();
				}
				processSearchData(TYPE_SEARCH_BY_TYPES, keyword,realName, false);
			}
		}
	}
	
	private void processSearchData(int type,String query,String realname,boolean isAllowAddRecent){
		HomeSearchObject mHomeSearchObject = TotalDataManager.getInstance().findHomeSearchObject(query);
		if(mHomeSearchObject!=null){
			TotalDataManager.getInstance().setSelectedObject(mHomeSearchObject);
			if(mDrawerAdapter!=null){
				mDrawerAdapter.notifyDataSetChanged();
			}
		}
		else{
			TotalDataManager.getInstance().setSelectedObject(0);
			mHomeSearchObject = TotalDataManager.getInstance().getListHomeSearchObjects().get(0);
			if(mHomeSearchObject!=null){
				mHomeSearchObject.setResponcePlaceResult(null);
				mHomeSearchObject.setKeyword(query);
				mHomeSearchObject.setType(type);
				mHomeSearchObject.setRealName(realname);
				if(isAllowAddRecent){
					KeywordObject mKeywordObject = MySuggestionDAO.getPrivateData(this, query);
					DBLog.d(TAG, "==============>mKeywordObject="+mKeywordObject);
					if(mKeywordObject==null){
						KeywordObject mKeywordObject2 = new KeywordObject(query, query);
						MySuggestionDAO.insertData(this, mKeywordObject2);
					}
				}
			}
		}
		String name = mHomeSearchObject.getName();
		if(name.equals(getString(R.string.title_custom_search))){
			name= mHomeSearchObject.getRealName();
			if(StringUtils.isStringEmpty(name)){
				name= mHomeSearchObject.getKeyword().toUpperCase(Locale.US).replaceAll("\\_+", " ");
			}
		}
		setTitle(name);
		startFindLocation();
	}

}
