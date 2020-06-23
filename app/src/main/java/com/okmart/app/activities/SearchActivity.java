package com.okmart.app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.okmart.app.R;
import com.okmart.app.adapters.SearchAdapter;
import com.okmart.app.model.ProductModel;
import com.okmart.app.utilities.DialogBoxError;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView rv_search;
    private SearchAdapter searchAdapter;
    private EditText search_product;
    private TextView go_back;

    private List<ProductModel> productDataList;
    private List<ProductModel> productDataListFiltered = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getSupportActionBar().hide();

        go_back = findViewById(R.id.go_back);
        go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogBoxError.hideKeyboard(SearchActivity.this);
                finish();
            }
        });

        rv_search = findViewById(R.id.rv_search);
        rv_search.setLayoutManager(new LinearLayoutManager(this));

        search_product = findViewById(R.id.search_product);

        DialogBoxError.showKeyboard(SearchActivity.this, rv_search);

        search_product.setFocusable(true);
        search_product.setFocusableInTouchMode(true);
        search_product.requestFocus();

        productDataList = (ArrayList<ProductModel>) getIntent().getSerializableExtra("productDataList");
//        productDataListFiltered = (ArrayList<ProductModel>) getIntent().getSerializableExtra("productDataListFiltered");

        Log.e("Response", "Response");

        searchAdapter=new SearchAdapter(this, productDataList, productDataListFiltered);
        rv_search.setAdapter(searchAdapter);

        search_product.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
//                String text = search_product.getText().toString().toLowerCase(Locale.getDefault());
//                searchAdapter.filter(text);


            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                //productDataListFiltered.clear();

                List<ProductModel> tempList = new ArrayList<>();

                if(s.length() != 0)
                {
                    String text = search_product.getText().toString().toLowerCase(Locale.getDefault());

                    for (int i = 0; i < productDataList.size(); i++)
                    {
                        String a = productDataList.get(i).getProduct_name().toLowerCase();
                        Log.e("Response", a);
                        Log.e("Response","Response");

                        if(a.contains(text))
                        {
                            //productDataListFiltered.add(productDataList.get(i));
                            tempList.add(productDataList.get(i));

                        }

                    }
                    /*searchAdapter=new SearchAdapter(SearchActivity.this, productDataList, productDataListFiltered);
                    rv_search.setAdapter(searchAdapter);*/

                }
                searchAdapter.filter(tempList);
                /*else {
                    productDataListFiltered.clear();
                }*/
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        DialogBoxError.hideKeyboard(SearchActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        DialogBoxError.hideKeyboard(SearchActivity.this);
    }


}