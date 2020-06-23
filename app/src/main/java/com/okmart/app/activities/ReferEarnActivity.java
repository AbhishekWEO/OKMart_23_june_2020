package com.okmart.app.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.okmart.app.R;
import com.okmart.app.utilities.DoubleDecimal;

public class ReferEarnActivity extends AppCompatActivity {

    private ImageView img_back;
    private RelativeLayout referral_copy;
    private TextView referred_code, send_invites, earn, you_will_earn;
    private String txt_referred_code, txt_referral_url, txt_amount_for_new_user, txt_amt_for_refrence_user, txt_min_txn_amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer_earn);

        getSupportActionBar().hide();

        Bundle extras = getIntent().getExtras();
        if (!(extras == null)) {

            txt_referred_code = extras.getString("referred_code");
            txt_referral_url = extras.getString("referral_url");
            txt_amount_for_new_user = extras.getString("amount_for_new_user");
            txt_amt_for_refrence_user = extras.getString("amt_for_refrence_user");
            txt_min_txn_amount = extras.getString("min_txn_amount");

        }

        referred_code = findViewById(R.id.referred_code);
        referred_code.setText(txt_referred_code);

        earn = findViewById(R.id.earn);
        earn.setText("Earn " + DoubleDecimal.twoPointsComma(txt_amount_for_new_user) + " PT" );

        you_will_earn = findViewById(R.id.you_will_earn);
        you_will_earn.setText("You will earn " + DoubleDecimal.twoPointsComma(txt_amt_for_refrence_user) + " PT when they recharge their account with a minimum of " + DoubleDecimal.twoPointsComma(txt_min_txn_amount) + " PT");

        img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        referral_copy = findViewById(R.id.referral_copy);
        referral_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("text", txt_referred_code);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(ReferEarnActivity.this, "Code copied", Toast.LENGTH_SHORT).show();

            }
        });

        send_invites = findViewById(R.id.send_invites);
        send_invites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Insert Subject here");
                String app_url = "Hey, check out this app at " + txt_referral_url +  " We can now buy products are lowest price when you signup you will get " + DoubleDecimal.twoPointsComma(txt_amount_for_new_user) + " PT Credit free to use.";
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, app_url);
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });
    }
}