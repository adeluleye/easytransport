package com.wizcodegroup.easytransport;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;



/**
 * Created by eit on 2/5/15.
 */
public class SplashActivity2 extends Activity{


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Thread splashThread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (waited < 5000) {
                        sleep(100);
                        waited += 100;
                    }
                } catch (InterruptedException e) {
                    // do nothing
                } finally {

                    Intent i = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(i);
                    finish();

                }
            }
        };
        splashThread.start();
    }




}
