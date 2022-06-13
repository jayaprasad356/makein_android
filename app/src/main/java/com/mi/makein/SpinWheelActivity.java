package com.mi.makein;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.bluehomestudio.luckywheel.LuckyWheel;
import com.bluehomestudio.luckywheel.OnLuckyWheelReachTheTarget;
import com.bluehomestudio.luckywheel.WheelItem;
import com.mi.makein.helper.ApiConfig;
import com.mi.makein.helper.Constant;
import com.mi.makein.helper.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SpinWheelActivity extends AppCompatActivity {
    ArrayList<WheelItem> wheelItems = new ArrayList<>();
    LuckyWheel lwv;
    Activity activity;
    int reward = 0;
    Session session;
    TextView chances;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spin_wheel);
        lwv = findViewById(R.id.lwv);
        chances = findViewById(R.id.chances);
        activity = SpinWheelActivity.this;
        session = new Session(activity);

        chances.setText("Chances Left = "+session.getData(Constant.SPIN_COUNT));

        wheelItems.add(new WheelItem(Color.parseColor("#880E4F"),
                BitmapFactory.decodeResource(getResources(), R.drawable.spinicon),
                ""));
        wheelItems.add(new WheelItem(Color.parseColor("#01579B"),
                BitmapFactory.decodeResource(getResources(), R.drawable.spinicon),
                ""));

        wheelItems.add(new WheelItem(Color.parseColor("#006064"),
                BitmapFactory.decodeResource(getResources(), R.drawable.spinicon),
                ""));

        wheelItems.add(new WheelItem(Color.parseColor("#F57F17"),
                BitmapFactory.decodeResource(getResources(), R.drawable.spinicon)
                ,""));

        wheelItems.add(new WheelItem(Color.parseColor("#3E2723"),
                BitmapFactory.decodeResource(getResources(), R.drawable.spinicon),
                ""));

        lwv. addWheelItems(wheelItems);
        if (session.getData(Constant.SPIN_COUNT).equals("0")){
            lwv.setLuckyWheelReachTheTarget(new OnLuckyWheelReachTheTarget() {
                @Override
                public void onReachTarget() {
                    Toast.makeText(activity, "You have No Chances to Spin", Toast.LENGTH_SHORT).show();
                }
            });



        }else {
            Map<String, String> params = new HashMap<>();
            params.put(Constant.TYPE,Constant.TARGET);
            ApiConfig.RequestToVolley((result, response) -> {

                if (result) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);

                        if (jsonObject.getBoolean(Constant.SUCCESS)) {
                            wheelItems.clear();
                            wheelItems.add(new WheelItem(Color.parseColor("#880E4F"),
                                    BitmapFactory.decodeResource(getResources(), R.drawable.spinicon),
                                    jsonArray.getJSONObject(0).getString(Constant.SPIN1)));
                            wheelItems.add(new WheelItem(Color.parseColor("#01579B"),
                                    BitmapFactory.decodeResource(getResources(), R.drawable.spinicon),
                                    jsonArray.getJSONObject(0).getString(Constant.SPIN2)));

                            wheelItems.add(new WheelItem(Color.parseColor("#006064"),
                                    BitmapFactory.decodeResource(getResources(), R.drawable.spinicon),
                                    jsonArray.getJSONObject(0).getString(Constant.SPIN3)));

                            wheelItems.add(new WheelItem(Color.parseColor("#F57F17"),
                                    BitmapFactory.decodeResource(getResources(), R.drawable.spinicon)
                                    ,jsonArray.getJSONObject(0).getString(Constant.SPIN4)));

                            wheelItems.add(new WheelItem(Color.parseColor("#3E2723"),
                                    BitmapFactory.decodeResource(getResources(), R.drawable.spinicon),
                                    jsonArray.getJSONObject(0).getString(Constant.SPIN5)));
                            lwv. addWheelItems(wheelItems);
                            int spinvalue = jsonObject.getInt(Constant.SPIN);

                            lwv.setTarget(spinvalue);
                            if (spinvalue == 1){
                                reward = Integer.parseInt(jsonArray.getJSONObject(0).getString(Constant.SPIN1));
                            }
                            else if (spinvalue == 2){
                                reward = Integer.parseInt(jsonArray.getJSONObject(0).getString(Constant.SPIN2));
                            }
                            else if (spinvalue == 3){
                                reward = Integer.parseInt(jsonArray.getJSONObject(0).getString(Constant.SPIN2));
                            }
                            else if (spinvalue == 4){
                                reward = Integer.parseInt(jsonArray.getJSONObject(0).getString(Constant.SPIN4));
                            }
                            else if (spinvalue == 5){
                                reward = Integer.parseInt(jsonArray.getJSONObject(0).getString(Constant.SPIN5));
                            }
                            lwv.setLuckyWheelReachTheTarget(new OnLuckyWheelReachTheTarget() {
                                @Override
                                public void onReachTarget() {
                                    Map<String, String> params = new HashMap<>();
                                    params.put(Constant.TYPE,Constant.UPDATE_TARGET);
                                    params.put(Constant.SPIN_POSITION,""+spinvalue);
                                    params.put(Constant.USER_ID,session.getData(Constant.ID));
                                    params.put(Constant.REWARD,""+reward);
                                    ApiConfig.RequestToVolley((result, response) -> {
                                        if (result) {
                                            try {
                                                JSONObject jsonObject = new JSONObject(response);
                                                JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);


                                                if (jsonObject.getBoolean(Constant.SUCCESS)) {
                                                    session.setData(Constant.BALANCE,jsonArray.getJSONObject(0).getString(Constant.BALANCE));
                                                    session.setData(Constant.EARN,jsonArray.getJSONObject(0).getString(Constant.EARN));
                                                    session.setData(Constant.SPIN_COUNT,jsonArray.getJSONObject(0).getString(Constant.SPIN_COUNT));
                                                    Toast.makeText(SpinWheelActivity.this, "Reward Added in Your Earnings", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(activity,MainActivity.class);
                                                    startActivity(intent);
                                                    finish();

                                                }
                                                else {
                                                    Toast.makeText(SpinWheelActivity.this, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show();

                                                }
                                            } catch (JSONException e){
                                                e.printStackTrace();

                                            }
                                        }
                                        else {
                                            Toast.makeText(SpinWheelActivity.this, String.valueOf(response) +String.valueOf(result), Toast.LENGTH_SHORT).show();

                                        }
                                        //pass url
                                    }, activity, Constant.SPIN_COUNT_URL, params,true);



                                }
                            });

                        }
                        else {
                            Toast.makeText(this, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show();

                        }
                    } catch (JSONException e){
                        e.printStackTrace();

                    }
                }
                else {
                    Toast.makeText(this, String.valueOf(response) +String.valueOf(result), Toast.LENGTH_SHORT).show();

                }
                //pass url
            }, activity, Constant.SPIN_COUNT_URL, params,true);

        }




    }
}