package com.blogspot.materialexoplayercustom.onrecyclerview;

public class KangjiExoRVItem {

    //private int uId;
    private String header, body, footer, thumbnail, videoURL;

    public KangjiExoRVItem(/*int uId, */String header, String body, String footer, String thumbnail, String videoURL) {
        //this.uId = uId;
        this.header = header;
        this.body = body;
        this.footer = footer;
        this.thumbnail = thumbnail;
        this.videoURL = videoURL;
    }

    //public KangjiExoRVItem() {
    //}
    /*
    public int getuId() {
        return uId;
    }

    public void setuId(int uId) {
        this.uId = uId;
    }
     */

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

}
