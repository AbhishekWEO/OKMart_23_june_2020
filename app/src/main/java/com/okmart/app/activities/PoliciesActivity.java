package com.okmart.app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.okmart.app.R;
import com.okmart.app.adapters.PoliciesAdapter;
import com.okmart.app.model.PoliciesModel;
import com.okmart.app.model.ProductModel;

import java.util.ArrayList;
import java.util.List;

public class PoliciesActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context = PoliciesActivity.this;
    private String TAG = PoliciesActivity.class.getSimpleName();
    private RelativeLayout tv_refund, tv_privacy_policy, tv_terms, tv_leagl;
    private Intent intent;
    private ImageView img_back;
    private ArrayList<PoliciesModel> policies=new ArrayList<PoliciesModel>();
    private RecyclerView rv_policies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policies);

        getSupportActionBar().hide();

        rv_policies = findViewById(R.id.rv_policies);
        rv_policies.setLayoutManager(new LinearLayoutManager(this));

        Bundle extras = getIntent().getExtras();
        if(!(extras == null)) {
            policies = (ArrayList<PoliciesModel>) getIntent().getSerializableExtra("arrayList");
        }

        rv_policies.setAdapter(new PoliciesAdapter(this, policies));

        intent = new Intent(context, WebViewActivity.class);

//        tv_refund = findViewById(R.id.tv_refund);
//        tv_refund.setOnClickListener(this);
//
//        tv_privacy_policy = findViewById(R.id.tv_privacy_policy);
//        tv_privacy_policy.setOnClickListener(this);
//
//        tv_terms = findViewById(R.id.tv_terms);
//        tv_terms.setOnClickListener(this);
//
//        tv_leagl = findViewById(R.id.tv_leagl);
//        tv_leagl.setOnClickListener(this);
//
        img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.img_back :

                finish();

                break;

            /*case R.id.tv_refund :

                intent.putExtra("type", "refund_policy");
                startActivity(intent);

                break;

            case R.id.tv_privacy_policy :

                intent.putExtra("type", "privacy_policy");
                startActivity(intent);

                break;

            case R.id.tv_terms :

                intent.putExtra("type", "terms_and_conditions");
                startActivity(intent);

                break;

            case R.id.tv_leagl :

                intent.putExtra("type", "legal");
                startActivity(intent);

                break;*/

            default:

                break;
        }
    }
}