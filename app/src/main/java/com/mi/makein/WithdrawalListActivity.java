package com.mi.makein;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mi.makein.adapter.WithdrawalAdapter;
import com.mi.makein.helper.ApiConfig;
import com.mi.makein.helper.Constant;
import com.mi.makein.helper.Session;
import com.mi.makein.model.Withdrawal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WithdrawalListActivity extends AppCompatActivity {

    public static Activity activity;
    public static RecyclerView recyclerView;
    public static WithdrawalAdapter withdrawalAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    Session session;
    Button withdrawal_amt;
    ImageView nowithdrawal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal_list);
        recyclerView = findViewById(R.id.recyclerView);
        withdrawal_amt = findViewById(R.id.withdrawal_amt);
        nowithdrawal = findViewById(R.id.nowithdrawal);
        activity = WithdrawalListActivity.this;
        session = new Session(activity);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(linearLayoutManager);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                withdrawalList();
            }
        });
        withdrawal_amt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WithdrawalListActivity.this,WithdrawalActivity.class);
                startActivity(intent);
            }
        });
        withdrawalList();
    }

    private void withdrawalList()
    {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.USER_ID,session.getData(Constant.ID));
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        nowithdrawal.setVisibility(View.GONE);
                        JSONObject object = new JSONObject(response);
                        JSONArray jsonArray = object.getJSONArray(Constant.DATA);
                        Gson g = new Gson();
                        ArrayList<Withdrawal> withdrawals = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            if (jsonObject1 != null) {
                                Withdrawal group = g.fromJson(jsonObject1.toString(), Withdrawal.class);
                                withdrawals.add(group);
                            } else {
                                break;
                            }
                        }

                        withdrawalAdapter = new WithdrawalAdapter(activity, withdrawals);
                        recyclerView.setAdapter(withdrawalAdapter);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                    else {
                        nowithdrawal.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(activity, String.valueOf(e), Toast.LENGTH_SHORT).show();
                }
            }
        }, activity, Constant.WITHDRAWAL_LIST_URL, params, true);
    }
}