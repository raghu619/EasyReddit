package com.example.android.easyreddit.touchhelper;

public interface  ItemTouchHelperAdapter {


    void onItemMove(int fromPosition, int toPosition);

    boolean onItemDismiss(int position);
}
