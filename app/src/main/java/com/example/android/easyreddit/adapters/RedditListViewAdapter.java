package com.example.android.easyreddit.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.android.easyreddit.R;
import com.example.android.easyreddit.model.RedditData;
import com.example.android.easyreddit.utils.GlideApp;
import com.example.android.easyreddit.utils.GlideImageCacheModule;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by raghvendra on 24/9/18.
 */

public class RedditListViewAdapter extends RecyclerView.Adapter<RedditListViewAdapter.RedditItemViewHolder>
{

    private static String LOG_TAG = RedditListViewAdapter.class.getSimpleName();
    private List<RedditData> mlistItemsList;
    private Context mContext;

    OnItemClickListener mItemClickListener;
    private int focusedItem = 0;


    public interface  OnItemClickListener{

        void OnItemClick(View view,int position);

    }

            public RedditListViewAdapter(Context mcontext,List<RedditData> mlistItemsList)
            {
               this.mlistItemsList=mlistItemsList;

               this.mContext=mcontext;

            }

            @Override
            public RedditItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
            {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.reddit_list_item,null);

                return new RedditItemViewHolder(view);
            }

            @Override
            public void onBindViewHolder(RedditItemViewHolder holder, int position)
            {
                 RedditData redditData=mlistItemsList.get(position);

                 GlideApp.with(mContext).load(redditData.getThumbnail())
                         .diskCacheStrategy(DiskCacheStrategy.DATA).into(holder.mthumbnail);

               holder.mtitle.setText(redditData.getTitle());
               holder.mcomments.setText(String.valueOf(redditData.getNumComments()));
               holder.mscore.setText(String.valueOf(redditData.getScore()));
               holder.msubreddit.setText(redditData.getSubreddit());

                Log.v(LOG_TAG,redditData.getSubreddit());






            }

            @Override
            public int getItemCount() {


                return (mlistItemsList!=null ? mlistItemsList.size():0);
            }

            public  class RedditItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


                @BindView(R.id.list_image_view)
                protected ImageView mthumbnail;
                @BindView(R.id.title_view)
                protected TextView mtitle;
                @BindView(R.id.topic_name_text_view)
                protected TextView msubreddit;
                @BindView(R.id.comments)
                protected TextView mcomments;
                @BindView(R.id.score)
                protected TextView mscore;


                public RedditItemViewHolder(View itemView) {
                        super(itemView);

                    ButterKnife.bind(this,itemView);
                    itemView.setClickable(true);
                    itemView.setOnClickListener(this);



                }

                @Override
                public void onClick(View v) {


                    if (mItemClickListener != null) {
                        mItemClickListener.OnItemClick(v, getAdapterPosition());
                    }

                }
            }



    public void clearAdapter()
    {
        if(mlistItemsList!=null)
            mlistItemsList.clear();
        notifyDataSetChanged();
    }


    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }


}
