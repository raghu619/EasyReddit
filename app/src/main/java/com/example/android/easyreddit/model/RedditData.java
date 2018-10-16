package com.example.android.easyreddit.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by raghvendra on 23/9/18.
 */

public class RedditData implements Parcelable {



    private String title;
    private String thumbnail;
    private String url;
    private String subreddit;
    private String author;
    private String permalink;
    private String id;

    private String imageUrl;

    private int score,numComments;
    private Boolean over18;

    public RedditData() {

    }


    public int getScore()
    {
        return score;
    }

    public void setScore(int score)
    {
        this.score = score;
    }

    public int getNumComments() {
        return numComments;
    }

    public void setNumComments(int numComments) {
        this.numComments = numComments;
    }

    public Boolean getOver18() {
        return over18;
    }

    public void setOver18(Boolean over18) {
        this.over18 = over18;
    }



    public String getImageUrl()
    {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getPostedOn() {
        return postedOn;
    }

    public void setPostedOn(long postedOn) {
        this.postedOn = postedOn;
    }

    private long postedOn;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }





    public RedditData(Parcel in)
    {
                 this.title=in.readString();
                 this.thumbnail = in.readString();
                 this.url = in.readString();
                 this.subreddit = in.readString();
                 this.author = in.readString();
                 this.permalink = in.readString();
                 this.id = in.readString();
                 this.postedOn = in.readLong();
                 this.imageUrl = in.readString();
                 this.score = in.readInt();
                 this.numComments = in.readInt();
                 this.over18 = (Boolean) in.readValue(Boolean.class.getClassLoader());



    }

    public static final Creator<RedditData> CREATOR = new Creator<RedditData>() {
        @Override
        public RedditData createFromParcel(Parcel in) {
            return new RedditData(in);
        }

        @Override
        public RedditData[] newArray(int size) {
            return new RedditData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
            dest.writeString(this.title);
            dest.writeString(this.thumbnail);
            dest.writeString(this.url);
            dest.writeString(this.subreddit);
            dest.writeString(this.author);
            dest.writeString(this.permalink);
            dest.writeString(this.id);
            dest.writeLong(this.postedOn);
            dest.writeString(this.imageUrl);
            dest.writeInt(this.score);
            dest.writeInt(this.numComments);
            dest.writeValue(this.over18);






      }
}
