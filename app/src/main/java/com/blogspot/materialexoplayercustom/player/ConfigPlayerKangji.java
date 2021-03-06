package com.blogspot.materialexoplayercustom.player;

import com.blogspot.materialexoplayercustom.R;

public class ConfigPlayerKangji {
    public static final String KEY_VIDEO_URI = "key_video_uri";
    public static final String KEY_VIDEO_JUDUL = "key_video_judul";
    public static final String KEY_VIDEO_DESKRIPSI = "key_video_deskripsi";
    public static final String KEY_VIDEO_GAMBAR_FULL = "key_video_gambar_full";

    //public static final String INTRO = "https://trembesi.github.io/loker/apps/android/sasi_player/files/video/intro.MP4";
    //public static final String INTRO = "https://www.youtube.com/watch?v=KBCI-rK4uq4";

    public static final String YT_BASE_URL_1 = "www.youtube.com";
    public static final String YT_BASE_URL_2 = "m.youtube.com";
    public static final String YT_BASE_URL_3 = "youtube.com";

    //Minimum Video you want to buffer while Playing
    public static final int MIN_BUFFER_DURATION = 5000;

    //Max Video you want to buffer during PlayBack
    public static final int MAX_BUFFER_DURATION = 5000;

    //Min Video you want to buffer before start Playing it
    public static final int MIN_PLAYBACK_START_BUFFER = 1500;

    //Min video You want to buffer when user resumes video
    public static final int MIN_PLAYBACK_RESUME_BUFFER = 5000;

    public static final String USER_AGENT = "ExoKangji_" + R.string.app_name;
}
