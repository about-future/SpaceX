package com.about.future.spacex.model.mission;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Ignore;

import com.google.gson.annotations.SerializedName;


public class Links {
    //@ColumnInfo(name = "patch")
    @SerializedName("patch")
    @Embedded
    private Patch missionPatch;

    //@ColumnInfo(name = "reddit")
    @SerializedName("reddit")
    @Embedded
    private Reddit reddit;

    //@ColumnInfo(name = "flickr")
    @SerializedName("flickr")
    @Embedded
    private Flickr flickr;

    @ColumnInfo(name = "presskit")
    @SerializedName("presskit")
    private String presskit;

    @ColumnInfo(name = "article")
    @SerializedName("article")
    private String articleLink;

    @SerializedName("wikipedia")
    @ColumnInfo(name = "wikipedia")
    private String wikipedia;

    @ColumnInfo(name = "webcast")
    @SerializedName("webcast")
    private String videoLink;

    @ColumnInfo(name = "youtube_id")
    @SerializedName("youtube_id")
    private String youtubeId;

    public Links(Patch missionPatch, Reddit reddit, Flickr flickr, String presskit,
                 String articleLink, String wikipedia, String videoLink, String youtubeId) {
        this.missionPatch = missionPatch;
        this.reddit = reddit;
        this.flickr = flickr;
        this.presskit = presskit;
        this.articleLink = articleLink;
        this.wikipedia = wikipedia;
        this.videoLink = videoLink;
        this.youtubeId = youtubeId;
    }

    @Ignore
    public Links(){}

    public Patch getMissionPatch() {
        return missionPatch;
    }

    public Reddit getReddit() {
        return reddit;
    }

    public Flickr getFlickr() {
        return flickr;
    }

    public String getPresskit() {
        return presskit;
    }

    public String getArticleLink() {
        return articleLink;
    }

    public String getWikipedia() {
        return wikipedia;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public String getYoutubeId() {
        return youtubeId;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
    }
}
