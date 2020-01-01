package com.blogspot.materialexoplayercustom;

import android.content.Context;
import android.util.Log;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class KangjiSaringanTahu {

    private String TAG = "=== " + KangjiSaringanTahu.class.getSimpleName() + " ===";
    private Context mContext;
    private String outputSource;
    private String inputSource;
    private KangjiSaringanTahuListener listener;
    private URI mURI;
    private String scheme;
    private boolean isYTSource;

    public KangjiSaringanTahu(Context mContext, String inputSource, KangjiSaringanTahuListener listener) {
        this.mContext = mContext;
        this.inputSource = inputSource;
        this.listener = listener;
    }

    private void saring() {
        try {
            mURI = new URI(inputSource);
            scheme = mURI.getScheme().toLowerCase();
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "scheme: " + scheme);

        switch (scheme) {
            case "rtp" :
            case "rtsp" :
            case "rtmp" :
            case "asset" :
            case "file" : {
                outputSource = inputSource;
                isYTSource = false;
                listener.onHasilSaringan(outputSource, isYTSource);
                break;
            }

            default: {

                try {
                    URL url = new URL(inputSource);

                }

            }

        }

    }

    public interface KangjiSaringanTahuListener {
        void onHasilSaringan(String outputSource, boolean isYTSource);
    }



}
