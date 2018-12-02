package com.example.android.easyreddit.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by raghvendra on 24/9/18.
 */

public class CommentData implements Parcelable

{
    String htmlText, author, points, postedOn;

    int level;

    public CommentData() {

    }

    public String getHtmlText() {

        return htmlText;
    }

    public void setHtmlText(String htmlText) {
        this.htmlText = htmlText;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getPostedOn() {
        return postedOn;
    }

    public void setPostedOn(String postedOn) {
        this.postedOn = postedOn;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public CommentData(Parcel in) {
        this.htmlText = in.readString();
        this.author = in.readString();
        this.points = in.readString();
        this.postedOn = in.readString();
        this.level = in.readInt();


    }

    public static final Creator<CommentData> CREATOR = new Creator<CommentData>() {
        @Override
        public CommentData createFromParcel(Parcel in) {
            return new CommentData(in);
        }

        @Override
        public CommentData[] newArray(int size) {
            return new CommentData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.htmlText);
        dest.writeString(this.author);
        dest.writeString(this.points);
        dest.writeString(this.postedOn);
        dest.writeInt(this.level);


    }
}
