package com.about.future.spacex.mission_entity;

import android.arch.persistence.room.ColumnInfo;

import com.google.gson.annotations.SerializedName;

public class Links {
    @ColumnInfo(name = "mission_patch")
    @SerializedName("mission_patch")
    private String missionPatch; //:"https://images2.imgbox.com/43/35/0QW7yRsB_o.png",
    @ColumnInfo(name = "mission_patch_small")
    @SerializedName("mission_patch_small")
    private String missionPatchSmall;   //:"https://images2.imgbox.com/11/ec/xng5hAXN_o.png",
    @ColumnInfo(name = "reddit_campaign")
    @SerializedName("reddit_campaign")
    private String redditCampaign;   //:"https://www.reddit.com/r/spacex/comments/8pua1m/crs15_launch_campaign_thread/",
    @ColumnInfo(name = "reddit_launch")
    @SerializedName("reddit_launch")
    private String redditLaunch;     //:"https://www.reddit.com/r/spacex/comments/8ugo3l/rspacex_crs15_official_launch_discussion_updates",
    @ColumnInfo(name = "reddit_recovery")
    @SerializedName("reddit_recovery")
    private String redditRecovery;   //:null,
    @ColumnInfo(name = "reddit_media")
    @SerializedName("reddit_media")
    private String redditMedia;      //:"https://www.reddit.com/r/spacex/comments/8ujcwo/rspacex_crs15_media_thread_videos_images_gifs/",
    //@ColumnInfo(name = "")
    @SerializedName("presskit")
    private String presskit;          //:"http://www.spacex.com/sites/spacex/files/crs15presskit.pdf",
    @ColumnInfo(name = "article_link")
    @SerializedName("article_link")
    private String articleLink;      //:null,
    //@ColumnInfo(name = "")
    @SerializedName("wikipedia")
    private String wikipedia;         //:null,
    @ColumnInfo(name = "video_link")
    @SerializedName("video_link")
    private String videoLink;        //:"https://www.youtube.com/watch?v=ycMagB1s8XM"

    public Links(String missionPatch, String missionPatchSmall, String redditCampaign,
                 String redditLaunch, String redditRecovery, String redditMedia, String presskit,
                 String articleLink, String wikipedia, String videoLink) {
        this.missionPatch = missionPatch;
        this.missionPatchSmall = missionPatchSmall;
        this.redditCampaign = redditCampaign;
        this.redditLaunch = redditLaunch;
        this.redditRecovery = redditRecovery;
        this.redditMedia = redditMedia;
        this.presskit = presskit;
        this.articleLink = articleLink;
        this.wikipedia = wikipedia;
        this.videoLink = videoLink;
    }

    public String getMissionPatch() { return missionPatch; }
    public String getMissionPatchSmall() { return missionPatchSmall; }
    public String getArticleLink() { return articleLink; }
    public String getPresskit() { return presskit; }
    public String getRedditCampaign() { return redditCampaign; }
    public String getRedditLaunch() { return redditLaunch; }
    public String getRedditMedia() { return redditMedia; }
    public String getRedditRecovery() { return redditRecovery; }
    public String getWikipedia() { return wikipedia; }
    public String getVideoLink() { return videoLink; }

    public void setMissionPatch(String missionPatch) { this.missionPatch = missionPatch; }
    public void setArticleLink(String articleLink) { this.articleLink = articleLink; }
    public void setMissionPatchSmall(String missionPatchSmall) { this.missionPatchSmall = missionPatchSmall; }
    public void setRedditCampaign(String redditCampaign) { this.redditCampaign = redditCampaign; }
    public void setPresskit(String presskit) { this.presskit = presskit; }
    public void setRedditLaunch(String redditLaunch) { this.redditLaunch = redditLaunch; }
    public void setRedditMedia(String redditMedia) { this.redditMedia = redditMedia; }
    public void setRedditRecovery(String redditRecovery) { this.redditRecovery = redditRecovery; }
    public void setVideoLink(String videoLink) { this.videoLink = videoLink; }
    public void setWikipedia(String wikipedia) { this.wikipedia = wikipedia; }
}
