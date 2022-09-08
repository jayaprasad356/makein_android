package com.mi.makein;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.mi.makein.helper.ApiConfig;
import com.mi.makein.helper.Constant;
import com.mi.makein.helper.Session;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.payumoney.core.PayUmoneyConstants;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.shreyaspatil.EasyUpiPayment.EasyUpiPayment;
import com.shreyaspatil.EasyUpiPayment.listener.PaymentStatusListener;
import com.shreyaspatil.EasyUpiPayment.model.TransactionDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class RechargeActivity extends AppCompatActivity implements PaytmPaymentTransactionCallback,PaymentStatusListener, PaymentResultListener {
    EditText etPay;
    Button paybtn;
    int amt;
    Activity activity;
    Session session;
    Chip razorpay,upi,paytm;
    TextView tvBalance;
    String UPI_ID = "";
    String RAZORPAY_KEY = "";
    String PAYTM_MERCHANT_ID = "";
    String RAZORPAY_PAYMENT_METHOD = "";
    String PAYTM_PAYMENT_METHOD = "";
    String PAYTM_MODE = "";
    public static String customerId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        session = new Session(RechargeActivity.this);
        etPay = findViewById(R.id.etPay);
        paybtn = findViewById(R.id.paybtn);
        razorpay = findViewById(R.id.razorpay);
        paytm = findViewById(R.id.paytm);
        upi = findViewById(R.id.upi);
        tvBalance = findViewById(R.id.tvBalance);
        activity = RechargeActivity.this;
        tvBalance.setText("My Balance Rs. "+session.getData(Constant.BALANCE));
        paybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etPay.getText().toString().equals("") || etPay.getText().toString().equals("0")){
                    Toast.makeText(RechargeActivity.this, "Enter Recharge Amount", Toast.LENGTH_SHORT).show();
                }
                else {
                    Map<String, String> params = new HashMap<>();
                    params.put(Constant.AMOUNT,etPay.getText().toString().trim());
                    ApiConfig.RequestToVolley((result, response) -> {
                        if (result) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.getBoolean(Constant.SUCCESS)) {
                                    if(razorpay.isChecked()){
                                       // launchPayUMoneyFlow(Double.parseDouble("50"));
                                        launchRayzorpay();

                                    }
                                    else if(upi.isChecked()){
                                        if (!UPI_ID.equals("")){

                                            try {
                                                Date c = Calendar.getInstance().getTime();
                                                SimpleDateFormat df = new SimpleDateFormat("ddMMyyyyHHmmss", Locale.getDefault());
                                                String transcId = df.format(c);
                                                makePayment(""+Double.parseDouble(etPay.getText().toString()), UPI_ID, session.getData(Constant.NAME), "recharge amount", transcId);


                                            }catch (Exception e){
                                                Log.d("PAYMENT_GATEWAY",e.getMessage());

                                            }

                                        }


                                    }
                                    else if (paytm.isChecked()){
                                        startPayTmPayment();

                                    }

                                }
                                else {
                                    Toast.makeText(activity, jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show();

                                }
                            } catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                        else {
                            Toast.makeText(activity, String.valueOf(response) +String.valueOf(result), Toast.LENGTH_SHORT).show();

                        }
                        //pass url
                    }, activity, Constant.CHECK_RECHARGE_URL, params,true);



                }
            }
        });
    }
    public void startPayTmPayment() {
        Map<String, String> params = new HashMap<>();

        params.put(Constant.ORDER_ID_, Constant.randomAlphaNumeric(20));
        params.put(Constant.CUST_ID, Constant.randomAlphaNumeric(10));
        params.put(Constant.TXN_AMOUNT, ApiConfig.StringFormat("" + Double.parseDouble(etPay.getText().toString().trim())));
        if (PAYTM_MODE.equals("sandbox")) {
            params.put(Constant.INDUSTRY_TYPE_ID, Constant.INDUSTRY_TYPE_ID_DEMO_VAL);
            params.put(Constant.CHANNEL_ID, Constant.MOBILE_APP_CHANNEL_ID_DEMO_VAL);
            params.put(Constant.WEBSITE, Constant.WEBSITE_DEMO_VAL);
        } else if (PAYTM_MODE.equals("production")) {
            params.put(Constant.INDUSTRY_TYPE_ID, Constant.INDUSTRY_TYPE_ID_LIVE_VAL);
            params.put(Constant.CHANNEL_ID, Constant.MOBILE_APP_CHANNEL_ID_LIVE_VAL);
            params.put(Constant.WEBSITE, Constant.WEBSITE_LIVE_VAL);
        }

//        System.out.println("====" + params.toString());
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject object = jsonObject.getJSONObject(Constant.DATA);
//                    System.out.println("=======res  " + response);

                    PaytmPGService Service = null;
                    if (PAYTM_MODE.equals("sandbox")) {
                        Service = PaytmPGService.getStagingService(Constant.PAYTM_ORDER_PROCESS_DEMO_VAL);
                    } else if (PAYTM_MODE.equals("production")) {
                        Service = PaytmPGService.getProductionService();
                    }

                    customerId = object.getString(Constant.CUST_ID);
                    //creating a hashmap and adding all the values required

                    HashMap<String, String> paramMap = new HashMap<>();
                    paramMap.put(Constant.MID, PAYTM_MERCHANT_ID);
                    paramMap.put(Constant.ORDER_ID_, jsonObject.getString("order id"));
                    paramMap.put(Constant.CUST_ID, object.getString(Constant.CUST_ID));
                    paramMap.put(Constant.TXN_AMOUNT, ApiConfig.StringFormat("" + Double.parseDouble(etPay.getText().toString().trim())));

                    if (PAYTM_MODE.equals("sandbox")) {
                        paramMap.put(Constant.INDUSTRY_TYPE_ID, Constant.INDUSTRY_TYPE_ID_DEMO_VAL);
                        paramMap.put(Constant.CHANNEL_ID, Constant.MOBILE_APP_CHANNEL_ID_DEMO_VAL);
                        paramMap.put(Constant.WEBSITE, Constant.WEBSITE_DEMO_VAL);
                    } else if (PAYTM_MODE.equals("production")) {
                        paramMap.put(Constant.INDUSTRY_TYPE_ID, Constant.INDUSTRY_TYPE_ID_LIVE_VAL);
                        paramMap.put(Constant.CHANNEL_ID, Constant.MOBILE_APP_CHANNEL_ID_LIVE_VAL);
                        paramMap.put(Constant.WEBSITE, Constant.WEBSITE_LIVE_VAL);
                    }

                    paramMap.put(Constant.CALLBACK_URL, object.getString(Constant.CALLBACK_URL));
                    paramMap.put(Constant.CHECKSUMHASH, jsonObject.getString("signature"));
                    Log.d("PAY_HASH", Arrays.asList(paramMap) + "");

                    //creating a paytm order object using the hashmap
                    PaytmOrder order = new PaytmOrder(paramMap);

                    //intializing the paytm service
                    Objects.requireNonNull(Service).initialize(order, null);

                    //finally starting the payment transaction
                    Service.startPaymentTransaction(activity, true, true, this);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, activity, Constant.GENERATE_PAYTM_CHECKSUM, params, false);



    }


    private void launchRayzorpay() {

        // on below line we are getting
        // amount that is entered by user.
        String samount = etPay.getText().toString();
        int amount = Math.round(Float.parseFloat(samount) * 100);
        // initialize Razorpay account.
        Checkout checkout = new Checkout();
        checkout.setKeyID(RAZORPAY_KEY);


        // initialize json object
        JSONObject object = new JSONObject();
        try {
            // to put name
            object.put("name", session.getData(Constant.NAME));

            // put description
            object.put("description", "App payment");

            // to set theme color
            object.put("theme.color", "");

            // put the currency
            object.put("currency", "INR");

            // put amount
            object.put("amount", amount);

            // put mobile number
            object.put("prefill.contact", session.getData(Constant.MOBILE));

            // put email
            object.put("prefill.email", "test@gmail.com");

            // open razorpay to checkout activity
            checkout.open(RechargeActivity.this, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    // Method to create hash
    public static String hashCal(String str) {
        byte[] hashseq = str.getBytes();
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
            algorithm.reset();
            algorithm.update(hashseq);
            byte messageDigest[] = algorithm.digest();
            for (byte aMessageDigest : messageDigest) {
                String hex = Integer.toHexString(0xFF & aMessageDigest);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException ignored) {
        }
        return hexString.toString();
    }
    @Override
    public void onPaymentSuccess(String s) {
        // this method is called on payment success.
        Toast.makeText(this, "Payment is successful : " + s, Toast.LENGTH_SHORT).show();
        rechargeAmount();
    }

    @Override
    public void onPaymentError(int i, String s) {
        // on payment failed.
        Log.d("PAYMENT",s);
       // Toast.makeText(this, "Payment Failed due to error : " + s, Toast.LENGTH_SHORT).show();
    }
    private PayUmoneySdkInitializer.PaymentParam calculateServerSideHashAndInitiatePayment1(final PayUmoneySdkInitializer.PaymentParam paymentParam) {

        StringBuilder stringBuilder = new StringBuilder();
        HashMap<String, String> params = paymentParam.getParams();
        stringBuilder.append(params.get(PayUmoneyConstants.KEY)).append("|");
        stringBuilder.append(params.get(PayUmoneyConstants.TXNID)).append("|");
        stringBuilder.append(params.get(PayUmoneyConstants.AMOUNT)).append("|");
        stringBuilder.append(params.get(PayUmoneyConstants.PRODUCT_INFO)).append("|");
        stringBuilder.append(params.get(PayUmoneyConstants.FIRSTNAME)).append("|");
        stringBuilder.append(params.get(PayUmoneyConstants.EMAIL)).append("|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF1)).append("|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF2)).append("|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF3)).append("|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF4)).append("|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF5)).append("||||||");

        stringBuilder.append(R.string.MerchantSalt);

        String hash = hashCal(stringBuilder.toString());
        paymentParam.setMerchantHash(hash);

        return paymentParam;
    }


    @Override
    protected void onStart() {
        super.onStart();
        Map<String, String> params = new HashMap<>();
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        UPI_ID = jsonArray.getJSONObject(0).getString(Constant.UPI_ID);
                        RAZORPAY_KEY = jsonArray.getJSONObject(0).getString(Constant.RAZORPAY_KEY);
                        PAYTM_MERCHANT_ID = jsonArray.getJSONObject(0).getString(Constant.PAYTM_MERCHANT_ID);
                        RAZORPAY_PAYMENT_METHOD = jsonArray.getJSONObject(0).getString(Constant.RAZORPAY_PAYMENT_METHOD);
                        PAYTM_PAYMENT_METHOD = jsonArray.getJSONObject(0).getString(Constant.PAYTM_PAYMENT_METHOD);
                        PAYTM_MODE = jsonArray.getJSONObject(0).getString(Constant.PAYTM_MODE);
                        if (RAZORPAY_PAYMENT_METHOD.equals("1") && !RAZORPAY_KEY.equals("")){
                            razorpay.setVisibility(View.VISIBLE);

                        }
                        if (PAYTM_PAYMENT_METHOD.equals("1") && !PAYTM_MERCHANT_ID.equals("")){
                            paytm.setVisibility(View.VISIBLE);

                        }

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
        }, activity, Constant.EARN_SETTINGS_URL, params,true);

    }

    private void rechargeAmount()
    {
        String type = "";
        if (razorpay.isChecked()){
            type = "razorpay";

        }
        else if (paytm.isChecked()){
            type = "paytm";

        }
        else {
            type = "upi";
        }
        Map<String, String> params = new HashMap<>();
        params.put(Constant.USER_ID,session.getData(Constant.ID));
        params.put(Constant.AMOUNT,etPay.getText().toString());
        params.put(Constant.TYPE,type);
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        Intent intent = new Intent(activity, MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(this,jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show();
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
        }, activity, Constant.RECHARGE_URL, params,true);
    }

    @Override
    public void onTransactionCompleted(TransactionDetails transactionDetails) {


    }

    @Override
    public void onTransactionSuccess() {
        rechargeAmount();
        Toast.makeText(activity, "Transaction Success", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onTransactionSubmitted() {

    }

    @Override
    public void onTransactionFailed() {

        Toast.makeText(activity, "Transaction Failed", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onTransactionCancelled() {
        Toast.makeText(activity, "Transaction Cancelled", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onAppNotFound() {

    }
    private void makePayment(String amount, String upi, String name, String desc, String transactionId) {
        // on below line we are calling an easy payment method and passing
        // all parameters to it such as upi id,name, description and others.
        final EasyUpiPayment easyUpiPayment = new EasyUpiPayment.Builder()
                .with(this)
                // on below line we are adding upi id.
                .setPayeeVpa(upi)
                // on below line we are setting name to which we are making oayment.
                .setPayeeName(name)
                // on below line we are passing transaction id.
                .setTransactionId(transactionId)
                // on below line we are passing transaction ref id.
                .setTransactionRefId(transactionId)
                // on below line we are adding description to payment.
                .setDescription(desc)
                // on below line we are passing amount which is being paid.
                .setAmount(amount)
                // on below line we are calling a build method to build this ui.
                .build();
        // on below line we are calling a start
        // payment method to start a payment.
        easyUpiPayment.startPayment();

        // on below line we are calling a set payment
        // status listener method to call other payment methods.
        easyUpiPayment.setPaymentStatusListener(this);
    }

    @Override
    public void onTransactionResponse(Bundle bundle) {
        String orderId = bundle.getString(Constant.ORDERID);
        String status = bundle.getString(Constant.STATUS_);
        if (status.equalsIgnoreCase(Constant.TXN_SUCCESS)) {
            verifyTransaction(orderId);
        } else {
            Toast.makeText(activity, "Transaction Failed", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void networkNotAvailable() {

    }

    @Override
    public void clientAuthenticationFailed(String inErrorMessage) {

    }

    @Override
    public void someUIErrorOccurred(String inErrorMessage) {

    }

    @Override
    public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {

    }

    @Override
    public void onBackPressedCancelTransaction() {

    }

    @Override
    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {

    }
    public void verifyTransaction(String orderId) {
        Map<String, String> params = new HashMap<>();
        params.put("orderId", orderId);
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getJSONObject("body").getJSONObject("resultInfo").getString("resultStatus");
                    if (status.equalsIgnoreCase("TXN_SUCCESS")) {
                        String txnId = jsonObject.getJSONObject("body").getString("txnId");
                        rechargeAmount();
                        //PlaceOrder(activity, getString(R.string.paytm), txnId, true, sendParams, Constant.SUCCESS);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }, activity, Constant.VALID_TRANSACTION, params, false);
    }

}