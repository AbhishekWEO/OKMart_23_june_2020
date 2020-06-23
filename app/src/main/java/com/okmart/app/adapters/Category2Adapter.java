package com.okmart.app.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.okmart.app.R;
import com.okmart.app.base_fragments.HomeFragment;
import com.okmart.app.model.CategoryData;
import com.okmart.app.utilities.Constants;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.round;

public class Category2Adapter extends PagerAdapter {

    private Context context;
    private HomeFragment homeFragment;
    private List<CategoryData> categoryDataList;

    private Category3Adapter category3Adapter;
    private RecyclerView rv_category;


    private String TAG = BannerAdapter.class.getSimpleName();

    int i1 = 0, i2 = 0;

    public Category2Adapter(Context context,List<CategoryData> categoryDataList, HomeFragment homeFragment) {
        this.context=context;
        this.categoryDataList=categoryDataList;
        this.homeFragment = homeFragment;
    }


    @Override
    public int getCount() {
        int a=categoryDataList.size();
        int y = 4;
        float z = (float) a / (float) y;

        return round(round(z));
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.category2, container, false   );

        rv_category = view.findViewById(R.id.rv_category);
        rv_category.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));

        setData(position);

        /*List<CategoryData> categoryDataList0 = new ArrayList<>(categoryDataList);
        List<CategoryData> categoryDataList1 = new ArrayList<>(categoryDataList);
        List<CategoryData> categoryDataList2 = new ArrayList<>(categoryDataList);
        List<CategoryData> categoryDataList3 = new ArrayList<>(categoryDataList);
        List<CategoryData> categoryDataList4 = new ArrayList<>(categoryDataList);
        List<CategoryData> categoryDataList5 = new ArrayList<>(categoryDataList);


        if(position == 0) {
            i1 = 0;
            i2 = 4;

            if(i2>=categoryDataList0.size()) {
                i2 = categoryDataList0.size();
            }
            //rv_category.setAdapter(new Category3Adapter(context,categoryDataList0.subList(i1, i2), homeFragment));
            category3Adapter = new Category3Adapter(context,categoryDataList0.subList(i1, i2), homeFragment);
            rv_category.setAdapter(category3Adapter);


        }
        else if(position == 1) {
            i1 = 4;
            i2 = 8;

            if(i2>=categoryDataList1.size()) {
                i2 = categoryDataList1.size();
            }

            //rv_category.setAdapter(new Category3Adapter(context,categoryDataList1.subList(i1, i2), homeFragment));
            category3Adapter = new Category3Adapter(context,categoryDataList1.subList(i1, i2), homeFragment);
            rv_category.setAdapter(category3Adapter);
        }
        else if(position == 2) {
            i1 = 8;
            i2 = 12;

            if(i2>=categoryDataList2.size()) {
                i2 = categoryDataList2.size();
            }

            rv_category.setAdapter(new Category3Adapter(context,categoryDataList2.subList(i1, i2), homeFragment));
        }
        else if(position == 3) {
            i1 = 12;
            i2 = 16;

            if(i2>=categoryDataList3.size()) {
                i2 = categoryDataList3.size();
            }

            rv_category.setAdapter(new Category3Adapter(context,categoryDataList3.subList(i1, i2), homeFragment));
        }
        else if(position == 4) {
            i1 = 16;
            i2 = 20;

            if(i2>=categoryDataList4.size()) {
                i2 = categoryDataList4.size();
            }

            rv_category.setAdapter(new Category3Adapter(context,categoryDataList4.subList(i1, i2), homeFragment));
        }
        else if(position == 5) {
            i1 = 20;
            i2 = 24;

            if(i2>=categoryDataList5.size()) {
                i2 = categoryDataList5.size();
            }

            rv_category.setAdapter(new Category3Adapter(context,categoryDataList5.subList(i1, i2), homeFragment));
        }
        else if(position == 6) {
            i1 = 24;
            i2 = 28;
        }
        else if(position == 7) {
            i1 = 28;
            i2 = 32;
        }
        else if(position == 8) {
            i1 = 32;
            i2 = 36;
        }

        if(i2>=categoryDataList.size()) {
            i2 = categoryDataList.size();
        }*/

