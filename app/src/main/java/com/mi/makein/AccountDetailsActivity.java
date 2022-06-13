package com.mi.makein;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.mi.makein.helper.ApiConfig;
import com.mi.makein.helper.Constant;
import com.mi.makein.helper.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AccountDetailsActivity extends AppCompatActivity {
    EditText etHolderName,etAccountNo,etIfsc,etBankname;
    Button btnUpdate;
    ImageButton back;
    Activity activity;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);
        activity = AccountDetailsActivity.this;
        session = new Session(activity);
        etHolderName = findViewById(R.id.etHolderName);
        etAccountNo = findViewById(R.id.etAccountNo);
        etIfsc = findViewById(R.id.etIfsc);
        etBankname = findViewById(R.id.etBankname);
        btnUpdate = findViewById(R.id.btnUpdate);
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        etHolderName.setText(session.getData(Constant.HOLDER_NAME));
        etAccountNo.setText(session.getData(Constant.ACCOUNT_NO));
        etIfsc.setText(session.getData(Constant.IFSC_CODE));
        etBankname.setText(session.getData(Constant.BANK_NAME));
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etHolderName.getText().toString().trim().equals("")){
                    etHolderName.setError("Enter Holder Name");
                    etHolderName.requestFocus();
                }
                else if (etAccountNo.getText().toString().trim().equals("")){
                    etAccountNo.setError("Enter Account No.");
                    etAccountNo.requestFocus();
                }
                else if (etIfsc.getText().toString().trim().equals("")){
                    etIfsc.setError("Enter IFSC Code");
                    etIfsc.requestFocus();
                }
                else if (etBankname.getText().toString().trim().equals("")){
                    etBankname.setError("Enter Bank Name");
                    etBankname.requestFocus();
                }
                else {
                    updateAccountDetails();
                }

            }
        });


    }

    private void updateAccountDetails()
    {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.HOLDER_NAME,etHolderName.getText().toString().trim());
        params.put(Constant.ACCOUNT_NO,etAccountNo.getText().toString().trim());
        params.put(Constant.IFSC_CODE,etIfsc.getText().toString().trim());
        params.put(Constant.BANK_NAME,etBankname.getText().toString().trim());
        params.put(Constant.USER_ID,session.getData(Constant.ID));
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);

                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        session.setData(Constant.ID,jsonArray.getJSONObject(0).getString(Constant.ID));
                        session.setData(Constant.NAME,jsonArray.getJSONObject(0).getString(Constant.NAME));
                        session.setData(Constant.MY_REFER_CODE,jsonArray.getJSONObject(0).getString(Constant.MY_REFER_CODE));
                        session.setData(Constant.EARN,jsonArray.getJSONObject(0).getString(Constant.EARN));
                        session.setData(Constant.HOLDER_NAME,jsonArray.getJSONObject(0).getString(Constant.HOLDER_NAME));
                        session.setData(Constant.ACCOUNT_NO,jsonArray.getJSONObject(0).getString(Constant.ACCOUNT_NO));
                        session.setData(Constant.IFSC_CODE,jsonArray.getJSONObject(0).getString(Constant.IFSC_CODE));
                        session.setData(Constant.BANK_NAME,jsonArray.getJSONObject(0).getString(Constant.BANK_NAME));
                        Toast.makeText(activity, "Updated", Toast.LENGTH_SHORT).show();
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
        }, activity, Constant.UPDATE_ACCOUNT_URL, params,true);



    }
}