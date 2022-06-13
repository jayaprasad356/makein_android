package com.mi.makein;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.mi.makein.helper.ApiConfig;
import com.mi.makein.helper.Constant;
import com.mi.makein.helper.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {
    Handler handler;
    Session session;
    Activity activity;
    String sharelink = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        activity = SplashActivity.this;
        session = new Session(activity);
        handler = new Handler();
        checkVersion();







    }
    private void checkVersion() {
        Map<String, String> params = new HashMap<>();
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);


                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                        sharelink = jsonArray.getJSONObject(0).getString(Constant.SHARE_LINK);
                        String latestversion = jsonArray.getJSONObject(0).getString(Constant.VERSION);
                        String currentversion = String.valueOf(BuildConfig.VERSION_CODE);
                        Log.d("SPLASH_RES","lat "+latestversion + "curent"+currentversion);
                        if (currentversion.equals(latestversion)){
                            GotoActivity();

                        }else {
                            updateAlertDialog();
                        }
                    }
                    else {
                        Log.d("MAINACTIVITY",jsonObject.getString(Constant.MESSAGE));

                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }

        }, activity, Constant.EARN_SETTINGS_URL, params,true);

    }

    private void updateAlertDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Yep! Here you can see a new update \uD83D\uDC3B\n");
        builder.setMessage(
                "We are working hard for your better experience \uD83E\uDD29\n" +
                        "\n" +
                        "So, Update it and enter in a new and updated version \uD83D\uDE0E\n" +
                        "\n");
        builder.setCancelable(false);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(sharelink)));

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
                finish();
            }
        });
        builder.show();
    }

    private void GotoActivity()
    {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (session.getBoolean("is_logged_in")){
                    Intent intent = new Intent(activity,MainActivity.class);
                    startActivity(intent);
                    finish();

                }
                else {
                    Intent intent = new Intent(activity,LoginActivity.class);
                    startActivity(intent);
                    finish();

                }




            }
        },2000);

    }

}