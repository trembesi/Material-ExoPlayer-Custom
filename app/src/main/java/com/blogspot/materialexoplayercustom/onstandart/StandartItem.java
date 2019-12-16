package com.blogspot.materialexoplayercustom.onstandart;

public class StandartItem {

    private String gambar, videoPath, judul, deskripsi;

    public StandartItem(String gambar, String judul, String deskripsi, String videoPath) {
        this.gambar = gambar;
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.videoPath = videoPath;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }
}
