package com.blogspot.materialexoplayercustom.onstandart;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.materialexoplayercustom.ConfigMedia;
import com.blogspot.materialexoplayercustom.KangjiRvDividerItemDecoration;
import com.blogspot.materialexoplayercustom.KangjiSaringanTahu;
import com.blogspot.materialexoplayercustom.R;
import com.blogspot.materialexoplayercustom.player.ConfigPlayerKangji;
import com.blogspot.materialexoplayercustom.player.ExoKangjiNew;
import com.blogspot.materialexoplayercustom.tls.MeksoTLS;
import com.blogspot.materialexoplayercustom.yt_extractor_naveedhassan913.ExtractorException;
import com.blogspot.materialexoplayercustom.yt_extractor_naveedhassan913.YoutubeStreamExtractor;
import com.blogspot.materialexoplayercustom.yt_extractor_naveedhassan913.model.YTMedia;
import com.blogspot.materialexoplayercustom.yt_extractor_naveedhassan913.model.YoutubeMeta;
import com.blogspot.materialexoplayercustom.yt_extractor_naveedhassan913.utils.LogUtils;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

public class PlayOnStandartActivity extends AppCompatActivity implements StandartAdapter.StandartAdapterListener, KangjiSaringanTahu.KangjiSaringanTahuListener {

    private String TAG = "=== " + PlayOnStandartActivity.class.getSimpleName() + " ===";
    private EditText etURL;
    private Button btnPlayURL;
    private RecyclerView rvStandart;
    private ProgressBar pbStandart;
    private TextView tvKontenStatus;
    //private boolean isYTSource;
    private List<StandartItem> standartList = new ArrayList<>();
    private StandartItem standartItem;
    private StandartAdapter adapter;
    private URI xURI;
    private String scheme;
    private String mediaGambar, mediaJudul, mediaDeskripsi, mediaVideoPath;

    private String gembokGeni;
    private int maxResult, visibleThreshold;
    private JSONArray arrayYTHost;


    // =========== layar tancep ==========
    private PlayerView mPlayerViewCilik, mPlayerViewGedhi;
    //private String inputSource, outputSource;
    private ImageView ivFullscreen;
    private FrameLayout btnFrameFullscreen;
    private boolean isFullScreen = false;
    private Dialog dialogFullscreen;
    private TextView tvError;
    private Drawable defaultArtWork;

    // ========== ExoPlayer ==========
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    //private static ExoKangjiNew mInstance = null;
    private SimpleExoPlayer mPlayer;
    private Uri mUri;
    ArrayList<String> mPlayList = null;
    Integer playlistIndex = 0;

