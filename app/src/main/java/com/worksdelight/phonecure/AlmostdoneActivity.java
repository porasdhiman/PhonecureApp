package com.worksdelight.phonecure;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.braintreepayments.api.BraintreeFragment;
import com.loopj.android.http.AsyncHttpClient;

/**
 * Created by worksdelight on 08/03/17.
 */

public class AlmostdoneActivity extends Activity {
    private static final String SERVER_BASE = "http://worksdelight.com"; // Replace with your own server
    private static final int REQUEST_CODE = Menu.FIRST;
    private AsyncHttpClient client = new AsyncHttpClient();
    private String clientToken;
    TextView btn_start;
    EditText first_name_ed,last_name_ed,address_ed,city_ed,zip_ed,phone_ed;
    TextView total_txt;
    BraintreeFragment mBraintreeFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shipping_layout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }
       // getToken();
        first_name_ed=(EditText)findViewById(R.id.first_name_ed) ;
        last_name_ed=(EditText)findViewById(R.id.last_name_ed) ;
        address_ed=(EditText)findViewById(R.id.address_ed) ;
        city_ed=(EditText)findViewById(R.id.city_ed) ;
        zip_ed=(EditText)findViewById(R.id.zip_ed) ;
        phone_ed=(EditText)findViewById(R.id.phone_ed);
        btn_start=(TextView) findViewById(R.id.btn_start);
        /*try {
            mBraintreeFragment = BraintreeFragment.newInstance(this, "sandbox_g24f7nn3_yjyc9yf86snb9b5b");
            // mBraintreeFragment is ready to use!
        } catch (InvalidArgumentException e) {
            // There was an issue with your authorization string.
        }
        mBraintreeFragment.addListener(new PaymentMethodNonceCreatedListener() {
            @Override
            public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
                String nonce = paymentMethodNonce.getNonce();
            }
        });*/
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(first_name_ed.length()==0){
                    first_name_ed.setError("Please enter first name");
                }else  if(last_name_ed.length()==0){
                    last_name_ed.setError("Please enter last name");
                }else  if(address_ed.length()==0){
                    address_ed.setError("Please enter address");
                }
                else  if(city_ed.length()==0){
                    city_ed.setError("Please enter city");
                }else  if(zip_ed.length()==0){
                    zip_ed.setError("Please enter Zip");
                }else  if(phone_ed.length()==0){
                    phone_ed.setError("Please enter Phone number");
                }else  if(phone_ed.length()!=10){
                    phone_ed.setError("Please enter correct Phone number");
                }
                else {
                    Intent i=new Intent(AlmostdoneActivity.this,PaymentInfoActivity.class);
                    startActivity(i);
                  /*  DropInRequest dropInRequest = new DropInRequest()
                            .clientToken("eyJ2ZXJzaW9uIjoyLCJhdXRob3JpemF0aW9uRmluZ2VycHJpbnQiOiIyZTY4MzZhOTU5MTk1NTNjOWY5YzZiZGY0YWIwMWIxOGJhMmFmYWE5MTA4M2I5YTJhYTdhMWU4MTIyMjAyNmEwfGNyZWF0ZWRfYXQ9MjAxNy0wMy0yOFQwNDo1NzoyNi44MDgwNjM5NjArMDAwMFx1MDAyNm1lcmNoYW50X2lkPTM0OHBrOWNnZjNiZ3l3MmJcdTAwMjZwdWJsaWNfa2V5PTJuMjQ3ZHY4OWJxOXZtcHIiLCJjb25maWdVcmwiOiJodHRwczovL2FwaS5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tOjQ0My9tZXJjaGFudHMvMzQ4cGs5Y2dmM2JneXcyYi9jbGllbnRfYXBpL3YxL2NvbmZpZ3VyYXRpb24iLCJjaGFsbGVuZ2VzIjpbXSwiZW52aXJvbm1lbnQiOiJzYW5kYm94IiwiY2xpZW50QXBpVXJsIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5icmFpbnRyZWVnYXRld2F5LmNvbTo0NDMvbWVyY2hhbnRzLzM0OHBrOWNnZjNiZ3l3MmIvY2xpZW50X2FwaSIsImFzc2V0c1VybCI6Imh0dHBzOi8vYXNzZXRzLmJyYWludHJlZWdhdGV3YXkuY29tIiwiYXV0aFVybCI6Imh0dHBzOi8vYXV0aC52ZW5tby5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tIiwiYW5hbHl0aWNzIjp7InVybCI6Imh0dHBzOi8vY2xpZW50LWFuYWx5dGljcy5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tLzM0OHBrOWNnZjNiZ3l3MmIifSwidGhyZWVEU2VjdXJlRW5hYmxlZCI6dHJ1ZSwicGF5cGFsRW5hYmxlZCI6dHJ1ZSwicGF5cGFsIjp7ImRpc3BsYXlOYW1lIjoiQWNtZSBXaWRnZXRzLCBMdGQuIChTYW5kYm94KSIsImNsaWVudElkIjpudWxsLCJwcml2YWN5VXJsIjoiaHR0cDovL2V4YW1wbGUuY29tL3BwIiwidXNlckFncmVlbWVudFVybCI6Imh0dHA6Ly9leGFtcGxlLmNvbS90b3MiLCJiYXNlVXJsIjoiaHR0cHM6Ly9hc3NldHMuYnJhaW50cmVlZ2F0ZXdheS5jb20iLCJhc3NldHNVcmwiOiJodHRwczovL2NoZWNrb3V0LnBheXBhbC5jb20iLCJkaXJlY3RCYXNlVXJsIjpudWxsLCJhbGxvd0h0dHAiOnRydWUsImVudmlyb25tZW50Tm9OZXR3b3JrIjp0cnVlLCJlbnZpcm9ubWVudCI6Im9mZmxpbmUiLCJ1bnZldHRlZE1lcmNoYW50IjpmYWxzZSwiYnJhaW50cmVlQ2xpZW50SWQiOiJtYXN0ZXJjbGllbnQzIiwiYmlsbGluZ0FncmVlbWVudHNFbmFibGVkIjp0cnVlLCJtZXJjaGFudEFjY291bnRJZCI6ImFjbWV3aWRnZXRzbHRkc2FuZGJveCIsImN1cnJlbmN5SXNvQ29kZSI6IlVTRCJ9LCJjb2luYmFzZUVuYWJsZWQiOmZhbHNlLCJtZXJjaGFudElkIjoiMzQ4cGs5Y2dmM2JneXcyYiIsInZlbm1vIjoib2ZmIn0=");
                    startActivityForResult(dropInRequest.getIntent(AlmostdoneActivity.this), REQUEST_CODE);*//*
                    PaymentRequest paymentRequest = new PaymentRequest()
                            .tokenizationKey("sandbox_g24f7nn3_yjyc9yf86snb9b5b")
                            //.clientToken("eyJ2ZXJzaW9uIjoyLCJhdXRob3JpemF0aW9uRmluZ2VycHJpbnQiOiIzNDliMmMzMTYyODI1NDEwZGFjZWZiNDlmNzY5YmQ1YmZiZTk5YTA5Y2M0MzJlMDRhOWMzOWMxMmJkNjZjYTJmfGNyZWF0ZWRfYXQ9MjAxNy0wMy0yM1QwNTowOTo1MC43ODc0ODMyMDIrMDAwMFx1MDAyNm1lcmNoYW50X2lkPTM0OHBrOWNnZjNiZ3l3MmJcdTAwMjZwdWJsaWNfa2V5PTJuMjQ3ZHY4OWJxOXZtcHIiLCJjb25maWdVcmwiOiJodHRwczovL2FwaS5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tOjQ0My9tZXJjaGFudHMvMzQ4cGs5Y2dmM2JneXcyYi9jbGllbnRfYXBpL3YxL2NvbmZpZ3VyYXRpb24iLCJjaGFsbGVuZ2VzIjpbXSwiZW52aXJvbm1lbnQiOiJzYW5kYm94IiwiY2xpZW50QXBpVXJsIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5icmFpbnRyZWVnYXRld2F5LmNvbTo0NDMvbWVyY2hhbnRzLzM0OHBrOWNnZjNiZ3l3MmIvY2xpZW50X2FwaSIsImFzc2V0c1VybCI6Imh0dHBzOi8vYXNzZXRzLmJyYWludHJlZWdhdGV3YXkuY29tIiwiYXV0aFVybCI6Imh0dHBzOi8vYXV0aC52ZW5tby5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tIiwiYW5hbHl0aWNzIjp7InVybCI6Imh0dHBzOi8vY2xpZW50LWFuYWx5dGljcy5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tLzM0OHBrOWNnZjNiZ3l3MmIifSwidGhyZWVEU2VjdXJlRW5hYmxlZCI6dHJ1ZSwicGF5cGFsRW5hYmxlZCI6dHJ1ZSwicGF5cGFsIjp7ImRpc3BsYXlOYW1lIjoiQWNtZSBXaWRnZXRzLCBMdGQuIChTYW5kYm94KSIsImNsaWVudElkIjpudWxsLCJwcml2YWN5VXJsIjoiaHR0cDovL2V4YW1wbGUuY29tL3BwIiwidXNlckFncmVlbWVudFVybCI6Imh0dHA6Ly9leGFtcGxlLmNvbS90b3MiLCJiYXNlVXJsIjoiaHR0cHM6Ly9hc3NldHMuYnJhaW50cmVlZ2F0ZXdheS5jb20iLCJhc3NldHNVcmwiOiJodHRwczovL2NoZWNrb3V0LnBheXBhbC5jb20iLCJkaXJlY3RCYXNlVXJsIjpudWxsLCJhbGxvd0h0dHAiOnRydWUsImVudmlyb25tZW50Tm9OZXR3b3JrIjp0cnVlLCJlbnZpcm9ubWVudCI6Im9mZmxpbmUiLCJ1bnZldHRlZE1lcmNoYW50IjpmYWxzZSwiYnJhaW50cmVlQ2xpZW50SWQiOiJtYXN0ZXJjbGllbnQzIiwiYmlsbGluZ0FncmVlbWVudHNFbmFibGVkIjp0cnVlLCJtZXJjaGFudEFjY291bnRJZCI6ImFjbWV3aWRnZXRzbHRkc2FuZGJveCIsImN1cnJlbmN5SXNvQ29kZSI6IlVTRCJ9LCJjb2luYmFzZUVuYWJsZWQiOmZhbHNlLCJtZXJjaGFudElkIjoiMzQ4cGs5Y2dmM2JneXcyYiIsInZlbm1vIjoib2ZmIn0=")
                            .amount("$30.00")
                            .primaryDescription("Awesome payment")
                            .secondaryDescription("Using the Client SDK")
                            .submitButtonText("Pay");

                    startActivityForResult(paymentRequest.getIntent(AlmostdoneActivity.this), REQUEST_CODE);*/
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      /*  if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                // use the result to update your UI and send the payment method nonce to your server

                postNonceToServer(result.getPaymentMethodNonce().getNonce());
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // the user canceled
            } else {
                // handle errors here, an exception may be available in
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
            }
        }*/
        /*if(requestCode==REQUEST_CODE) {

            switch (resultCode) {
                case BraintreePaymentActivity.RESULT_OK:


                    PaymentMethodNonce paymentMethodNonce = data.getParcelableExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE);

                    RequestParams requestParams = new RequestParams();
                    requestParams.put("payment_method_nonce", paymentMethodNonce.getNonce());
                    requestParams.put("amount", "30.00");

                    client.post(SERVER_BASE + "/payment", requestParams, new TextHttpResponseHandler() {
                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            Toast.makeText(AlmostdoneActivity.this, "2", Toast.LENGTH_LONG).show();
                            Log.e("respnse string", responseString);
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, String responseString) {
                             Toast.makeText(AlmostdoneActivity.this, "1", Toast.LENGTH_LONG).show();
                            Log.e("respnse string", responseString);
                        }
                    });
                    break;
                case BraintreePaymentActivity.BRAINTREE_RESULT_DEVELOPER_ERROR:
                    Toast.makeText(AlmostdoneActivity.this,"developer error",Toast.LENGTH_SHORT).show();
                    break;
                case BraintreePaymentActivity.BRAINTREE_RESULT_SERVER_ERROR:
                    Toast.makeText(AlmostdoneActivity.this,"server error",Toast.LENGTH_SHORT).show();
                    break;
                case BraintreePaymentActivity.BRAINTREE_RESULT_SERVER_UNAVAILABLE:
                    Toast.makeText(AlmostdoneActivity.this,"server unavailable",Toast.LENGTH_SHORT).show();
                    // handle errors here, a throwable may be available in
                    // data.getSerializableExtra(BraintreePaymentActivity.EXTRA_ERROR_MESSAGE)
                    break;
                default:
                    break;
            }



        }*/
    }
   /* void postNonceToServer(String nonce) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("payment_method_nonce", nonce);
        client.post(SERVER_BASE + "/payment", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(AlmostdoneActivity.this, responseString, Toast.LENGTH_LONG).show();
                Log.e("respnse string", responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Toast.makeText(AlmostdoneActivity.this, "1", Toast.LENGTH_LONG).show();
                Log.e("respnse string", responseString);
            }
        });
    }*/
   /* private void getToken() {
        client.get(SERVER_BASE + "/token", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                // findViewById(R.id.btn_start).setEnabled(false);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                clientToken = responseString;
                //findViewById(R.id.btn_start).setEnabled(true);
            }
        });
    }
*/

}
