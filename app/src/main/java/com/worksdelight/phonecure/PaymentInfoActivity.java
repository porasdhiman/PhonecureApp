package com.worksdelight.phonecure;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Button;

import com.loopj.android.http.AsyncHttpClient;

/*import com.braintreepayments.api.BraintreePaymentActivity;
import com.braintreepayments.api.PaymentRequest;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;*/


/**
 * Created by worksdelight on 22/03/17.
 */

public class PaymentInfoActivity extends Activity {
    private static final String SERVER_BASE = "http://worksdelight.com"; // Replace with your own server
    private static final int REQUEST_CODE = Menu.FIRST;
    private AsyncHttpClient client = new AsyncHttpClient();
    private String clientToken;
Button btn_start;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_info_layout);
        /*getToken();
        btn_start=(Button)findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PaymentRequest paymentRequest = new PaymentRequest()
                        .clientToken("eyJ2ZXJzaW9uIjoyLCJhdXRob3JpemF0aW9uRmluZ2VycHJpbnQiOiIzNDliMmMzMTYyODI1NDEwZGFjZWZiNDlmNzY5YmQ1YmZiZTk5YTA5Y2M0MzJlMDRhOWMzOWMxMmJkNjZjYTJmfGNyZWF0ZWRfYXQ9MjAxNy0wMy0yM1QwNTowOTo1MC43ODc0ODMyMDIrMDAwMFx1MDAyNm1lcmNoYW50X2lkPTM0OHBrOWNnZjNiZ3l3MmJcdTAwMjZwdWJsaWNfa2V5PTJuMjQ3ZHY4OWJxOXZtcHIiLCJjb25maWdVcmwiOiJodHRwczovL2FwaS5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tOjQ0My9tZXJjaGFudHMvMzQ4cGs5Y2dmM2JneXcyYi9jbGllbnRfYXBpL3YxL2NvbmZpZ3VyYXRpb24iLCJjaGFsbGVuZ2VzIjpbXSwiZW52aXJvbm1lbnQiOiJzYW5kYm94IiwiY2xpZW50QXBpVXJsIjoiaHR0cHM6Ly9hcGkuc2FuZGJveC5icmFpbnRyZWVnYXRld2F5LmNvbTo0NDMvbWVyY2hhbnRzLzM0OHBrOWNnZjNiZ3l3MmIvY2xpZW50X2FwaSIsImFzc2V0c1VybCI6Imh0dHBzOi8vYXNzZXRzLmJyYWludHJlZWdhdGV3YXkuY29tIiwiYXV0aFVybCI6Imh0dHBzOi8vYXV0aC52ZW5tby5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tIiwiYW5hbHl0aWNzIjp7InVybCI6Imh0dHBzOi8vY2xpZW50LWFuYWx5dGljcy5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tLzM0OHBrOWNnZjNiZ3l3MmIifSwidGhyZWVEU2VjdXJlRW5hYmxlZCI6dHJ1ZSwicGF5cGFsRW5hYmxlZCI6dHJ1ZSwicGF5cGFsIjp7ImRpc3BsYXlOYW1lIjoiQWNtZSBXaWRnZXRzLCBMdGQuIChTYW5kYm94KSIsImNsaWVudElkIjpudWxsLCJwcml2YWN5VXJsIjoiaHR0cDovL2V4YW1wbGUuY29tL3BwIiwidXNlckFncmVlbWVudFVybCI6Imh0dHA6Ly9leGFtcGxlLmNvbS90b3MiLCJiYXNlVXJsIjoiaHR0cHM6Ly9hc3NldHMuYnJhaW50cmVlZ2F0ZXdheS5jb20iLCJhc3NldHNVcmwiOiJodHRwczovL2NoZWNrb3V0LnBheXBhbC5jb20iLCJkaXJlY3RCYXNlVXJsIjpudWxsLCJhbGxvd0h0dHAiOnRydWUsImVudmlyb25tZW50Tm9OZXR3b3JrIjp0cnVlLCJlbnZpcm9ubWVudCI6Im9mZmxpbmUiLCJ1bnZldHRlZE1lcmNoYW50IjpmYWxzZSwiYnJhaW50cmVlQ2xpZW50SWQiOiJtYXN0ZXJjbGllbnQzIiwiYmlsbGluZ0FncmVlbWVudHNFbmFibGVkIjp0cnVlLCJtZXJjaGFudEFjY291bnRJZCI6ImFjbWV3aWRnZXRzbHRkc2FuZGJveCIsImN1cnJlbmN5SXNvQ29kZSI6IlVTRCJ9LCJjb2luYmFzZUVuYWJsZWQiOmZhbHNlLCJtZXJjaGFudElkIjoiMzQ4cGs5Y2dmM2JneXcyYiIsInZlbm1vIjoib2ZmIn0=")
                        .amount("$10.00")
                        .primaryDescription("Awesome payment")
                        .secondaryDescription("Using the Client SDK")
                        .submitButtonText("Pay");

                startActivityForResult(paymentRequest.getIntent(PaymentInfoActivity.this), REQUEST_CODE);
            }
        });*/

    }



   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == BraintreePaymentActivity.RESULT_OK) {
            PaymentMethodNonce paymentMethodNonce = data.getParcelableExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE);

            RequestParams requestParams = new RequestParams();
            requestParams.put("payment_method_nonce", paymentMethodNonce.getNonce());
            requestParams.put("amount", "10.00");

            client.post(SERVER_BASE + "/payment", requestParams, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                   // Toast.makeText(PaymentInfoActivity.this, responseString, Toast.LENGTH_LONG).show();
                    Log.e("respnse string",responseString);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                   // Toast.makeText(PaymentInfoActivity.this, responseString, Toast.LENGTH_LONG).show();
                    Log.e("respnse string",responseString);
                }
            });
        }
    }

    private void getToken() {
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
    }*/
}