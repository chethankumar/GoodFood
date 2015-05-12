package com.jpl.goodfood;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.fabric.sdk.android.Fabric;
import me.alexrs.prefs.lib.Prefs;
import timber.log.Timber;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;

import org.apache.http.auth.AUTH;

import java.sql.Time;

public class MainActivity extends Activity implements Constants {

    @InjectView(R.id.login_title)
    TextView loginTitle;

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "SEWUHuWtwlFU5NuSEtRMqL5zn";
    private static final String TWITTER_SECRET = "6fyn83xVE3u6YLhbkdHbwOsyoztynPiLbS5pkqjRU6Qz9xTbzC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        Timber.plant(new Timber.DebugTree());

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        final DigitsAuthButton digitsButton = (DigitsAuthButton) findViewById(R.id.auth_button);
        digitsButton.setCallback(new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {
                // Do something with the session and phone number
                //storing the phone number into local store and use it as ID
                Log.d("login", "success" + phoneNumber);
                Prefs.with(getApplicationContext()).save(AUTHORIZED, true);
                if (phoneNumber != null) {
                    Prefs.with(getApplicationContext()).save(PHONE_NUMBER, phoneNumber);
                }else{
                    //if not phone number.. re auth
                }
                MainActivity.this.finish();
                Intent menuIntent = new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(menuIntent);

            }

            @Override
            public void failure(DigitsException exception) {
                // Do something on failure
                Log.d("login", "failure" + exception.getMessage());
            }
        });

        loginTitle.setTypeface(Utils.getLatoRegularTypeface(getApplicationContext()));

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (Prefs.with(getApplicationContext()).getBoolean(AUTHORIZED, false)) {
                    //Authorized. skip login screen and move to next Activity
                    MainActivity.this.finish();
                    Intent menuIntent = new Intent(getApplicationContext(), MenuActivity.class);
                    startActivity(menuIntent);

                    Timber.d("Already authorized");
//            ParseObject order = new ParseObject(ORDER);
//            ParseObject menuItem = new ParseObject(MENU_ITEM);
//
//            menuItem.put(MENU_NAME, "Full Meals");
//            menuItem.put(MENU_DETAIL, "2Roti + Rice + 2curry + papad");
//            menuItem.put(MENU_PRICE, 50);
//            menuItem.put(MENU_IMG_SRC, "img_ on_dropbox");
//
//            menuItem.saveInBackground(new SaveCallback() {
//                @Override
//                public void done(ParseException e) {
//                    if (e == null) {
//                        //success
//                        Timber.d("saved one menu item");
//                    } else {
//                        Timber.d(e.getMessage());
//                    }
//                }
//            });
//
                } else {
                    digitsButton.setVisibility(View.VISIBLE);
                }
            }
        }, 2000);


    }

}
