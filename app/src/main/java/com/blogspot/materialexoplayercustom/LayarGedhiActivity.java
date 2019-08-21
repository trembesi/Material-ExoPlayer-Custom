package com.blogspot.materialexoplayercustom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.blogspot.materialexoplayercustom.player.ExoKangji;
import com.blogspot.materialexoplayercustom.player.ConfigPlayerKangji;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;

public class LayarGedhiActivity extends AppCompatActivity {

    private String TAG = "=== " + LayarGedhiActivity.class.getSimpleName() + " ===";

    private boolean destroyVideo = true;
    private PlayerView mPlayerViewGedhi;
    private ImageView ivFullscreen;
    private FrameLayout btnFrameFullscreen;
    private String videoLink;
    private Handler handlerNdelikneSystemUI;
    private int WEKTU_UI_ILANG = 5000;

    private ProgressBar pbBuffer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_layar_gedhi);
        //njupukData();
        //notoLayarGuedhi();
        //pbBuffer = findViewById(R.id.lg_pb_buffer);
        //ExoKangji.getSharedInstance().persiapanExoPlayer(LayarGedhiActivity.this, mPlayerViewGedhi, videoLink, pbBuffer);
        //initFullscreenButton();
    }

    @Override
    protected void onResume(){
        super.onResume();

        njupukData();
        notoLayarGuedhi();
        ExoKangji.getSharedInstance().persiapanExoPlayer(LayarGedhiActivity.this, mPlayerViewGedhi, videoLink, pbBuffer);
        ExoKangji.getSharedInstance().goToForeground();
        initFullscreenButton();
        ivFullscreen.setImageResource(R.drawable.ic_fullscreen_skrink);

        pbBuffer = findViewById(R.id.lg_pb_buffer);
        mPlayerViewGedhi.getPlayer().addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {
                    case Player.STATE_BUFFERING:
                        Log.d(TAG, "(= STATUS PLAYBACK =) - Buffering...");
                        pbBuffer.setVisibility(View.VISIBLE);
                        break;
                    case Player.STATE_ENDED:
                        Log.d(TAG, "(= STATUS PLAYBACK =) - Ended...");
                        // Activate the force enable
                        pbBuffer.setVisibility(View.INVISIBLE);
                        break;
                    case Player.STATE_IDLE:
                        Log.d(TAG, "(= STATUS PLAYBACK =) - Idle...");
                        break;
                    case Player.STATE_READY:
                        Log.d(TAG, "(= STATUS PLAYBACK =) - Ready...");
                        //dialogBuffer.cancel();
                        pbBuffer.setVisibility(View.INVISIBLE);
                        break;
                    default:
                        // status = PlaybackStatus.IDLE;
                        break;
                }
            }
        });

        //Toast.makeText(this, TAG + " - " + "ON RESUME" + "\n" + "videoLink: " + videoLink, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed(){
        destroyVideo = false;
        super.onBackPressed();
        hideSystemUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ExoKangji.getSharedInstance().goToBackground();
        //Toast.makeText(this, TAG + " - " + "ON PAUSE", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(destroyVideo){
            ExoKangji.getSharedInstance().releasePlayer();
            //Toast.makeText(this, TAG + " - " + "ON DESTROY - RELEASE PLAYER", Toast.LENGTH_SHORT).show();
        }
        else {
            //Toast.makeText(this, TAG + " - " + "ON DESTROY", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }


    private void notoLayarGuedhi() {
        getSupportActionBar().setTitle(TAG);
        handlerNdelikneSystemUI = new Handler();
        mPlayerViewGedhi = findViewById(R.id.lg_player_view_gedhi);
        mPlayerViewGedhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tampilTimerSystemUI();
            }
        });
    }

    private void njupukData() {
        if (getIntent().hasExtra(ConfigPlayerKangji.KEY_VIDEO_URI)) {
            videoLink = getIntent().getStringExtra(ConfigPlayerKangji.KEY_VIDEO_URI);
            Log.d("==LAPORAN==", "videoLink: " + videoLink);
        }
    }

    private void initFullscreenButton() {
        PlayerControlView controlView = mPlayerViewGedhi.findViewById(R.id.exo_controller);
        ivFullscreen = controlView.findViewById(R.id.exo_fullscreen_icon);
        btnFrameFullscreen = controlView.findViewById(R.id.exo_fullscreen_button);
        btnFrameFullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destroyVideo = false;
                finish();
            }
        });
    }

    private Runnable runNdelikneSystemUI = new Runnable() {
        @Override
        public void run() {
            hideSystemUI();
            handlerNdelikneSystemUI.removeCallbacks(runNdelikneSystemUI);
        }
    };

    private void ndelikneSistemUI() {
        handlerNdelikneSystemUI.postDelayed(runNdelikneSystemUI, (long) WEKTU_UI_ILANG);
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
        //Toast.makeText(LayarTancepActivity.this, "WAYAHE NDELIK", Toast.LENGTH_SHORT).show();
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private void tampilTimerSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        ndelikneSistemUI();
    }

}
