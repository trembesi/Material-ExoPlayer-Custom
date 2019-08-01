package com.blogspot.materialexoplayercustom.uborampe;


import android.content.Context;

import com.blogspot.materialexoplayercustom.BuildConfig;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Sasipitu on 14/11/2018.
 */

public class StelKendo {

    // keyword ads
    public static final String KEYWORD1 = "insurance quotes";
    public static final String KEYWORD2 = "credit card";
    public static final String KEYWORD3 = "money transfer";
    public static final String KEYWORD4 = "mortgage loan";

    public static final int persiCode = BuildConfig.VERSION_CODE;
    public static final String persiName = "ver." +  BuildConfig.VERSION_NAME;

    public static final int WEKTU_CUCUK_LAMPAH = 2000;
    public static final int WEKTU_TOMBOL_MUNCUL = 500;
    public static final int TAG_SOCKET_TIMEOUT = 5000;

    private String URL_KONTEN = "https://www.dropbox.com/s/3ue9py92lmno9q3/exo_json.json?dl=1";

    public static final String KEY_ARRAY_ERROR = "ERROR";
    public static final String KEY_ARRAY_MESSAGE = "MESSAGE";
    public static final String KEY_ARRAY_RESPONSE = "RESPONSE";
    public static final String KEY_ARRAY_JUDUL = "judul";
    public static final String KEY_ARRAY_SUBJUDUL = "subjudul";
    public static final String KEY_ARRAY_GENRE = "genre";
    public static final String KEY_ARRAY_URL = "url";
    public static final String KEY_ARRAY_GAMBAR_POSTER = "gambar_poster";
    public static final String KEY_ARRAY_GAMBAR_FULL = "gambar_full";
    public static final String KEY_ARRAY_DESKRIPSI = "deskripsi";

    public static final String TAG_ARRAY_ERROR = "error";
    public static final String TAG_ARRAY_PESAN = "message";
    public static final String TAG_ARRAY_REPLAY = "replay";

    public static final String TAG_ID_APP = "id_app";
    public static final String TAG_ID_BANNER = "id_banner";
    public static final String TAG_ID_INTERSTITIAL = "id_interstitial";

    private Context mContext;
    public StelKendo(Context mContext) {
        this.mContext = mContext;
    }

    public String getServerKontenURL() {
        String urlServerKonten = URL_KONTEN;
        return urlServerKonten;
    }

}
