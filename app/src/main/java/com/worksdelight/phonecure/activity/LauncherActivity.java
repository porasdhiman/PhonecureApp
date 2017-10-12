package com.worksdelight.phonecure.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.wallet.Cart;
import com.stripe.wrap.pay.AndroidPayConfiguration;
import com.stripe.wrap.pay.activity.StripeAndroidPayActivity;
import com.stripe.wrap.pay.utils.CartContentException;
import com.stripe.wrap.pay.utils.CartManager;
import com.worksdelight.phonecure.Global;
import com.worksdelight.phonecure.R;


public class LauncherActivity extends Activity {

    /*
     * Change this to your publishable key.
     *
     * You can get your key here: https://dashboard.stripe.com/account/apikeys
     */
    //private static final String FUNCTIONAL_SOURCE_PUBLISHABLE_KEY = "pk_live_C1cXigL187bmBE5E4y8Y1gX3";
    private static final String FUNCTIONAL_SOURCE_PUBLISHABLE_KEY ="pk_test_lWrpqiQI2kjjP6eNNGzLzJNX";
    Global global;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        global = (Global) getApplicationContext();
        Button tokenButton = (Button) findViewById(R.id.btn_make_card_tokens);
        tokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                global.setBackType("0");
                Intent intent = new Intent(LauncherActivity.this, PaymentActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        Button sourceButton = (Button) findViewById(R.id.btn_make_sources);
        sourceButton.setVisibility(View.GONE);
        /*sourceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                global.setBackType("0");
                Intent intent = new Intent(LauncherActivity.this, PollingActivity.class);
                startActivityForResult(intent, 0);
            }
        });*/

        Button androidPayButton = (Button) findViewById(R.id.btn_android_pay_launch);
        androidPayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSampleCartAndLaunchAndroidPayActivity();
            }
        });
    }

    @Override
    public void onBackPressed() {
        global.setBackType("1");
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (global.getBackType().equalsIgnoreCase("0")) {
                finish();
            }
            /*if (resultCode == Activity.RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                // use the result to update your UI and send the payment method nonce to your server
                payment_method_nonce = result.getPaymentMethodNonce().getNonce();

                second_count_img.setBackgroundResource(R.drawable.step_one_right_light);
                *//*ViewGroup.LayoutParams params1 = third_count_img.getLayoutParams();
                params1.height = 50;
                params1.height = 50;
                third_count_img.setLayoutParams(params1);*//*
                third_count_img.setBackgroundResource(R.drawable.step_one_right_light);
                second_line.setBackgroundResource(R.drawable.linew_white);

                dialogWindow();
                bookingMethod();
                // Toast.makeText(AlmostdoneActivity.this,result.getPaymentMethodNonce().getNonce(),Toast.LENGTH_SHORT).show();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // the user canceled
            } else {
                // handle errors here, an exception may be available in
                Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
            }*/
        }
    }

    private void createSampleCartAndLaunchAndroidPayActivity() {

        AndroidPayConfiguration.init(FUNCTIONAL_SOURCE_PUBLISHABLE_KEY, "USD");
        AndroidPayConfiguration androidPayConfiguration = AndroidPayConfiguration.getInstance();
        androidPayConfiguration.setShippingAddressRequired(true);
        CartManager cartManager = new CartManager("USD");
        cartManager.addLineItem("Llama Food", 5000L);
        cartManager.addLineItem("Llama Shoes", 4, 2000L);
        cartManager.addShippingLineItem("Domestic shipping estimate", 1000L);

        try {
            Cart cart = cartManager.buildCart();
            Intent intent = new Intent(this, AndroidPayActivity.class)
                    .putExtra(StripeAndroidPayActivity.EXTRA_CART, cart);
            startActivity(intent);
        } catch (CartContentException unexpected) {
            // Ignore for now.
        }
    }
}