    //PlayerView mPlayerView;
    //private String userAgent = "ExoKangji";
    private String fileExtUppercase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_on_standart);

        MeksoTLS meksoTLS = new MeksoTLS(this);
        meksoTLS.peksoTLSv12();

        notoStandart();
        muatDataMedia();

    }

    @Override
    public void onDestroy() {
        ExoKangjiNew.getSharedInstance().releasePlayer();
        //releasePlayer();
        super.onDestroy();
    }

    @Override
    public void onStandartItemSelected(StandartItem item) {
        //Toast.makeText(PlayOnStandartActivity.this, item.getJudul() + "\n" + item.getVideoPath(), Toast.LENGTH_SHORT).show();
        //saringanTahu(item.getVideoPath());
        //ekstrakManggisNaveedHassan931(item.getVideoPath());
        new KangjiSaringanTahu(this, item.getVideoPath(), this::onHasilSaringan);
    }

    @Override
    public void onHasilSaringan(String outputSource, boolean isYTSource) {
        Log.e(TAG, "onHasilSaringan: " + "\n" + "outputSource: " + outputSource + "\n" + "isYTSource: " + isYTSource);
        ExoKangjiNew.getSharedInstance().playContent(outputSource, isYTSource);
    }

    private void notoStandart() {

        pbStandart = findViewById(R.id.standart_pb_konten);
        pbStandart.setVisibility(View.GONE);
        tvKontenStatus = findViewById(R.id.standart_tv_konten_status);
        tvKontenStatus.setVisibility(View.GONE);

        standartList = new ArrayList<>();

        rvStandart = findViewById(R.id.standart_rv);
        rvStandart.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvStandart.setLayoutManager(llm);
        rvStandart.setItemAnimator(new DefaultItemAnimator());
        rvStandart.addItemDecoration(new KangjiRvDividerItemDecoration(PlayOnStandartActivity.this, DividerItemDecoration.VERTICAL, 50));

        adapter = new StandartAdapter(PlayOnStandartActivity.this, standartList, this::onStandartItemSelected);
        //rvStandart.setAdapter(adapter);

        // noto playerview
        defaultArtWork = getResources().getDrawable(R.drawable.ic_menu_rate);
        mPlayerViewCilik = findViewById(R.id.sorot2_layar_cilik);
        //initFullscreenButton(mPlayerViewCilik);
        //ivFullscreen.setImageResource(R.drawable.ic_fullscreen_expand);
        tvError = findViewById(R.id.sorot2_tv_error_layar);
        tvError.setVisibility(View.INVISIBLE);

        ExoKangjiNew.getSharedInstance().initializePlayer(PlayOnStandartActivity.this, mPlayerViewCilik, null, defaultArtWork);
        //playerListener(mPlayerViewCilik);

    }

    private String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("media_standart.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    private void muatDataMedia() {

        pbStandart.setVisibility(View.VISIBLE);
        tvKontenStatus.setVisibility(View.GONE);

        try {
            pbStandart.setVisibility(View.GONE);

            JSONObject jsonObject = new JSONObject(loadJSONFromAsset());
            JSONArray jsonArray = jsonObject.getJSONArray(ConfigMedia.ARRAY_RESPONSE);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);

                JSONArray jsonArrayDataMedia = object.getJSONArray(ConfigMedia.ARRAY_DATA_MEDIA);
                for (int idm = 0; idm < jsonArrayDataMedia.length(); idm++) {
                    JSONObject jsonObjectDataMedia = jsonArrayDataMedia.getJSONObject(idm);
                    mediaGambar = jsonObjectDataMedia.getString(ConfigMedia.OBJECT_MEDIA_GAMBAR);
                    mediaJudul = jsonObjectDataMedia.getString(ConfigMedia.OBJECT_MEDIA_JUDUL);
                    mediaDeskripsi = jsonObjectDataMedia.getString(ConfigMedia.OBJECT_MEDIA_DESKRIPSI);
                    mediaVideoPath = jsonObjectDataMedia.getString(ConfigMedia.OBJECT_MEDIA_VIDEO_PATH);

                    standartItem = new StandartItem(mediaGambar, mediaJudul, mediaDeskripsi, mediaVideoPath);
                    standartList.add(standartItem);

                    Log.e(TAG, jsonObjectDataMedia.toString());
                }

                Log.e(TAG, object.toString());
            }
            rvStandart.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        catch (JSONException e) {
            pbStandart.setVisibility(View.GONE);
            tvKontenStatus.setText("ERROR");
            tvKontenStatus.setVisibility(View.VISIBLE);
            e.printStackTrace();
        }

    }



    // =============================================================
    // ======================== P L A Y E R ========================
    // =============================================================
    /*
    public boolean isHasNext() {
        if (isSourceFromSaved) {
            if (currentPos < adapterSaved.getItemCount()-1) {
                return true;
            }
        }
        else {
            if (currentPos < adapterYTStok.getItemCount()-1) {
                return true;
            }
        }

        return false;
    }

    public void playNextVideo() {
        if (isHasNext()) {
            currentPos = currentPos + 1;

            if (isSourceFromSaved) {
                adapterSaved.toggleSelection(currentPos);
                rvKonten.scrollToPosition(currentPos);
                SavedItem savedItemNext = savedList.get(currentPos);
                setJudulToolbar(savedItemNext.get_title(), (currentPos+1) + "/" + kangjiSQLiteDatabase.getDataCount());
                ekstrakManggis(KangjiYT.URL_BASE_YT_VIDEO + savedItemNext.get_url());
            }
            else {
                adapterYTStok.toggleSelection(currentPos);
                rvKonten.scrollToPosition(currentPos);
                YTItem ytItemNext = tempYTList.get(currentPos);
                setJudulToolbar(ytItemNext.getJudulVideo(), (currentPos+1) + "/" + totalResults);
                ekstrakManggis(KangjiYT.URL_BASE_YT_VIDEO + ytItemNext.getIdVideo());
            }

        }
        //Log.e(TAG, "isHasNext: " + isHasNext());
    }


    private void playIntro() {
        int introMustRun = 3; // kudu mlaku ping piro: 0 - x
        int introHasBeenRun = getIntroHasBeenRunned();
        isNowPlayIntro = true;

        if (introMustRun >= introHasBeenRun) {
            //ekstrakManggis(ConfigPlayerKangji.INTRO);
            ekstrakManggis(osengOseng.getIntroVideoUrl());
            simpanIntroHasBeenRunned(introHasBeenRun + 1);
        }
        else {
            if (isPlayIntro) {
                //ekstrakManggis(ConfigPlayerKangji.INTRO);
                ekstrakManggis(osengOseng.getIntroVideoUrl());
            }
        }
    }

    private void simpanIntroHasBeenRunned(int count) {
        editorMyPref.putInt(SetelActivity.PREF_KEY_INTRO_FIRST_RUN, count);
        editorMyPref.commit();
        Log.e(TAG, "SIMPEN INTRO HAS BEEN RUNNED - COUNT: " + count);
    }

    private int getIntroHasBeenRunned() {
        int count = 0;
        if (myPref.contains(SetelActivity.PREF_KEY_INTRO_FIRST_RUN)) {
            count = myPref.getInt(SetelActivity.PREF_KEY_INTRO_FIRST_RUN, 0);
        }
        Log.e(TAG, "INTRO HAS BEEN RUNNED: " + count);
        return count;
    }

     */
