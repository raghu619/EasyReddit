package com.example.android.easyreddit.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.easyreddit.R;
import com.example.android.easyreddit.touchhelper.ItemTouchHelperAdapter;
import com.example.android.easyreddit.touchhelper.ItemTouchHelperViewHolder;
import com.example.android.easyreddit.touchhelper.OnStartDragListener;
import com.example.android.easyreddit.utils.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class RedditRecyclierView extends RecyclerView.Adapter<RedditRecyclierView.ItemHolder>implements ItemTouchHelperAdapter {

    private static String LOG_TAG = RedditRecyclierView.class.getSimpleName();
    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mItems,fromPosition,toPosition);
        notifyItemMoved(fromPosition, toPosition);
        prefs.edit().putString(Resources.getSystem().getString(R.string.subreddits_key),StringUtil.arrayToString(mItems)).commit();
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
        prefs.edit().putString(Resources.getSystem().getString(R.string.subreddits_key),StringUtil.arrayToString(mItems)).commit();

    }

    public void add(int location, String iName) {
        mItems.add(location, iName);
        notifyItemInserted(location);
        prefs.edit().putString(Resources.getSystem().getString(R.string.subreddits_key), StringUtil.arrayToString(mItems)).apply();
    }

    public void remove(int location) {
        if (location >= mItems.size())
            return;

        mItems.remove(location);
        notifyItemRemoved(location);
        prefs.edit().putString(Resources.getSystem().getString(R.string.subreddits_key
        ), StringUtil.arrayToString(mItems)).apply();
    }



    public interface OnItemClickListener {
        public void onItemClick(ItemHolder item, int position);
    }


    private OnItemClickListener onItemClickListener;

    private LayoutInflater layoutInflater;
    private final OnStartDragListener mDragStartListener;
    private final ArrayList<String> mItems = new ArrayList<>();
    SharedPreferences prefs;


    public RedditRecyclierView(Context context, OnStartDragListener dragStartListener) {
        mDragStartListener = dragStartListener;
        layoutInflater = LayoutInflater.from(context);
        prefs = context.getSharedPreferences(
                context.getString(R.string.Subreddits_shared_preferences), Context.MODE_PRIVATE);
        Log.d(LOG_TAG, prefs.getString(context.getString(R.string.subreddits_key), "ERROR"));
        mItems.addAll(Arrays.asList(prefs.getString(context.getString(R.string.subreddits_key), "").split(",")));

    }


    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = layoutInflater.inflate(R.layout.subreddit_layout_item, parent, false);
        return new ItemHolder(itemView);
    }

    @Override

    public void onBindViewHolder(final ItemHolder holder, int position) {

        holder.setItemName(mItems.get(position));

        holder.handleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEventCompat.getActionMasked(motionEvent) == MotionEvent.ACTION_DOWN){
                    mDragStartListener.onStartDrag(holder);
                }

                return true;
            }
        });



    }

    @Override
    public int getItemCount() {
        return  (mItems!=null)?mItems.size():0;
    }


    public ArrayList<String> getmItems() {
        return mItems;
    }

    public static class ItemHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

        public final TextView textItemName;
        public final ImageView handleView;


        public ItemHolder(View itemView) {
            super(itemView);
            textItemName = (TextView) itemView.findViewById(R.id.item_name);

            handleView = (ImageView) itemView.findViewById(R.id.handle);

        }

        public void setItemName(CharSequence name) {
            textItemName.setText(name);
        }

        public CharSequence getItemName() {
            return textItemName.getText();
        }


        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

}

