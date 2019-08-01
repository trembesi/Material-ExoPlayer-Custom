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

import com.blogspot.materialexoplayercustom.player.ExoKangji;
import com.blogspot.materialexoplayercustom.player.StelKendoLayarTancep;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;

import java.net.MalformedURLException;
import java.net.URL;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

public class LayarCilikActivity extends AppCompatActivity {

    private String TAG = "=== " + LayarCilikActivity.class.getSimpleName() + " ===";
    private Button btnTest;
    private PlayerView mPlayerViewCilik;
    private String videoLink;
    private ImageView ivFullscreen;
    private FrameLayout btnFrameFullscreen;
    private boolean isFullScreen;
    private String fullscreenVideoLink;

    private ProgressBar pbBuffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        //getSupportActionBar().hide();
        setContentView(R.layout.activity_layar_cilik);
        njupukData();
        notoLayarCilik();
    }

    @Override
    public void onResume() {
        super.onResume();
        //for (String dataURL : mVideoUrls) {
        //ExoPlayerViewManager.getInstance(videoLink).goToForeground();
        //ExoPlayerManager.getSharedInstance(getActivity(), mPlayerViewCilik).goToForeground();
        //}
        //notoLayarCilik();
        //priksoData();
        //ExoKangji.getSharedInstance().exoPlayerListener();

        pbBuffer = findViewById(R.id.lc_pb_buffer);

        if (videoLink != null && mPlayerViewCilik != null) {
            //ExoKangji.getSharedInstance().persiapanExoPlayer(getActivity(), mPlayerViewCilik, videoLink);
            ekstrakManggis(videoLink);
            ExoKangji.getSharedInstance().goToForeground();
            isFullScreen = false;
            initFullscreenButton();
        }
        ivFullscreen.setImageResource(R.drawable.ic_fullscreen_expand);
        //Toast.makeText(this, TAG + " - " + "ON RESUME", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPause() {
        super.onPause();
        //for (String dataURL : mVideoUrls) {
        //ExoPlayerViewManager.getInstance(videoLink).goToBackground();
        //ExoPlayerManager.getSharedInstance(getActivity(), mPlayerViewCilik).goToBackground();
        //}
        ExoKangji.getSharedInstance().goToBackground();
        //Toast.makeText(this, TAG + " - " + "ON PAUSE", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ExoKangji.getSharedInstance().releasePlayer();
        //Toast.makeText(this, TAG + " - " + "ON DESTROYVIEW", Toast.LENGTH_SHORT).show();
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
    private void notoLayarCilik() {
        getSupportActionBar().setTitle("Layar Tancep Activity");
        btnTest = findViewById(R.id.lc_btn_test);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(LayarCilikActivity.this, LayarGuedhiActivity.class));
                //String msg = Integer.toString(mPlayerViewCilik.getControllerShowTimeoutMs()) ;
                //Toast.makeText(LayarCilikActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        mPlayerViewCilik = findViewById(R.id.lc_player_view_cilik);
        //pbBuffer = findViewById(R.id.lc_pb_buffer);
    }

    private void njupukData() {
        if (getIntent().hasExtra(StelKendoLayarTancep.KEY_VIDEO_URI)) {
            videoLink = getIntent().getStringExtra(StelKendoLayarTancep.KEY_VIDEO_URI);

            Log.d("==LAPORAN==", "videoLink: " + videoLink);

            if (videoLink.equals(null)) {
                Log.d("==PIDIO LINK==", "NULL.....");

            } else {
                ekstrakManggis(videoLink);
                Log.d("==LAPORAN==", "OK PLAY...");
            }

        }
    }

    private void ekstrakManggis(String link) {
        try {
            URL url = new URL(link);
            //String baseUrl = url.getProtocol() + "://" + url.getHost();
            String baseUrl = url.getHost();
            if (baseUrl.equals(StelKendoLayarTancep.YT_BASE_URL_1) ||
                    baseUrl.equals(StelKendoLayarTancep.YT_BASE_URL_2) ||
                    baseUrl.equals(StelKendoLayarTancep.YT_BASE_URL_3)) {

                new YouTubeExtractor(this) {
                    @Override
                    public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                        if (ytFiles != null) {
                            int itag = 22;
                            //String downloadUrl = ytFiles.get(itag).getUrl();
                            //Intent intent = ExoPlayerActivity.getStartIntent(MainActivity.this, "http://dl3.film2movie.biz/film/Captain.America.Civil.War.2016.720p.BluRay.GAN.Film2Movie_BiZ.mkv");
                            //startActivity(intent);

                            //urlLayarTancep = ytFiles.get(itag).getUrl();
                            //playVideo(ytFiles.get(itag).getUrl());
                            ExoKangji.getSharedInstance().persiapanExoPlayer(LayarCilikActivity.this, mPlayerViewCilik, ytFiles.get(itag).getUrl(), pbBuffer);
                            fullscreenVideoLink = ytFiles.get(itag).getUrl();
                            Log.d("== PLAY VIDEO ==", ytFiles.get(itag).getUrl());
                        }
                    }
                }.extract(videoLink, true, true);

            } else {
                // LANGSUNG VIDEO LINK, GAK PERLU EXTRACTOR
                //playVideo(videoLink);
                ExoKangji.getSharedInstance().persiapanExoPlayer(this, mPlayerViewCilik, videoLink, pbBuffer);
                fullscreenVideoLink = videoLink;
                Log.d("== PLAY VIDEO ==", videoLink);
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
                    Intent intentGuedhi = new Intent(LayarCilikActivity.this, LayarGedhiActivity.class);
                    intentGuedhi.putExtra(StelKendoLayarTancep.KEY_VIDEO_URI, fullscreenVideoLink);
                    startActivity(intentGuedhi);
                }
            }
        });
    }


}
