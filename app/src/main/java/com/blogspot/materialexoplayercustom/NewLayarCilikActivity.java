package com.blogspot.materialexoplayercustom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.blogspot.materialexoplayercustom.player.ConfigPlayerKangji;
import com.blogspot.materialexoplayercustom.player.ExoKangjiNew;
//import com.blogspot.materialexoplayercustom.player.ExoKangji;
import com.blogspot.materialexoplayercustom.tls.MeksoTLS;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;

import java.net.MalformedURLException;
import java.net.URL;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

public class NewLayarCilikActivity extends AppCompatActivity {

    private String TAG = "=== " + NewLayarCilikActivity.class.getSimpleName() + " ===";

    private PlayerView mPlayerViewCilik;
    private ProgressBar progressBar;
    private Button btnTest1, btnTest2, btnTest3, btnTest4, btnTest5;
    private String inputSource, outputSource;

    private ImageView ivFullscreen;
    private FrameLayout btnFrameFullscreen;
    private boolean isFullScreen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_new_layar_cilik);
        MeksoTLS meksoTLS = new MeksoTLS(this);
        meksoTLS.peksoTLSv12();
        notoNewLayarCilik();
    }

    @Override
    public void onResume() {
        super.onResume();
        /*
        pbBuffer = findViewById(R.id.new_lc_pb_buffer);
        if (videoLink != null && mPlayerViewCilik != null) {
            //ExoKangji.getSharedInstance().persiapanExoPlayer(getActivity(), mPlayerViewCilik, videoLink);
            ekstrakManggis(videoLink);
            ExoKangji.getSharedInstance().goToForeground();
            isFullScreen = false;
            initFullscreenButton();
        }
        ivFullscreen.setImageResource(R.drawable.ic_fullscreen_expand);
        //Toast.makeText(this, TAG + " - " + "ON RESUME", Toast.LENGTH_SHORT).show();
        */

        if (outputSource != null && mPlayerViewCilik != null) {
            //ekstrakManggis(inputSource);
            ExoKangjiNew.getSharedInstance(NewLayarCilikActivity.this).playStreamingContent(outputSource);
            ExoKangjiNew.getSharedInstance(NewLayarCilikActivity.this).goToForeground();
            isFullScreen = false;
        }
        ivFullscreen.setImageResource(R.drawable.ic_fullscreen_expand);


        /*
        if (mPlayerViewCilik != null) {
            if (outputSource != null) {
                ExoKangjiNew.getSharedInstance(NewLayarCilikActivity.this).playStreamingContent(outputSource);
                ExoKangjiNew.getSharedInstance(NewLayarCilikActivity.this).goToForeground();
            }
            else {
                ekstrakManggis(ConfigPlayerKangji.INTRO);
            }

            isFullScreen = false;
        }
        ivFullscreen.setImageResource(R.drawable.ic_fullscreen_expand);
        */


    }

    @Override
    public void onPause() {
        super.onPause();
        //ExoKangji.getSharedInstance().goToBackground();
        ExoKangjiNew.getSharedInstance(NewLayarCilikActivity.this).goToBackground();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //ExoKangji.getSharedInstance().releasePlayer();
        ExoKangjiNew.getSharedInstance(this).releasePlayer();
    }

    /*
   @Override
   public void onBackPressed() {

       if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
           finish();
       }
       else if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
           getSupportFragmentManager().popBackStack();
       }
       else {
           super.onBackPressed();
       }

   }
   */

    private void notoNewLayarCilik() {
        setTitle(TAG);
        progressBar = findViewById(R.id.new_lc_pb_buffer);
        mPlayerViewCilik = findViewById(R.id.new_lc_player_view_cilik);

        btnTest1 = findViewById(R.id.new_lc_btn_test_1);
        btnTest1.setText("Test Streaming ANTV");
        btnTest1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputSource = "http://202.80.222.170/000001/2/ch14061215034900095272/index.m3u8?virtualDomain=000001.live_hls.zte.com";
                //ExoKangjiNew.getSharedInstance(NewLayarCilikActivity.this).checkLinkAndPlayStreaming(inputSource);
                ekstrakManggis(inputSource);
            }
        });

        btnTest2 = findViewById(R.id.new_lc_btn_test_2);
        btnTest2.setText("Test Streaming AhsanTV");
        btnTest2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputSource = "http://119.82.224.75:1935/live/ahsantv/playlist.m3u8";
                //ExoKangjiNew.getSharedInstance(NewLayarCilikActivity.this).checkLinkAndPlayStreaming(inputSource);
                ekstrakManggis(inputSource);
            }
        });

        btnTest3 = findViewById(R.id.new_lc_btn_test_3);
        btnTest3.setText("Test Content YouTube 1");
        btnTest3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputSource = "https://www.youtube.com/watch?v=SXIGXSrwygU";
                //ExoKangjiNew.getSharedInstance(NewLayarCilikActivity.this).checkLinkAndPlayStreaming(inputSource);
                ekstrakManggis(inputSource);
            }
        });

        btnTest4 = findViewById(R.id.new_lc_btn_test_4);
        btnTest4.setText("Test Content YouTube 2");
        btnTest4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputSource = "https://www.youtube.com/watch?v=uykAHRDaZH8&t";
                //ExoKangjiNew.getSharedInstance(NewLayarCilikActivity.this).checkLinkAndPlayStreaming(inputSource);
                ekstrakManggis(inputSource);
            }
        });

        btnTest5 = findViewById(R.id.new_lc_btn_test_5);
        btnTest5.setText("Test Audio Streaming");
        btnTest5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputSource = "https://ia800501.us.archive.org/34/items/BurdahEnsemblePearlsAndCoral/Burdah%20Ensemble%20-%20Pearls%20and%20Coral%20%E2%80%93%2013.%20Al%20Madad%20Ya%20Rasul%20Allah.mp3";
                //ExoKangjiNew.getSharedInstance(NewLayarCilikActivity.this).checkLinkAndPlayStreaming(inputSource);
                ekstrakManggis(inputSource);
            }
        });
        initFullscreenButton();
        ExoKangjiNew.getSharedInstance(NewLayarCilikActivity.this).initializePlayer(mPlayerViewCilik, progressBar);
    }

    private void ekstrakManggis(String stringLinkContent) {
        inputSource = stringLinkContent;
        try {
            URL url = new URL(inputSource);
            //String baseUrl = url.getProtocol() + "://" + url.getHost();
            String baseUrl = url.getHost();
            if (baseUrl.equals(ConfigPlayerKangji.YT_BASE_URL_1) ||
                    baseUrl.equals(ConfigPlayerKangji.YT_BASE_URL_2) ||
                    baseUrl.equals(ConfigPlayerKangji.YT_BASE_URL_3)) {

                new YouTubeExtractor(this) {
                    @Override
                    public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                        if (ytFiles != null) {
                            int itag = 22;
                            //fullscreenVideoLink = ytFiles.get(itag).getUrl();
                            outputSource = ytFiles.get(itag).getUrl();
                            ExoKangjiNew.getSharedInstance(NewLayarCilikActivity.this).changeAndPlayStreamingContent(outputSource);
                            Log.d("== PLAY VIDEO ==", ytFiles.get(itag).getUrl());
                        }
                    }
                }.extract(inputSource, true, true);

            } else {
                // LANGSUNG VIDEO LINK, GAK PERLU EXTRACTOR
                outputSource = inputSource;
                ExoKangjiNew.getSharedInstance(NewLayarCilikActivity.this).changeAndPlayStreamingContent(outputSource);
                Log.d("== PLAY VIDEO ==", outputSource);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e("==EXTRAK MANGGIS==", e.toString());
        }
    }



    private void initFullscreenButton() {
        PlayerControlView controlView = mPlayerViewCilik.findViewById(R.id.exo_controller);
        ivFullscreen = controlView.findViewById(R.id.exo_fullscreen_icon);
        btnFrameFullscreen = controlView.findViewById(R.id.exo_fullscreen_button);
        btnFrameFullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFullScreen) {
                    // lek kondisine wes full screen
                }
                else {
                    // lek kondisine gung full screen
                    Intent intentGuedhi = new Intent(NewLayarCilikActivity.this, NewLayarGedhiActivity.class);
                    intentGuedhi.putExtra(ConfigPlayerKangji.KEY_VIDEO_URI, outputSource);
                    startActivity(intentGuedhi);
                    Toast.makeText(NewLayarCilikActivity.this, outputSource, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
