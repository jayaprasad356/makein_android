package com.lsa.makein;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.lsa.makein.fragment.CallUsFragment;
import com.lsa.makein.fragment.HomeFragment;
import com.lsa.makein.fragment.ProfileFragment;
import com.lsa.makein.fragment.ShopFragment;
import com.lsa.makein.fragment.TeamFragment;
import com.lsa.makein.helper.ApiConfig;
import com.lsa.makein.helper.Constant;
import com.lsa.makein.helper.Session;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {
    HomeFragment homeFragment;
    ShopFragment shopFragment;
    TeamFragment teamFragment;
    CallUsFragment callUsFragment;
    ProfileFragment profileFragment;
    BottomNavigationView bottomNavigationView;
    Session session;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        session = new Session(MainActivity.this);
        activity = MainActivity.this;
        homeFragment = new HomeFragment();
        shopFragment = new ShopFragment();
        teamFragment = new TeamFragment();
        callUsFragment = new CallUsFragment();
        profileFragment = new ProfileFragment();
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment,"HOME").commit();

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            session.setData(Constant.FCM_ID, token);
            Log.d(Constant.FCM_ID,token);
            Register_FCM(token);
        });



    }
    public void Register_FCM(String token) {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.USER_ID, session.getData(Constant.ID));
        params.put(Constant.FCM_ID, token);

        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        session.setData(Constant.FCM_ID, token);
                    }
                } catch (JSONException ignored) {

                }

            }
        }, activity, Constant.UPDATE_FCM, params, false);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                getUserDetails();
                getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment,"HOME").commit();
                return true;

            case R.id.nav_shop:
                getUserDetails();
                getSupportFragmentManager().beginTransaction().replace(R.id.container,shopFragment,"SHOP" ).commit();
                return true;
            case R.id.nav_team:
                getUserDetails();
                getSupportFragmentManager().beginTransaction().replace(R.id.container,teamFragment,"TEAM" ).commit();
                return true;
            case R.id.nav_callus:
                getUserDetails();
                getSupportFragmentManager().beginTransaction().replace(R.id.container,callUsFragment,"CALLUS" ).commit();
                return true;
            case R.id.nav_profile:
                getUserDetails();
                getSupportFragmentManager().beginTransaction().replace(R.id.container,profileFragment,"PROFILE" ).commit();
                return true;
        }

        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        getUserDetails();

    }

    private void getDailyIncome() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.USER_ID,session.getData(Constant.ID));
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);

                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        session.setData(Constant.BALANCE,jsonArray.getJSONObject(0).getString(Constant.BALANCE));
                    }
                    else {
                        Log.d("MAINACTIVITY",jsonObject.getString(Constant.MESSAGE));

                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }

        }, activity, Constant.DAILY_INCOME_URL, params,true);

    }

    private void getUserDetails()
    {
        getDailyIncome();
        Map<String, String> params = new HashMap<>();
        params.put(Constant.USER_ID,session.getData(Constant.ID));
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);

                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        session.setData(Constant.BALANCE,jsonArray.getJSONObject(0).getString(Constant.BALANCE));
                        session.setData(Constant.EARN,jsonArray.getJSONObject(0).getString(Constant.EARN));
                        session.setData(Constant.SPIN_COUNT,jsonArray.getJSONObject(0).getString(Constant.SPIN_COUNT));


                    }
                    else {
                        Log.d("MAINACTIVITY",jsonObject.getString(Constant.MESSAGE));

                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }

        }, activity, Constant.USER_DETAILS_URL, params,true);

    }
}

