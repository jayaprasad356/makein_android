package com.mi.makein;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mi.makein.helper.ApiConfig;
import com.mi.makein.helper.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    Button signup_btn;
    Activity activity;
    EditText etName,etMobile,etReferral,etHolderName,etAccountNo,etIfsc,etBankname;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        signup_btn = findViewById(R.id.signup_btn);
        etName = findViewById(R.id.etName);
        etMobile = findViewById(R.id.etMobile);
        etReferral = findViewById(R.id.etReferral);
        etHolderName = findViewById(R.id.etHolderName);
        etAccountNo = findViewById(R.id.etAccountNo);
        etIfsc = findViewById(R.id.etIfsc);
        etBankname = findViewById(R.id.etBankname);
        activity = SignUpActivity.this;

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etName.getText().toString().trim().equals("")){
                    etName.setError("Name is Empty");
                    etName.requestFocus();
                }
                else if (etMobile.getText().toString().trim().equals("")){
                    etMobile.setError("Mobile Number is Empty");
                    etMobile.requestFocus();
                }
                else if (etHolderName.getText().toString().trim().equals("")){
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
                    signUp();

                }
            }
        });
    }

    private void signUp()
    {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.MOBILE,etMobile.getText().toString().trim());
        params.put(Constant.NAME,etName.getText().toString().trim());
        params.put(Constant.REFERRAL,etReferral.getText().toString().trim());
        params.put(Constant.HOLDER_NAME,etHolderName.getText().toString().trim());
        params.put(Constant.ACCOUNT_NO,etAccountNo.getText().toString().trim());
        params.put(Constant.IFSC_CODE,etIfsc.getText().toString().trim());
        params.put(Constant.BANK_NAME,etBankname.getText().toString().trim());
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        Toast.makeText(this,jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(activity, LoginActivity.class));
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
        }, activity, Constant.SIGNUP_USER_URL, params,true);



    }

}