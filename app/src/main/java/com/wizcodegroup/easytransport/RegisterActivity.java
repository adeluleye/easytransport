package com.wizcodegroup.easytransport;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class RegisterActivity extends ActionBarActivity {

    //String TITLES[] = {"Home","Take a Test","Study Key Points","Community","Chat","Tip of the day","Help","Logout"};
    //int ICONS[] = {R.drawable.home,R.drawable.test,R.drawable.test,R.drawable.community,R.drawable.chat,R.drawable.test,R.drawable.help,R.drawable.logout};

    //String NAME = "Abideen Adelu";
    //String EMAIL = "abideen.adelu@meltwater.org";
    //int PROFILE = R.drawable.picture;

    private Toolbar toolbar;

    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    DrawerLayout Drawer;                                  // Declaring DrawerLayout

    ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);

        //TextView mTxtTitle = (TextView) findViewById(R.id.txtTitle);
        //mTxtTitle.setText(getString(R.string.settings));

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
        // Assigning the RecyclerView Object to the xml View

        //mRecyclerView.setHasFixedSize(true);
        // Letting the system know that the list objects are of fixed size

        //mAdapter = new MyAdapter(TITLES,ICONS,NAME,EMAIL,PROFILE,this);
        // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture

        //mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView

        /*final GestureDetector mGestureDetector = new GestureDetector(RegisterActivity.this, new GestureDetector.SimpleOnGestureListener() {

            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });
*/
/*
        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(),motionEvent.getY());

                //if(child = recyclerView.getChildPosition(1)){}
                if(child!=null && mGestureDetector.onTouchEvent(motionEvent)){
                    Drawer.closeDrawers();
                    //onNavigationDrawerItemSelected();
                    //Intent mIntent = new Intent(MainActivity.this, RegisterActivity.class);
                    //startActivity(mIntent);
                    //Toast.makeText(MainActivity.this, "The Item Clicked is: " + recyclerView.getChildPosition(child), Toast.LENGTH_SHORT).show();
                    getSupportActionBar().setTitle("Home");

                    return true;

                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

            }
        });*/


        //mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager

        //mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager


        //Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view
        //mDrawerToggle = new ActionBarDrawerToggle(this,Drawer,toolbar,R.string.drawer_open,R.string.drawer_close){

            /*@Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(R.string.app_name);
                // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                // open I am not going to put anything here)
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                // Code here will execute once drawer is closed
            }



        }; // Drawer Toggle Object Made
        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
          //  return true;
        //}

        finish();
        return super.onOptionsItemSelected(item);
    }

    /*@Override
    public void onBackPressed() {

        boolean drawerOpen = Drawer.isDrawerOpen(mDrawerListView);
        if (drawerOpen) {
            Drawer.closeDrawer(mDrawerListView);
        } else {
            super.onBackPressed();
        }
    }*/
}
