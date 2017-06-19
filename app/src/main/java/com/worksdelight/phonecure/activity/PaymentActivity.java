package com.worksdelight.phonecure.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.stripe.android.view.CardInputWidget;
import com.worksdelight.phonecure.Global;
import com.worksdelight.phonecure.R;
import com.worksdelight.phonecure.module.DependencyHandler;


public class PaymentActivity extends AppCompatActivity {

    private DependencyHandler mDependencyHandler;
    ImageView back;
    Global global;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_activity);
        global = (Global) getApplicationContext();
        mDependencyHandler = new DependencyHandler(
                this,
                (CardInputWidget) findViewById(R.id.card_input_widget),
                (ListView) findViewById(R.id.listview));

        Button saveButton = (Button) findViewById(R.id.save);
        mDependencyHandler.attachAsyncTaskTokenController(this, saveButton);

      /*  Button saveRxButton = (Button) findViewById(R.id.saverx);
        mDependencyHandler.attachRxTokenController(saveRxButton);

        Button saveIntentServiceButton = (Button) findViewById(R.id.saveWithService);
        mDependencyHandler.attachIntentServiceTokenController(this, saveIntentServiceButton);*/
    }

    @Override
    public void onBackPressed() {
        global.setBackType("1");
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDependencyHandler.clearReferences();
    }
}
