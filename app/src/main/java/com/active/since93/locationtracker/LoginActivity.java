package com.active.since93.locationtracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.active.since93.locationtracker.constants.Constants;

/**
 * Created by myzupp on 18-03-2017.
 *
 * @author Darshan Parikh (parikhdarshan36@gmail.com)
 */

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences userPreferences;
    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = LoginActivity.this;
        userPreferences = getSharedPreferences(Constants.USER_DETAILS, MODE_PRIVATE);

        if(userPreferences.getBoolean(Constants.IS_USER_LOGGED_IN, false)) {
            goToMainActivity();
        }

        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToMainActivity();

                SharedPreferences.Editor editor = userPreferences.edit();
                editor.putBoolean(Constants.IS_USER_LOGGED_IN, true);
                editor.apply();
            }
        });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
