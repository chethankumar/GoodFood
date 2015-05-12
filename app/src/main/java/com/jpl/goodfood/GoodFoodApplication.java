package com.jpl.goodfood;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by chethan on 03/04/15.
 */
public class GoodFoodApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(getApplicationContext(),"NCHhK66ORPGRwpYO2ZAAPByvmGkWvJlcsyxCXYZf","CHrHyY00KBHkNMcc2vxn1szj8nwUW2hp1kSeGQ3U");

    }
}
