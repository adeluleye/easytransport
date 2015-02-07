package com.wizcodegroup.easytransport;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wizcodegroup.easytransport.fragment.FareFragment;
import com.wizcodegroup.easytransport.fragment.FragmentHome;

import com.wizcodegroup.easytransport.fragment.TipsFragment;
import com.ypyproductions.utils.StringUtils;


import com.wizcodegroup.easytransport.adapter.DrawerAdapter;
import com.wizcodegroup.easytransport.adapter.NavigationListAdapter;
import com.wizcodegroup.easytransport.constanst.IWhereMyLocationConstants;
import com.wizcodegroup.easytransport.dataMng.NavigationItem;
import com.wizcodegroup.easytransport.extra.AllConstants;
import com.wizcodegroup.easytransport.fragment.FragmentAboutUs;
import com.wizcodegroup.easytransport.fragment.HomeFragment;
import com.wizcodegroup.easytransport.fragment.ReportAccidentFragment;
import com.wizcodegroup.easytransport.fragment.ReportTrafficFragment;
import com.ypyproductions.bitmap.ImageFetcher;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity implements IWhereMyLocationConstants {
//public class MainActivity extends DBFragmentActivity implements IWhereMyLocationConstants, PopupMenu.OnMenuItemClickListener {
    public static final String TAG = MainActivity.class.getSimpleName();


    private Fragment mFragment = null;
    private FragmentManager mFragmentManager;
    private ListView mDrawerListView;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mListStrDrawerTitles;
    private DrawerAdapter mDrawerAdapter;
    private TypedArray mListStrDrawerIcons;
    private TypedArray mListIconsSelected;
    private ArrayList<NavigationItem> navDrawerItems;
    private ArrayList<NavigationItem> navDrawerItemsSelected;
    private NavigationListAdapter mNavigationListAdapter;
    private Intent mIntent = null;

    public Typeface mTypeFaceRobotoBold;

    public Typeface mTypeFaceRobotoLight;
    public ImageFetcher mImgFetcher;
    private boolean isShowDialog;
    private boolean isChecking;

    private ArrayList<Fragment> mListFragments = new ArrayList<Fragment>();


    private int mCurrentIndex = HOME_INDEX;

    private Menu mMenu;

    private String mStartFrom;

    private static Context con;

    private Toolbar toolbar;

    private DrawerLayout Drawer;                                  // Declaring DrawerLayout

    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        con = this;

        Intent mIntent = getIntent();
        if (mIntent != null) {
            mStartFrom = mIntent.getStringExtra(KEY_START_FROM);
        }

        toolbar = (Toolbar) findViewById(R.id.tool_bar);

        mTitle = mDrawerTitle = getTitle();
        mListStrDrawerTitles = getResources().getStringArray(R.array.list_options);
        mListIconsSelected = getResources().obtainTypedArray(R.array.list_icons_selected);


        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);
        mDrawerListView = (ListView) findViewById(R.id.left_drawer);
        Drawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        navDrawerItems = new ArrayList<NavigationItem>();
        //navDrawerItemsSelected = new ArrayList<NavigationItem>();

        navDrawerItems.add(new NavigationItem(mListStrDrawerTitles[0], mListIconsSelected
                .getResourceId(0, -1)));
        navDrawerItems.add(new NavigationItem(mListStrDrawerTitles[1], mListIconsSelected
                .getResourceId(1, -1)));
        navDrawerItems.add(new NavigationItem(mListStrDrawerTitles[2], mListIconsSelected
                .getResourceId(2, -1)));
        navDrawerItems.add(new NavigationItem(mListStrDrawerTitles[3], mListIconsSelected
                .getResourceId(3, -1)));
        navDrawerItems.add(new NavigationItem(mListStrDrawerTitles[4], mListIconsSelected
                .getResourceId(4, -1)));
        navDrawerItems.add(new NavigationItem(mListStrDrawerTitles[5], mListIconsSelected
                .getResourceId(5, -1)));
        navDrawerItems.add(new NavigationItem(mListStrDrawerTitles[6], mListIconsSelected
                .getResourceId(6, -1)));

        mListIconsSelected.recycle();
        mDrawerListView.setOnItemClickListener(new SlideMenuClickListener());


        this.setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this,Drawer,toolbar,R.string.drawer_open,R.string.drawer_close){



            @Override
            public void onDrawerClosed(View view) {

                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu();

            }

            @Override
            public void onDrawerOpened(View drawerView) {

                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();


            }


        }; // Drawer Toggle Object Made
        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();
        if (savedInstanceState == null) {

            mNavigationListAdapter = new NavigationListAdapter(this, navDrawerItems, mTypeFaceRobotoBold, mTypeFaceRobotoBold);
            displayView(0);
            mNavigationListAdapter.setSelectedDrawer(0);

            mTypeFaceRobotoBold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");

        }
        this.initTypeFace();
        mNavigationListAdapter = new NavigationListAdapter(this, navDrawerItems, mTypeFaceRobotoBold, mTypeFaceRobotoLight);
        mDrawerListView.setAdapter(mNavigationListAdapter);

    }

    private void initTypeFace() {
        mTypeFaceRobotoBold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");
        mTypeFaceRobotoLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
    }

    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            mNavigationListAdapter.setSelectedDrawer(position);
            displayView(position);

        }
    }

    private void displayView(int position) {
        switch (position) {
            case HOME_INDEX:

                showFragmentByTag(TAG_HOME, HOME_INDEX);
                //mIntent = new Intent(this, RegisterActivity.class);
                break;
            case FARE_INDEX:
                showFragmentByTag(TAG_FARE, FARE_INDEX);
                //mIntent = new Intent(this, PublishActivity.class);
                break;
            case TRAFFIC_INDEX:
                showFragmentByTag(TAG_TRAFFIC, TRAFFIC_INDEX);
                //mFragment = new HomeFragment();
                break;
            case ACCIDENT_INDEX:
                showFragmentByTag(TAG_ACCIDENT, ACCIDENT_INDEX);
                break;
            case TIP_INDEX:
                showFragmentByTag(TAG_TIP, TIP_INDEX);
                break;
            case SETTING_INDEX:
                //showFragmentByTag(TAG_SETTINGS, SETTING_INDEX);
                //mFragment = new HomeFragment();
                Intent mIntent = new Intent(this, CityGuideActivity.class);
                startActivity(mIntent);
                //showFragmentByTag(TAG_SETTINGS, SETTING_INDEX);
                break;
            case HELP_INDEX:
                showFragmentByTag(TAG_HELP, HELP_INDEX);
                break;

            default:
                break;
        }


    }

    public void showFragmentByTag(String mTag, int index) {
        if (StringUtils.isStringEmpty(mTag)) {
            return;
        }
        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
        Fragment mFragment = mFragmentManager.findFragmentByTag(mTag);

        this.mCurrentIndex = index;

        if (mListFragments.size() > 0) {
            for (Fragment mFragment1 : mListFragments) {
                if (mFragment1 != null && !mFragment1.getTag().equals(mTag)) {
                    mFragmentTransaction.hide(mFragment1);
                }
            }
        }
        if (mFragment == null) {
            if (mTag.equals(TAG_HELP)) {
                FragmentAboutUs mFragmentAboutUs = new FragmentAboutUs();
                mFragmentTransaction.add(R.id.frameContainer, mFragmentAboutUs, mTag);
                mListFragments.add(mFragmentAboutUs);
            }
            else if (mTag.equals(TAG_HOME)) {
                HomeFragment mFragmentHome = new HomeFragment();
                mFragmentTransaction.add(R.id.frameContainer, mFragmentHome, mTag);
                mListFragments.add(mFragmentHome);

            }
            else if (mTag.equals(TAG_TIP)) {
                TipsFragment mFragmentTip = new TipsFragment();
                mFragmentTransaction.add(R.id.frameContainer, mFragmentTip, mTag);
                mListFragments.add(mFragmentTip);

            }
            else if (mTag.equals(TAG_SETTINGS)) {
                FragmentHome mFragmentNearby = new FragmentHome();
                mFragmentTransaction.add(R.id.frameContainer, mFragmentNearby, mTag);
                mListFragments.add(mFragmentNearby);

                /*FragmentMyLocation mFragmentMyLocation = new FragmentMyLocation();
                mFragmentTransaction.add(R.id.frameContainer, mFragmentMyLocation, mTag);
                mListFragments.add(mFragmentMyLocation);*/
            }
            else if (mTag.equals(TAG_FARE)) {
                FareFragment mFragmentFare = new FareFragment();
                mFragmentTransaction.add(R.id.frameContainer, mFragmentFare, mTag);
                mListFragments.add(mFragmentFare);
            }
            else if (mTag.equals(TAG_TRAFFIC)) {
                ReportTrafficFragment mFragmentTraffic = new ReportTrafficFragment();
                mFragmentTransaction.add(R.id.frameContainer, mFragmentTraffic, mTag);
                mListFragments.add(mFragmentTraffic);
            }

            else if (mTag.equals(TAG_ACCIDENT)) {
                ReportAccidentFragment mFragmentAccident = new ReportAccidentFragment();
                mFragmentTransaction.add(R.id.frameContainer, mFragmentAccident, mTag);
                mListFragments.add(mFragmentAccident);
            }
        }
        else {
            mFragmentTransaction.show(mFragment);
        }
        setTitle(mListStrDrawerTitles[index]);
        mFragmentTransaction.commit();
        Drawer.closeDrawer(mDrawerListView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(mDrawerToggle != null) {
            if (mDrawerToggle.onOptionsItemSelected(item)) {
                return true;
            }

            switch (item.getItemId()) {
                /*case R.id.action_menu:
                    if (mCurrentIndex == MY_LOCATION_INDEX) {
                        showMenu(R.id.action_menu, R.menu.location_options, this);
                    }
                    break;
                case R.id.action_delete_all:
                    if (mCurrentIndex == FAVORITES_INDEX) {
                        for (android.support.v4.app.Fragment mFragment : mListFragments) {
                            if (mFragment instanceof FragmentFavorites) {
                                ((FragmentFavorites) mFragment).deleteAllFavorite();
                                break;
                            }
                        }
                    }
                    break;*/
		/*case R.id.action_quick_distance:
			showDialogChangeDistance();
			break;*/

                case R.id.fbook:
                    AllConstants.webUrl = "https://www.facebook.com/groups/1582053832032461";
                    AllConstants.topTitle = "EASYTRANSPORT FAN PAGE";
                    Intent next = new Intent(con, DroidWebViewActivity.class);
                    next.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(next);
                    break;

                case R.id.publish:
                    Intent publish = new Intent(con, PublishActivity.class);
                    publish.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(publish);
                    break;

                case R.id.share:
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT,
                            "EasyTransport");
                    sharingIntent.putExtra(Intent.EXTRA_TEXT,
                            "Hi friend! I have found a good app which helps me with my daily commuting life. " +
                                    "Also, its interesting to know I can now calculate the fare price of taxi, which prevents me from getting ripped off by drivers." +
                                    "Kindly download EasyTransport from the play store");
                    startActivity(Intent.createChooser(sharingIntent, "Share via"));
                    break;
                default:
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {

        boolean drawerOpen = Drawer.isDrawerOpen(mDrawerListView);
        if (drawerOpen) {
            Drawer.closeDrawer(mDrawerListView);
        } else {
            super.onBackPressed();
        }
    }

}
