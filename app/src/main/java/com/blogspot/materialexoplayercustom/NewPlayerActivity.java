package com.blogspot.materialexoplayercustom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.blogspot.materialexoplayercustom.player.ConfigPlayerKangji;
import com.blogspot.materialexoplayercustom.player.ExoKangjiNew;
import com.blogspot.materialexoplayercustom.tls.MeksoTLS;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

public class NewPlayerActivity extends AppCompatActivity {

    private String TAG = "=== " + NewPlayerActivity.class.getSimpleName() + " ===";

    private PlayerView mPlayerViewCilik, mPlayerViewGedhi;
    private Button btnTest1, btnTest2, btnTest3, btnTest4, btnTest5, btnTest6;
    private String inputSource, outputSource;
    private EditText etInputLink;
    private Button btnGoPlay;

    private ImageView ivFullscreen;
    private FrameLayout btnFrameFullscreen;
    private boolean isFullScreen = false;
    private Dialog dialogFullscreen;

    private URI xURI;
    private String scheme;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_new_player);
        MeksoTLS meksoTLS = new MeksoTLS(this);
        meksoTLS.peksoTLSv12();
        notoNewLayarCilik();
    }

    @Override
    public void onResume() {
        super.onResume();
        /*
        if (outputSource != null && mPlayerViewCilik != null) {
            ekstrakManggis(outputSource);
        }
        ivFullscreen.setImageResource(R.drawable.ic_fullscreen_expand);

         */
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ExoKangjiNew.getSharedInstance().releasePlayer();
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

        etInputLink = findViewById(R.id.et_input_link);
        btnGoPlay = findViewById(R.id.btn_go);
        btnGoPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etInputLink.getText().toString().trim().isEmpty()) {
                    Toast.makeText(NewPlayerActivity.this, "EMPTY INPUT", Toast.LENGTH_SHORT).show();
                }
                else {
                    ekstrakManggis(etInputLink.getText().toString().trim());
                }
            }
        });

        btnTest1 = findViewById(R.id.new_lc_btn_test_1);
        btnTest1.setText("Test Streaming ANTV");
        btnTest1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputSource = "http://202.80.222.170/000001/2/ch14061215034900095272/index.m3u8?virtualDomain=000001.live_hls.zte.com";
                ekstrakManggis(inputSource);
            }
        });

        btnTest2 = findViewById(R.id.new_lc_btn_test_2);
        btnTest2.setText("Test Streaming AhsanTV");
        btnTest2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputSource = "http://119.82.224.75:1935/live/ahsantv/playlist.m3u8";
                ekstrakManggis(inputSource);
            }
        });

        btnTest3 = findViewById(R.id.new_lc_btn_test_3);
        btnTest3.setText("Test Content YouTube 1");
        btnTest3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputSource = "https://www.youtube.com/watch?v=SXIGXSrwygU";
                ekstrakManggis(inputSource);
            }
        });

        btnTest4 = findViewById(R.id.new_lc_btn_test_4);
        btnTest4.setText("Test Content YouTube 2");
        btnTest4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputSource = "https://www.youtube.com/watch?v=uykAHRDaZH8&t";
                ekstrakManggis(inputSource);
            }
        });

        btnTest5 = findViewById(R.id.new_lc_btn_test_5);
        btnTest5.setText("RTMP TVMU");
        btnTest5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputSource = "rtmp://118.97.183.222/discover/muhammadiyahtv";
                ekstrakManggis(inputSource);
            }
        });

        btnTest6 = findViewById(R.id.new_lc_btn_test_6);
        btnTest6.setText("Test Local Content");
        btnTest6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputSource = "https://cdn0-a.production.vidio.static6.com/uploads/1655223/ets-10-20uripedekne-d2e7-b1600.mp4.m3u8";
                Uri uriInputSource = Uri.parse("asset:///jogja_istimewa.MP4");
                ExoKangjiNew.getSharedInstance().playLocalContent(uriInputSource);
            }
        });

        mPlayerViewCilik = findViewById(R.id.new_lc_player_view_cilik);
        initFullscreenButton(mPlayerViewCilik);
        ivFullscreen.setImageResource(R.drawable.ic_fullscreen_expand);
        ExoKangjiNew.getSharedInstance().initializePlayer(NewPlayerActivity.this, mPlayerViewCilik, null);

        //play intro video
        ekstrakManggis(ConfigPlayerKangji.INTRO);
    }

    private void ekstrakManggis(String stringLinkContent) {
        inputSource = stringLinkContent;

        try {
            xURI = new URI(inputSource);
            scheme = xURI.getScheme().toUpperCase();
        }
        catch (URISyntaxException e) {

        }

        switch (scheme) {
            case "RTP" :
            case "RTSP" :
            case "RTMP" : {
                outputSource = inputSource;
                ExoKangjiNew.getSharedInstance().playStreamingContent(outputSource);
                break;
            }
            default: {
                try {
                    URL url = new URL(inputSource);
                    //String baseUrl = url.getProtocol() + "://" + url.getHost();
                    String baseUrl = url.getHost().toUpperCase();
                    if (baseUrl.equals(ConfigPlayerKangji.YT_BASE_URL_1) ||
                            baseUrl.equals(ConfigPlayerKangji.YT_BASE_URL_2) ||
                            baseUrl.equals(ConfigPlayerKangji.YT_BASE_URL_3)) {

                        new YouTubeExtractor(this) {
                            @Override
                            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                                if (ytFiles != null) {
                                    int itag = 22;
                                    outputSource = ytFiles.get(itag).getUrl();
                                    ExoKangjiNew.getSharedInstance().initializePlayer(NewPlayerActivity.this, mPlayerViewCilik, null);
                                    ExoKangjiNew.getSharedInstance().playStreamingContent(outputSource);
                                    Log.d("== PLAY VIDEO ==", ytFiles.get(itag).getUrl());
                                }
                            }
                        }.extract(inputSource, true, true);

                    } else {
                        // LANGSUNG VIDEO LINK, GAK PERLU EXTRACTOR
                        outputSource = inputSource;
                        ExoKangjiNew.getSharedInstance().playStreamingContent(outputSource);
                        Log.d("== PLAY VIDEO ==", outputSource);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Log.e("==EXTRAK MANGGIS==", e.toString());
                }
            }


        }




        /*
        try {
            URL url = new URL(inputSource);
            //String baseUrl = url.getProtocol() + "://" + url.getHost();
            String baseUrl = url.getHost().toUpperCase();
            if (baseUrl.equals(ConfigPlayerKangji.YT_BASE_URL_1) ||
                    baseUrl.equals(ConfigPlayerKangji.YT_BASE_URL_2) ||
                    baseUrl.equals(ConfigPlayerKangji.YT_BASE_URL_3)) {

                new YouTubeExtractor(this) {
                    @Override
                    public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                        if (ytFiles != null) {
                            int itag = 22;
                            outputSource = ytFiles.get(itag).getUrl();
                            ExoKangjiNew.getSharedInstance().initializePlayer(NewPlayerActivity.this, mPlayerViewCilik, null);
                            ExoKangjiNew.getSharedInstance().playStreamingContent(outputSource);
                            Log.d("== PLAY VIDEO ==", ytFiles.get(itag).getUrl());
                        }
                    }
                }.extract(inputSource, true, true);

            } else {
                // LANGSUNG VIDEO LINK, GAK PERLU EXTRACTOR
                outputSource = inputSource;
                ExoKangjiNew.getSharedInstance().playStreamingContent(outputSource);
                Log.d("== PLAY VIDEO ==", outputSource);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e("==EXTRAK MANGGIS==", e.toString());
        }

         */
    }

    private void initFullscreenButton(PlayerView playerView) {
        PlayerControlView controlView = playerView.findViewById(R.id.exo_controller);

        ivFullscreen = controlView.findViewById(R.id.exo_fullscreen_icon);
        btnFrameFullscreen = controlView.findViewById(R.id.exo_fullscreen_button);
        btnFrameFullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFullScreen) {
                    // lek kondisine wes full screen
                    closeFullscreen();
                    //Toast.makeText(NewPlayerActivity.this, "CLOSE", Toast.LENGTH_SHORT).show();
                }
                else {
                    // lek kondisine gung full screen
                    openFullScreen();
                    //Toast.makeText(NewPlayerActivity.this, "OPEN", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openFullScreen() {

        dialogFullscreen = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            public void onBackPressed() {
                if (isFullScreen) {
                    closeFullscreen();
                }
                super.onBackPressed();
            }
        };

        dialogFullscreen.setContentView(R.layout.fullscreen_player);
        mPlayerViewGedhi = dialogFullscreen.findViewById(R.id.new_lc_player_view_full);

        isFullScreen = true;
        ExoKangjiNew.getSharedInstance().switchScreen(mPlayerViewCilik, mPlayerViewGedhi);
        initFullscreenButton(mPlayerViewGedhi);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        dialogFullscreen.show();
    }

    private void closeFullscreen() {
        isFullScreen = false;
        ExoKangjiNew.getSharedInstance().switchScreen(mPlayerViewGedhi, mPlayerViewCilik);
        initFullscreenButton(mPlayerViewCilik);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        dialogFullscreen.dismiss();
    }

}
