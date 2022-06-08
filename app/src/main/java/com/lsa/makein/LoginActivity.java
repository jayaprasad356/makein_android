package com.lsa.makein;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lsa.makein.helper.ApiConfig;
import com.lsa.makein.helper.Constant;
import com.lsa.makein.helper.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    TextView tvsignup;
    EditText etMobile;
    Button btnSendOtp;
    Activity activity;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        session = new Session(LoginActivity.this);

        tvsignup = findViewById(R.id.tvsignup);
        btnSendOtp = findViewById(R.id.btnSendOtp);
        etMobile = findViewById(R.id.etMobile);
        tvsignup.setPaintFlags(tvsignup.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        activity = LoginActivity.this;
        btnSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etMobile.getText().toString().trim().equals("")){
                    etMobile.setError("Mobile Number is Empty");
                    etMobile.requestFocus();
                }
                else {
                    session.setBoolean("is_logged_in", true);
//                    Intent intent = new Intent(activity,MainActivity.class);
//                    startActivity(intent);
//                    finish();
                    checkMobileNumber();

                }

            }
        });
        tvsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity,SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void checkMobileNumber()
    {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.MOBILE,etMobile.getText().toString().trim());
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);

                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        session.setData(Constant.ID,jsonArray.getJSONObject(0).getString(Constant.ID));
                        session.setData(Constant.NAME,jsonArray.getJSONObject(0).getString(Constant.NAME));
                        session.setData(Constant.MOBILE,jsonArray.getJSONObject(0).getString(Constant.MOBILE));
                        session.setData(Constant.MY_REFER_CODE,jsonArray.getJSONObject(0).getString(Constant.MY_REFER_CODE));
                        session.setData(Constant.EARN,jsonArray.getJSONObject(0).getString(Constant.EARN));
                        session.setData(Constant.BALANCE,jsonArray.getJSONObject(0).getString(Constant.BALANCE));
                        session.setData(Constant.HOLDER_NAME,jsonArray.getJSONObject(0).getString(Constant.HOLDER_NAME));
                        session.setData(Constant.ACCOUNT_NO,jsonArray.getJSONObject(0).getString(Constant.ACCOUNT_NO));
                        session.setData(Constant.IFSC_CODE,jsonArray.getJSONObject(0).getString(Constant.IFSC_CODE));
                        session.setData(Constant.BANK_NAME,jsonArray.getJSONObject(0).getString(Constant.BANK_NAME));;
                        session.setData(Constant.SPIN_COUNT,jsonArray.getJSONObject(0).getString(Constant.SPIN_COUNT));;
                        Intent intent = new Intent(activity,MainActivity.class);
                        intent.putExtra(Constant.MOBILE,etMobile.getText().toString().trim());
                        startActivity(intent);
                        finish();
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
        }, activity, Constant.CHECK_MOBILE_NUMBER_URL, params,true);

    }
}