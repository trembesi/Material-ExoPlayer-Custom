package com.blogspot.materialexoplayercustom;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
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
//import com.commit451.youtubeextractor.YouTubeExtractionResult;
//import com.commit451.youtubeextractor.YouTubeExtractor;
import com.commit451.youtubeextractor.YouTubeExtractionResult;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewPlayerActivity extends AppCompatActivity {

    private String TAG = "=== " + NewPlayerActivity.class.getSimpleName() + " ===";

    private PlayerView mPlayerViewCilik, mPlayerViewGedhi;
    private Button btnTest1, btnTest2, btnTest3, btnTest4, btnTest5, btnTest6, btnTest7, btnTest8;
    private String inputSource, outputSource;
    private EditText etInputLink;
    private Button btnGoPlay;

    private ImageView ivFullscreen;
    private FrameLayout btnFrameFullscreen;
    private boolean isFullScreen = false;
    private Dialog dialogFullscreen;

    private URI xURI;
    private String scheme;

    /*
    private final YouTubeExtractor mExtractor = YouTubeExtractor.create();
    private Callback<YouTubeExtractionResult> mExtractionCallback = new Callback<YouTubeExtractionResult>() {
        @Override
        public void onResponse(Call<YouTubeExtractionResult> call, Response<YouTubeExtractionResult> response) {
            //Log.d(TAG, response.body().toString());
            yt451BindVideoResult(response.body());
        }

        @Override
        public void onFailure(Call<YouTubeExtractionResult> call, Throwable t) {
            yt451OnError(t);
        }
    };

     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_new_player);
        MeksoTLS meksoTLS = new MeksoTLS(this);
        meksoTLS.peksoTLSv12();
        requestPermission();
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
        etInputLink.setText("http://cdn0-a.production.vidio.static6.com/uploads/1567393/ets-bumb33-6048-b900.mp4.m3u8");
        btnGoPlay = findViewById(R.id.btn_go);
        btnGoPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etInputLink.getText().toString().trim().isEmpty()) {
                    Toast.makeText(NewPlayerActivity.this, "EMPTY INPUT", Toast.LENGTH_SHORT).show();
                }
                else {
                    //ekstrakManggis(etInputLink.getText().toString().trim());
                }
            }
        });

        btnTest1 = findViewById(R.id.new_lc_btn_test_1);
        btnTest1.setText("Test Streaming ANTV");
        btnTest1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputSource = "http://202.80.222.170/000001/2/ch14061215034900095272/index.m3u8?virtualDomain=000001.live_hls.zte.com";
                //ekstrakManggis(inputSource);
            }
        });

        btnTest2 = findViewById(R.id.new_lc_btn_test_2);
        btnTest2.setText("Test Streaming AhsanTV");
        btnTest2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputSource = "http://119.82.224.75:1935/live/ahsantv/playlist.m3u8";
                //ekstrakManggis(inputSource);
            }
        });

        btnTest3 = findViewById(R.id.new_lc_btn_test_3);
        btnTest3.setText("Test Content YouTube 1");
        btnTest3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //inputSource = "https://www.youtube.com/watch?v=SXIGXSrwygU";
                inputSource = "https://www.youtube.com/watch?v=uzKqylx4Cqs";
                //ekstrakYTHuber(inputSource);
                ekstrakManggis(inputSource);
                //mExtractor.extract("uzKqylx4Cqs").enqueue(mExtractionCallback);
            }
        });

        btnTest4 = findViewById(R.id.new_lc_btn_test_4);
        btnTest4.setText("Test Content YouTube 2");
        btnTest4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputSource = "https://www.youtube.com/watch?v=uykAHRDaZH8&t";
                //ekstrakYTHuber(inputSource);
                ekstrakManggis(inputSource);
                //mExtractor.extract("ea4-5mrpGfE").enqueue(mExtractionCallback);
            }
        });

        btnTest5 = findViewById(R.id.new_lc_btn_test_5);
        btnTest5.setVisibility(View.GONE);
        btnTest5.setText("RTMP TVMU");
        btnTest5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputSource = "rtmp://118.97.183.222/discover/muhammadiyahtv";
                ekstrakManggis(inputSource);
            }
        });


        btnTest6 = findViewById(R.id.new_lc_btn_test_6);
        btnTest6.setText("Test Local Asset Folder - MP4");
        btnTest6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //inputSource = "https://cdn0-a.production.vidio.static6.com/uploads/1655223/ets-10-20uripedekne-d2e7-b1600.mp4.m3u8";
                Uri uriInputSource = Uri.parse("asset:///jogja_istimewa.MP4");
                //ExoKangjiNew.getSharedInstance().playLocalContent(uriInputSource);
                ExoKangjiNew.getSharedInstance().playContent(uriInputSource.toString());
            }
        });

        btnTest7 = findViewById(R.id.new_lc_btn_test_7);
        btnTest7.setText("Test Local Asset Folder - MP3");
        btnTest7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //inputSource = "file:///storage/emulated/0/DATAKU%20TEST/Burdah%20Ensemble%20-%20Pearls%20and%20Coral%20%E2%80%93%2013.%20Al%20Madad%20Ya%20Rasul%20Allah.mp3";
                Uri uriInputSource = Uri.parse("asset:///orek_orek.mp3");
                //ExoKangjiNew.getSharedInstance().playLocalContent(uriInputSource);
                ExoKangjiNew.getSharedInstance().playContent(uriInputSource.toString());
            }
        });

        btnTest8 = findViewById(R.id.new_lc_btn_test_8);
        btnTest8.setVisibility(View.GONE);
        btnTest8.setText("Local Storage Content - MP4");
        btnTest8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputSource = "file:///storage/emulated/0/DATAKU%20TEST/Nella%20Kharisma%20-%20Sayang%202%20(New%20Arista).mp4";
                ExoKangjiNew.getSharedInstance().playContent(inputSource);
            }
        });

        mPlayerViewCilik = findViewById(R.id.new_lc_player_view_cilik);
        // use default ArtWork
        //mPlayerViewCilik.setDefaultArtwork(getResources().getDrawable(R.drawable.ic_menu_rate));
        initFullscreenButton(mPlayerViewCilik);
        ivFullscreen.setImageResource(R.drawable.ic_fullscreen_expand);
        ExoKangjiNew.getSharedInstance().initializePlayer(NewPlayerActivity.this, mPlayerViewCilik, null, getResources().getDrawable(R.drawable.ic_menu_rate));

        //play intro video
        //ekstrakManggis(ConfigPlayerKangji.INTRO);
    }

    // ========= YTExtractor 451 ==============
    private void yt451OnError(Throwable t) {
        t.printStackTrace();
        Toast.makeText(NewPlayerActivity.this, "ERROR EXTRACTION BROOOO", Toast.LENGTH_SHORT).show();
    }
    private void yt451BindVideoResult(YouTubeExtractionResult result) {
        Log.d(TAG, "onSuccess - Got a result with the best url: " + result.getBestAvailableQualityVideoUri());
        //Toast.makeText(NewPlayerActivity.this, "result: " + result.getSd360VideoUri(), Toast.LENGTH_LONG).show();
    }
    // ========= YTExtractor 451 ==============

    /*
    // ========== YTHuber racikan ampuh =========
    private void ekstrakYTHuber(String ytUrl) {
        new YouTubeExtractor(this) {
            @Override
            protected void onExtractionComplete(SparseArray<YtFile> files, VideoMeta videoMeta) {
                if (files != null) {
                    // 720, 1080, 480
                    List<Integer> iTags = Arrays.asList(22, 137, 18);
                    for (Integer iTag : iTags) {
                        YtFile ytFile = files.get(iTag);
                        if (ytFile != null) {
                            String downloadUrl = ytFile.getUrl();
                            if (downloadUrl != null && !downloadUrl.isEmpty()) {
                                Log.d(TAG, downloadUrl);
                                ExoKangjiNew.getSharedInstance().playContent(downloadUrl);
                                //return;
                            }
                        }
                    }
                }
            }
        }.extract(ytUrl, true, true);
    }
     */

    private void ekstrakManggis(String ytVideoUrl) {
        inputSource = ytVideoUrl;

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
                ExoKangjiNew.getSharedInstance().playContent(outputSource);
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
                                /*
                                if (ytFiles != null) {
                                    int itag = 22;
                                    outputSource = ytFiles.get(itag).getUrl();
                                    ExoKangjiNew.getSharedInstance().initializePlayer(NewPlayerActivity.this, mPlayerViewCilik, null, getResources().getDrawable(R.drawable.ic_menu_rate));
                                    ExoKangjiNew.getSharedInstance().playContent(outputSource);
                                    Log.d("== PLAY VIDEO ==", ytFiles.get(itag).getUrl());
                                }
                                 */
                                if (ytFiles != null) {
                                    // 720, 1080, 480
                                    List<Integer> iTags = Arrays.asList(22, 137, 18);
                                    for (Integer iTag : iTags) {
                                        YtFile ytFile = ytFiles.get(iTag);
                                        if (ytFile != null) {
                                            String downloadUrl = ytFile.getUrl();
                                            if (downloadUrl != null && !downloadUrl.isEmpty()) {
                                                Log.d(TAG, downloadUrl);
                                                ExoKangjiNew.getSharedInstance().playContent(downloadUrl);
                                                //return;
                                            }
                                        }
                                    }
                                }
                            }
                        }.extract(inputSource, true, true);

                    } else {
                        // LANGSUNG VIDEO LINK, GAK PERLU EXTRACTOR
                        outputSource = inputSource;
                        ExoKangjiNew.getSharedInstance().playContent(outputSource);
                        Log.d("== PLAY VIDEO ==", outputSource);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Log.e("==EXTRAK MANGGIS==", e.toString());
                }
            }


        }

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
        // use default ArtWork
        //mPlayerViewGedhi.setDefaultArtwork(getResources().getDrawable(R.drawable.ic_menu_rate));

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


    private void requestPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.INTERNET,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                // check if all permissions are granted
                if (report.areAllPermissionsGranted()) {
                    // do you work now
                    notoNewLayarCilik();
                }

                // check for permanent denial of any permission
                if (report.isAnyPermissionPermanentlyDenied()) {
                    // permission is denied permenantly, navigate user to app settings
                    showSettingsDialog();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).withErrorListener(new PermissionRequestErrorListener() {
            @Override
            public void onError(DexterError error) {
                Toast.makeText(getApplicationContext(), "error: \n" + error.toString(), Toast.LENGTH_LONG).show();
            }
        }).onSameThread()
                .check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(NewPlayerActivity.this);
        builder.setTitle(getResources().getString(R.string.ijin_judul));
        builder.setMessage(getResources().getString(R.string.ijin_pesan));
        builder.setPositiveButton("SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dialog.cancel();
                dialog.dismiss();
                bukakSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dialog.cancel();
                dialog.dismiss();
            }
        });
        builder.show();
    }

    // navigating user to app settings
    private void bukakSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }


}
