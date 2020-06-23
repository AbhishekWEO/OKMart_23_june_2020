package com.okmart.app.pagination;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.okmart.app.utilities.Constants;

public abstract class PaginationListener extends RecyclerView.OnScrollListener {

    private LinearLayoutManager layoutManager;

    public PaginationListener(LinearLayoutManager layoutManager)
    {
        this.layoutManager=layoutManager;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        Log.e("PaginationListener", "visibleItemCount: "+visibleItemCount+" ,totalItemCount: "+totalItemCount );

        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        Log.e("PaginationListener", "firstVisibleItemPosition: "+firstVisibleItemPosition );

        if (!isLoading() && !isLastPage())
        {
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount &&
            firstVisibleItemPosition >=0 && totalItemCount >= Constants.limit)
            {
                loadMoreItems();
            }
        }
    }

    public abstract void loadMoreItems();
    public abstract boolean isLastPage();
    public abstract boolean isLoading();
}