/*
    public void saringanTahu(String linkSource) {

        inputSource = linkSource;
        try {
            xURI = new URI(inputSource);
            scheme = xURI.getScheme().toLowerCase();
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
                ExoKangjiNew.getSharedInstance().playContent(outputSource, false);
                break;
            }

            default: {

                try {
                    URL url = new URL(inputSource);
                    String baseUrl = url.getHost().toLowerCase();
                    if (baseUrl.equals(ConfigPlayerKangji.YT_BASE_URL_1) ||
                            baseUrl.equals(ConfigPlayerKangji.YT_BASE_URL_2) ||
                            baseUrl.equals(ConfigPlayerKangji.YT_BASE_URL_3)) {

                        new YouTubeExtractor(this) {
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
                                                ExoKangjiNew.getSharedInstance().playContent(outputSource, true);
                                                //return;
                                            }
                                        }
                                    }
                                }
                                else {
                                    Log.e(TAG, "ekstrakManggisHaarigerHarald: ZONK");
                                    ekstrakManggisNaveedHassan931(linkSource);
                                }

                            }
                        }.extract(linkSource, true, true);

                    }
                    else {
                        // LANGSUNG VIDEO LINK, GAK PERLU EXTRACTOR
                        outputSource = inputSource;
                        ExoKangjiNew.getSharedInstance().playContent(outputSource, false);
                        Log.e(TAG, outputSource);
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
                    ExoKangjiNew.getSharedInstance().playContent(url, true);
                    Log.e(TAG, "ekstrakManggisNaveedHassan931 - muxedStream URL: " + url);
                }




            }
        }).Extract(linkSource);
    }

 */
    /*
    private void initFullscreenButton(PlayerView playerView) {
        PlayerControlView controlView = playerView.findViewById(R.id.exo_controller);

        ivFullscreen = controlView.findViewById(R.id.exo_fullscreen_icon);
        ivFullscreen.setImageResource(R.drawable.ic_fullscreen_expand);

        btnFrameFullscreen = controlView.findViewById(R.id.exo_fullscreen_button);
        btnFrameFullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //toggleFullScreen();

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

        isFullScreen = true;

        dialogFullscreen = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            public void onBackPressed() {
                if (isFullScreen) {
                    closeFullscreen();
                }
                super.onBackPressed();
            }
        };

        dialogFullscreen.setContentView(R.layout.fullscreen_player);
        mPlayerViewGedhi = dialogFullscreen.findViewById(R.id.fs_layar_tancep_full);

        ExoKangjiNew.getSharedInstance().switchScreen(mPlayerViewCilik, mPlayerViewGedhi);
        //mPlayerViewGedhi.getPlayer().getVideoComponent().clearVideoSurface();
        //mPlayerViewGedhi.getPlayer().getVideoComponent().setVideoSurfaceView((SurfaceView) mPlayerViewGedhi.getVideoSurfaceView());
        //playerListener(mPlayerViewGedhi);

        initFullscreenButton(mPlayerViewGedhi);
        ivFullscreen.setImageResource(R.drawable.ic_fullscreen_shrink);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        dialogFullscreen.show();
    }

    private void closeFullscreen() {

        isFullScreen = false;

        ExoKangjiNew.getSharedInstance().switchScreen(mPlayerViewGedhi, mPlayerViewCilik);
        //playerListener(mPlayerViewCilik);

        initFullscreenButton(mPlayerViewCilik);
        ivFullscreen.setImageResource(R.drawable.ic_fullscreen_expand);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        dialogFullscreen.dismiss();
    }

     */

    private void screenOnFlag() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void screenOffFlag() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void playerListener(PlayerView playerView) {
        playerView.getPlayer().addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {
                Log.i(TAG, "onTimelineChanged: ");
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                Log.i(TAG, "onTracksChanged: ");
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                Log.i(TAG, "onLoadingChanged: ");
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {
                    case Player.STATE_BUFFERING:
                        Log.e(TAG, "(= STATUS PLAYBACK =) - Buffering...");
                        tvError.setVisibility(View.GONE);
                        screenOffFlag();
                        //if (progressBarBuffering != null) {
                        //    progressBarBuffering.setVisibility(View.VISIBLE);
                        //}
                        break;
                    case Player.STATE_ENDED:
                        Log.e(TAG, "(= STATUS PLAYBACK =) - Ended...");
                        tvError.setVisibility(View.GONE);

                        if (playerView.isControllerVisible()) {
                            playerView.hideController();
                        }
                        /*
                        if (isNowPlayIntro) {
                            isNowPlayIntro = false;
                        }
                        else {
                            playNextVideo();
                        }
                         */


                        screenOffFlag();

                        // Activate the force enable
                        //if (progressBarBuffering != null) {
                        //    progressBarBuffering.setVisibility(View.GONE);}
                        break;
                    case Player.STATE_IDLE:
                        Log.e(TAG, "(= STATUS PLAYBACK =) - Idle...");
                        if (playerView.isControllerVisible()) {
                            playerView.hideController();
                        }
                        screenOffFlag();
                        //tvError.setVisibility(View.GONE);
                        break;
                    case Player.STATE_READY:
                        Log.e(TAG, "(= STATUS PLAYBACK =) - Ready...");
                        tvError.setVisibility(View.GONE);
                        screenOnFlag();
                        //dialogBuffer.cancel();
                        //if (progressBarBuffering != null) {
                        //    progressBarBuffering.setVisibility(View.GONE);}
                        break;
                    default:
                        // status = PlaybackStatus.IDLE;
                        break;
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {
                Log.i(TAG, "onRepeatModeChanged: ");
            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
                Log.i(TAG, "onShuffleModeEnabledChanged: ");
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.i(TAG, "onPlayerError: ");
                //tvError.setVisibility(View.VISIBLE);

                switch (error.type) {
                    case ExoPlaybackException.TYPE_SOURCE:
                        tvError.setText("Error: TYPE_SOURCE");
                        tvError.setVisibility(View.VISIBLE);
                        if (playerView.isControllerVisible()) {
                            playerView.hideController();
                        }
                        Log.e(TAG, "TYPE_SOURCE: " + error.getSourceException().getMessage());
                        break;

                    case ExoPlaybackException.TYPE_RENDERER:
                        tvError.setText("Error: TYPE_RENDERER");
                        tvError.setVisibility(View.VISIBLE);
                        if (playerView.isControllerVisible()) {
                            playerView.hideController();
                        }
                        Log.e(TAG, "TYPE_RENDERER: " + error.getRendererException().getMessage());
                        break;

                    case ExoPlaybackException.TYPE_UNEXPECTED:
                        tvError.setText("Error: TYPE_UNEXPECTED");
                        tvError.setVisibility(View.VISIBLE);
                        if (playerView.isControllerVisible()) {
                            playerView.hideController();
                        }
                        Log.e(TAG, "TYPE_UNEXPECTED: " + error.getUnexpectedException().getMessage());
                        break;
                }
            }

            @Override
            public void onPositionDiscontinuity(int reason) {
                Log.i(TAG, "onPositionDiscontinuity: ");
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                Log.i(TAG, "onPlaybackParametersChanged: ");
            }

            @Override
            public void onSeekProcessed() {
                Log.i(TAG, "onSeekProcessed: ");
            }
        });
    }




}
