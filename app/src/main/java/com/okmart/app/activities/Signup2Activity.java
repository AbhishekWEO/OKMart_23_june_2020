package com.okmart.app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.okmart.app.R;
import com.okmart.app.utilities.DialogBoxError;

public class Signup2Activity extends AppCompatActivity {

    private EditText email;
    private String txt_phonenumber, txt_email;
    private ImageView img_back;
    private TextView next;
    private Context context = Signup2Activity.this;
    private CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);

        getSupportActionBar().hide();

        Bundle extras = getIntent().getExtras();
        if(!(extras == null)) {

            txt_phonenumber = getIntent().getStringExtra("phonenumber");

        }

        email = findViewById(R.id.email);
        img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        next = findViewById(R.id.next);

        cardView = findViewById(R.id.cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                txt_email = email.getText().toString();

                String emailPattern = "[a-zA-Z0-9._-]+@[a-z0-9]+\\.+[a-z.]+";

                if (TextUtils.isEmpty(txt_email)) {
                    DialogBoxError.showError(context, "Please enter email");
                    return;
                } else if (!(txt_email.matches(emailPattern))) {
                    DialogBoxError.showError(context, "Please enter correct email");
                    return;
                }

                Intent intent = new Intent(Signup2Activity.this, Signup3Activity.class);
                intent.putExtra("phonenumber", txt_phonenumber);
                intent.putExtra("email", txt_email);
                startActivity(intent);

            }
        });

    }
}
