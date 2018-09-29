package com.example.android.easyreddit.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.easyreddit.R;
import com.example.android.easyreddit.model.CommentData;

import org.w3c.dom.Comment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentsViewAdapter extends RecyclerView.Adapter<CommentsViewAdapter.CommentViewHolder> {



        private ArrayList<CommentData> mcommentArrayList;
        private Context mContext;




        public CommentsViewAdapter(Context mContext,ArrayList<CommentData> mcommentArrayList){
            this.mContext=mContext;
            this.mcommentArrayList=mcommentArrayList;




        }


        @Override
        public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item,null);
            return new CommentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CommentViewHolder holder, int position) {


            CommentData comment_data=mcommentArrayList.get(position);
            holder.mauthor.setText(comment_data.getAuthor());
            holder.mpoints.setText(comment_data.getPoints() + " Points");
            holder.mbody.setText(comment_data.getHtmlText());
            holder.mpostedOn.setText(comment_data.getPostedOn());


        }

        @Override
        public int getItemCount() {
            return (mcommentArrayList!=null?mcommentArrayList.size():0);
        }

        public class CommentViewHolder extends RecyclerView.ViewHolder {
                @BindView(R.id.author)
                protected TextView mauthor;
                @BindView(R.id.body)
                protected TextView mbody;
                @BindView(R.id.postedOn)
                protected TextView mpostedOn;
                @BindView(R.id.points)
                protected TextView mpoints;
                @BindView(R.id.view)
                protected RelativeLayout  mlevelIndicator ;
                @BindView(R.id.card_linear)
                LinearLayout mcontainer;

                public CommentViewHolder(View itemView) {
                    super(itemView);

                    ButterKnife.bind(this,itemView);



                }

        }



        public void clearAdapter(){

            mcommentArrayList.clear();
            notifyDataSetChanged();


        }



}
