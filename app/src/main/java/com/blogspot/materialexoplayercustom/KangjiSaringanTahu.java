package com.blogspot.materialexoplayercustom;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import com.blogspot.materialexoplayercustom.player.ConfigPlayerKangji;
import com.blogspot.materialexoplayercustom.yt_extractor_naveedhassan913.ExtractorException;
import com.blogspot.materialexoplayercustom.yt_extractor_naveedhassan913.YoutubeStreamExtractor;
import com.blogspot.materialexoplayercustom.yt_extractor_naveedhassan913.model.YTMedia;
import com.blogspot.materialexoplayercustom.yt_extractor_naveedhassan913.model.YoutubeMeta;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

public class KangjiSaringanTahu {

    private String TAG = "=== " + KangjiSaringanTahu.class.getSimpleName() + " ===";
    private Context mContext;
    private String outputSource;
    private String inputSource;
    private KangjiSaringanTahuListener listener;
    private URI mURI;
    private String scheme;

    public KangjiSaringanTahu(Context mContext, String inputSource, KangjiSaringanTahuListener listener) {
        this.mContext = mContext;
        this.inputSource = inputSource;
        this.listener = listener;
        saring();
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
                listener.onHasilSaringan(outputSource, false);
                break;
            }

            default: {

                try {
                    URL url = new URL(inputSource);
                    String baseUrl = url.getHost().toLowerCase();
                    if (baseUrl.equals(ConfigPlayerKangji.YT_BASE_URL_1) ||
                            baseUrl.equals(ConfigPlayerKangji.YT_BASE_URL_2) ||
                            baseUrl.equals(ConfigPlayerKangji.YT_BASE_URL_3)) {

                        new YouTubeExtractor(mContext) {
                            @Override
                            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {

                                if (ytFiles != null) {
                                    // 720, 1080, 480
                                    List<Integer> iTags = Arrays.asList(22, 137, 18);
                                    for (Integer iTag : iTags) {
                                        YtFile ytFile = ytFiles.get(iTag);
                                        if (ytFile != null) {
                                            outputSource = ytFile.getUrl();
                                            if (outputSource != null && !outputSource.isEmpty()) {
                                                Log.e(TAG, "ekstrakManggisHaarigerHarald - URL: " + outputSource);
                                                //ExoKangjiNew.getSharedInstance().playContent(outputSource, true);
                                                listener.onHasilSaringan(outputSource, true);
                                                //return;
                                            }
                                        }
                                    }
                                }
                                else {
                                    Log.e(TAG, "ekstrakManggisHaarigerHarald: ZONK");
                                    ekstrakManggisNaveedHassan931(inputSource);
                                }

                            }
                        }.extract(inputSource, true, true);

                    }
                    else {
                        // LANGSUNG VIDEO LINK, GAK PERLU EXTRACTOR
                        outputSource = inputSource;
                        //ExoKangjiNew.getSharedInstance().playContent(outputSource, false);
                        listener.onHasilSaringan(outputSource, false);
                        Log.e(TAG, "LANGSUNG VIDEO LINK, GAK PERLU EXTRACTOR" + "\n" + "outputSource: " + outputSource);
                    }
                }
                catch (MalformedURLException e) {
                    e.printStackTrace();
                    Log.e(TAG, "(= ERROR ekstrakManggisHaarigerHarald =) - " + e.toString());
                }

                break;

            }

        }

    }


    private void ekstrakManggisNaveedHassan931(String linkSource) {
        new YoutubeStreamExtractor(new YoutubeStreamExtractor.ExtractorListner() {
            @Override
            public void onExtractionGoesWrong(ExtractorException e) {
                Log.e(TAG, e.toString());
            }

            @Override
            public void onExtractionDone(List<YTMedia> adaptiveStream, List<YTMedia> muxedStream, YoutubeMeta meta) {

                for (YTMedia c:muxedStream) {
                    Log.e(TAG + " - muxedStream", Integer.toString(c.getItag()));
                }
                for (YTMedia c:adaptiveStream) {
                    Log.e(TAG + " - adaptiveStream", Integer.toString(c.getItag()));
                }

                if (muxedStream.isEmpty()) {
                    Log.e(TAG, "muxedStream isEmpty: " + muxedStream);
                }
                else {
                    // 22 = 720p  // 137 = 1080p  // 18 = 480p
                    //String url = adaptiveStream.get(22).getUrl();
                    String url = muxedStream.get(0).getUrl();
                    //ExoKangjiNew.getSharedInstance().playContent(url, true);
                    listener.onHasilSaringan(url, true);
                    Log.e(TAG, "ekstrakManggisNaveedHassan931 - muxedStream URL: " + url);
                }

            }
        }).Extract(linkSource);
    }

    public interface KangjiSaringanTahuListener {
        void onHasilSaringan(String outputSource, boolean isYTSource);
    }

}
