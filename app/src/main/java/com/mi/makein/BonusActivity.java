package com.mi.makein;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.mi.makein.adapter.BonusAdapter;
import com.mi.makein.helper.ApiConfig;
import com.mi.makein.helper.Constant;
import com.mi.makein.helper.Session;
import com.mi.makein.model.Bonus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BonusActivity extends AppCompatActivity {
    public static RecyclerView recyclerView;
    public static BonusAdapter bonusAdapter;
    Activity activity;
    Session session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bonus);
        activity = BonusActivity.this;
        recyclerView = findViewById(R.id.recyclerView);

        session = new Session(activity);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(linearLayoutManager);
        bonusList();
    }

    private void bonusList()
    {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.USER_ID, session.getData(Constant.ID));
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        ArrayList<Bonus> bonuses = new ArrayList<>();
                        bonusAdapter = new BonusAdapter(activity, bonuses);
                        JSONObject object = new JSONObject(response);
                        JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                        Gson g = new Gson();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            if (jsonObject1 != null) {
                                Bonus group = g.fromJson(jsonObject1.toString(), Bonus.class);
                                bonuses.add(group);
                            } else {
                                break;
                            }
                        }


                        recyclerView.setAdapter(bonusAdapter);
                    }
                    else {
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, activity, Constant.BONUS_LIST_URL, params, true);

    }
}