//        rv_category.setAdapter(new Category3Adapter(context,categoryDataList, homeFragment, position));

        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view, 0);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;

    }

    public void setData(int position)
    {
        if (Constants.isClick.equals("true"))
        {
            for (int i = 0;i<categoryDataList.size();i++)
            {
                CategoryData categoryData = new CategoryData();

                categoryData.setName(categoryDataList.get(i).getName());
                categoryData.setCat_icon(categoryDataList.get(i).getCat_icon());
                categoryData.setCat_selected_icon(categoryDataList.get(i).getCat_selected_icon());
                categoryData.setCategory_ref(categoryDataList.get(i).getCategory_ref());

                if(categoryDataList.get(i).getCategory_ref().equals(Constants.category_ref))//if(categoryDataList.get(position).isCheck()==true)
                {
                    categoryData.setCheck(true);
                }
                else
                {
                    categoryData.setCheck(false);
                }
                categoryDataList.remove(i);
                categoryDataList.add(i,categoryData);
            }
        }



        List<CategoryData> categoryDataList0 = new ArrayList<>(categoryDataList);
        List<CategoryData> categoryDataList1 = new ArrayList<>(categoryDataList);
        List<CategoryData> categoryDataList2 = new ArrayList<>(categoryDataList);
        List<CategoryData> categoryDataList3 = new ArrayList<>(categoryDataList);
        List<CategoryData> categoryDataList4 = new ArrayList<>(categoryDataList);
        List<CategoryData> categoryDataList5 = new ArrayList<>(categoryDataList);


        if(position == 0)
        {
            i1 = 0;
            i2 = 4;

            if(i2>=categoryDataList0.size())
            {
                i2 = categoryDataList0.size();
            }
            if (categoryDataList0.subList(i1, i2).size()==4)
            {
                ViewGroup.LayoutParams params = rv_category.getLayoutParams();
                params.width = LinearLayout.LayoutParams.WRAP_CONTENT * categoryDataList0.subList(i1, i2).size();
                rv_category.setLayoutParams(params);
            }


            //rv_category.setAdapter(new Category3Adapter(context,categoryDataList0.subList(i1, i2), homeFragment));
            category3Adapter = new Category3Adapter(context,categoryDataList0.subList(i1, i2), homeFragment);
            rv_category.setAdapter(category3Adapter);


        }
        else if(position == 1)
        {
            i1 = 4;
            i2 = 8;

            if(i2>=categoryDataList1.size()) {
                i2 = categoryDataList1.size();
            }


            if (categoryDataList1.subList(i1, i2).size()==4)
            {
                ViewGroup.LayoutParams params = rv_category.getLayoutParams();
                params.width = LinearLayout.LayoutParams.WRAP_CONTENT * categoryDataList1.subList(i1, i2).size();
                rv_category.setLayoutParams(params);
            }

            //rv_category.setAdapter(new Category3Adapter(context,categoryDataList1.subList(i1, i2), homeFragment));
            category3Adapter = new Category3Adapter(context,categoryDataList1.subList(i1, i2), homeFragment);
            rv_category.setAdapter(category3Adapter);
        }
        else if(position == 2) {
            i1 = 8;
            i2 = 12;

            if(i2>=categoryDataList2.size()) {
                i2 = categoryDataList2.size();
            }

            if (categoryDataList2.subList(i1, i2).size()==4)
            {
                ViewGroup.LayoutParams params = rv_category.getLayoutParams();
                params.width = LinearLayout.LayoutParams.WRAP_CONTENT * categoryDataList2.subList(i1, i2).size();
                rv_category.setLayoutParams(params);
            }

            rv_category.setAdapter(new Category3Adapter(context,categoryDataList2.subList(i1, i2), homeFragment));
            rv_category.setAdapter(category3Adapter);
        }
        else if(position == 3) {
            i1 = 12;
            i2 = 16;

            if(i2>=categoryDataList3.size()) {
                i2 = categoryDataList3.size();
            }

            if (categoryDataList3.subList(i1, i2).size()==4)
            {
                ViewGroup.LayoutParams params = rv_category.getLayoutParams();
                params.width = LinearLayout.LayoutParams.WRAP_CONTENT * categoryDataList3.subList(i1, i2).size();
                rv_category.setLayoutParams(params);
            }

            rv_category.setAdapter(new Category3Adapter(context,categoryDataList3.subList(i1, i2), homeFragment));
            rv_category.setAdapter(category3Adapter);
        }
        else if(position == 4) {
            i1 = 16;
            i2 = 20;

            if(i2>=categoryDataList4.size()) {
                i2 = categoryDataList4.size();
            }

            if (categoryDataList4.subList(i1, i2).size()==4)
            {
                ViewGroup.LayoutParams params = rv_category.getLayoutParams();
                params.width = LinearLayout.LayoutParams.WRAP_CONTENT * categoryDataList4.subList(i1, i2).size();
                rv_category.setLayoutParams(params);
            }

            rv_category.setAdapter(new Category3Adapter(context,categoryDataList4.subList(i1, i2), homeFragment));
            rv_category.setAdapter(category3Adapter);
        }
        else if(position == 5) {
            i1 = 20;
            i2 = 24;

            if(i2>=categoryDataList5.size()) {
                i2 = categoryDataList5.size();
            }

            if (categoryDataList5.subList(i1, i2).size()==4)
            {
                ViewGroup.LayoutParams params = rv_category.getLayoutParams();
                params.width = LinearLayout.LayoutParams.WRAP_CONTENT * categoryDataList5.subList(i1, i2).size();
                rv_category.setLayoutParams(params);
            }

            rv_category.setAdapter(new Category3Adapter(context,categoryDataList5.subList(i1, i2), homeFragment));
            rv_category.setAdapter(category3Adapter);
        }
        else if(position == 6) {
            i1 = 24;
            i2 = 28;
        }
        else if(position == 7) {
            i1 = 28;
            i2 = 32;
        }
        else if(position == 8) {
            i1 = 32;
            i2 = 36;
        }

        if(i2>=categoryDataList.size()) {
            i2 = categoryDataList.size();
        }
    }


    public void getViewPagerPosition(int position)
    {
        //category3Adapter.getViewPagerPosition(position);
        Log.e("categoryDataList.size()",""+categoryDataList.size());

        //int check_position = 4*position;

        if (Constants.isClick.equals("true"))
        {
            for (int i = 0;i<categoryDataList.size();i++)
            {
                CategoryData categoryData = new CategoryData();

                categoryData.setName(categoryDataList.get(i).getName());
                categoryData.setCat_icon(categoryDataList.get(i).getCat_icon());
                categoryData.setCat_selected_icon(categoryDataList.get(i).getCat_selected_icon());
                categoryData.setCategory_ref(categoryDataList.get(i).getCategory_ref());

                if(categoryDataList.get(i).getCategory_ref().equals(Constants.category_ref))//if(categoryDataList.get(position).isCheck()==true)
                {
                    categoryData.setCheck(true);
                }
                else
                {
                    categoryData.setCheck(false);
                }
                categoryDataList.remove(i);
                categoryDataList.add(i,categoryData);
            }
        }

        List<CategoryData> categoryDataList0 = new ArrayList<>(categoryDataList);
        List<CategoryData> categoryDataList1 = new ArrayList<>(categoryDataList);
        List<CategoryData> categoryDataList2 = new ArrayList<>(categoryDataList);
        List<CategoryData> categoryDataList3 = new ArrayList<>(categoryDataList);
        List<CategoryData> categoryDataList4 = new ArrayList<>(categoryDataList);
        List<CategoryData> categoryDataList5 = new ArrayList<>(categoryDataList);

        if(position == 0) {
            i1 = 0;
            i2 = 4;

            if(i2>=categoryDataList0.size()) {
                i2 = categoryDataList0.size();
            }

            category3Adapter = new Category3Adapter(context,categoryDataList0.subList(i1, i2), homeFragment);
            //rv_category.setAdapter(category3Adapter);

        }
        else if(position == 1) {
            i1 = 4;
            i2 = 8;

            if(i2>=categoryDataList1.size()) {
                i2 = categoryDataList1.size();
            }

            category3Adapter = new Category3Adapter(context,categoryDataList1.subList(i1, i2), homeFragment);
            //rv_category.setAdapter(category3Adapter);
        }
        else if(position == 2) {
            i1 = 8;
            i2 = 12;

            if(i2>=categoryDataList2.size()) {
                i2 = categoryDataList2.size();
            }

            rv_category.setAdapter(new Category3Adapter(context,categoryDataList2.subList(i1, i2), homeFragment));
        }
        else if(position == 3) {
            i1 = 12;
            i2 = 16;

            if(i2>=categoryDataList3.size()) {
                i2 = categoryDataList3.size();
            }

            rv_category.setAdapter(new Category3Adapter(context,categoryDataList3.subList(i1, i2), homeFragment));
        }
        else if(position == 4) {
            i1 = 16;
            i2 = 20;

            if(i2>=categoryDataList4.size()) {
                i2 = categoryDataList4.size();
            }

            rv_category.setAdapter(new Category3Adapter(context,categoryDataList4.subList(i1, i2), homeFragment));
        }
        else if(position == 5) {
            i1 = 20;
            i2 = 24;

            if(i2>=categoryDataList5.size()) {
                i2 = categoryDataList5.size();
            }

            rv_category.setAdapter(new Category3Adapter(context,categoryDataList5.subList(i1, i2), homeFragment));
        }
        else if(position == 6) {
            i1 = 24;
            i2 = 28;
        }
        else if(position == 7) {
            i1 = 28;
            i2 = 32;
        }
        else if(position == 8) {
            i1 = 32;
            i2 = 36;
        }

        if(i2>=categoryDataList.size()) {
            i2 = categoryDataList.size();
        }

        rv_category.setAdapter(category3Adapter);
        //category3Adapter.notifyDataSetChanged();
    }



}
