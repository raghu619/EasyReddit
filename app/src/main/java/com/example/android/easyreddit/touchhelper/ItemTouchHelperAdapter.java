package com.example.android.easyreddit.touchhelper;

public interface  ItemTouchHelperAdapter {


    boolean onItemMove(int fromPosition, int toPosition);

    void  onItemDismiss(int position);
}
