package com.blogspot.materialexoplayercustom.player;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

//import com.blogspot.materialexoplayercustom.R;
import com.blogspot.materialexoplayercustom.R;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class ExoKangji {
    private String TAG = "=== " + ExoKangji.class.getSimpleName() + " ===";
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private DataSource.Factory dataSourceFactory;
    private static ExoKangji mInstance = null;
    private SimpleExoPlayer mPlayer;
    private boolean isPlayerPlaying;
    private Uri mUri;
    private CallBacks.playerCallBack listner;
    private ArrayList<String> mPlayList = null;
    private Integer playlistIndex = 0;
    private String uriString = "";

    private AlertDialog dialogBuffer;
    //private ProgressBar pbBuffering;
    //private RelativeLayout layoutPB;
    private PlayerView mPlayerView;
    private Context mContext;


    public static ExoKangji getSharedInstance() {
        if (mInstance == null) {
            mInstance = new ExoKangji();
        }
        return mInstance;
    }

    public void persiapanExoPlayer(Context mContext, PlayerView mPlayerView, String videoUriString, @Nullable ProgressBar progressBarBuffering) {

        this.mContext = mContext;
        this.mPlayerView = mPlayerView;
        /*
        MeksoTLS meksoTLS = new MeksoTLS(mContext);
        meksoTLS.peksoTLSv12();
        */

        if (mContext == null || mPlayerView == null) {
            return;
        }
        if (mPlayer == null) {
            // Create a new player if the player is null or
            // we want to play a new video

            // Do all the standard ExoPlayer code here...
            // 1. Create a default TrackSelector
            LoadControl loadControl = new DefaultLoadControl(
                    new DefaultAllocator(true, 16),
                    ConfigPlayerKangji.MIN_BUFFER_DURATION,
                    ConfigPlayerKangji.MAX_BUFFER_DURATION,
                    ConfigPlayerKangji.MIN_PLAYBACK_START_BUFFER,
                    ConfigPlayerKangji.MIN_PLAYBACK_RESUME_BUFFER,
                    -1,
                    true);

            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
            TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

            // 2. Create the player
            mPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector/*, loadControl*/);

            // 3 NGGAWE MEDIA SOURCE (create a media source)
            if (videoUriString == null) {
                return;
            }
            mUri = Uri.parse(videoUriString);
            // Produces DataSource instances through which media data is loaded.
            dataSourceFactory = new DefaultDataSourceFactory(mContext, Util.getUserAgent(mContext, mContext.getString(R.string.app_name)), BANDWIDTH_METER);
            // This is the MediaSource representing the media to be played.
            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(mUri);

            //== OPO IKI YAAA ====
            String filenameArray[] = videoUriString.split("\\.");
            if (videoUriString.toUpperCase().contains("M3U8")) {
                //videoSource = new HlsMediaSource(mUri, dataSourceFactory, null, null);
                videoSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(mUri);
            }
            else {
                videoSource = new ExtractorMediaSource(mUri, dataSourceFactory, new DefaultExtractorsFactory(), null, null);
            }

            // Prepare the player with the source.
            mPlayer.prepare(videoSource);

            //init buffering
            //bufferInitialize(mContext);

            // Add Player Listener
            //exoPlayerListener();
            mPlayer.addListener(new Player.EventListener() {
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
                            Log.d(TAG, "(= STATUS PLAYBACK =) - Buffering...");
                            if (progressBarBuffering != null) {
                                progressBarBuffering.setVisibility(View.VISIBLE);
                            }
                            break;
                        case Player.STATE_ENDED:
                            Log.d(TAG, "(= STATUS PLAYBACK =) - Ended...");
                            // Activate the force enable
                            if (progressBarBuffering != null) {
                                progressBarBuffering.setVisibility(View.GONE);}
                            break;
                        case Player.STATE_IDLE:
                            Log.d(TAG, "(= STATUS PLAYBACK =) - Idle...");
                            break;
                        case Player.STATE_READY:
                            Log.d(TAG, "(= STATUS PLAYBACK =) - Ready...");
                            //dialogBuffer.cancel();
                            if (progressBarBuffering != null) {
                                progressBarBuffering.setVisibility(View.GONE);}
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

        mPlayer.clearVideoSurface();
        //mPlayer.setVideoSurfaceView((SurfaceView) mPlayerView.getVideoSurfaceView());
        mPlayer.setVideoTextureView((TextureView) mPlayerView.getVideoSurfaceView());
        mPlayer.seekTo(mPlayer.getCurrentPosition() + 1);

        mPlayerView.setUseController(true);
        mPlayerView.setPlayer(mPlayer);
    }

    public void changeAndPlayStreaming(String linkStreaming) {
        mPlayer.stop();
        mPlayer.seekTo(0L);
        mPlayer.clearVideoSurface();
        mPlayer.setVideoTextureView((TextureView) mPlayerView.getVideoSurfaceView());

        // 3 NGGAWE MEDIA SOURCE (create a media source)
        if (linkStreaming == null) {
            return;
        }
        mUri = Uri.parse(linkStreaming);
        // Produces DataSource instances through which media data is loaded.
        dataSourceFactory = new DefaultDataSourceFactory(mContext, Util.getUserAgent(mContext, mContext.getString(R.string.app_name)), BANDWIDTH_METER);
        // This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(mUri);

        //== OPO IKI YAAA ====
        String filenameArray[] = linkStreaming.split("\\.");
        if (linkStreaming.toUpperCase().contains("M3U8")) {
            //videoSource = new HlsMediaSource(mUri, dataSourceFactory, null, null);
            videoSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(mUri);
        }
        else {
            videoSource = new ExtractorMediaSource(mUri, dataSourceFactory, new DefaultExtractorsFactory(), null, null);
        }

        // Prepare the player with the source.
        mPlayer.prepare(videoSource);
    }
/*
    public void exoPlayerListener() {

        mPlayer.addListener(new Player.EventListener() {
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
                        Log.d(TAG, "(= STATUS PLAYBACK =) - Buffering...");
                        bufferTampil();
                        break;
                    case Player.STATE_ENDED:
                        Log.d(TAG, "(= STATUS PLAYBACK =) - Ended...");
                        // Activate the force enable
                        bufferAmblas();
                        break;
                    case Player.STATE_IDLE:
                        Log.d(TAG, "(= STATUS PLAYBACK =) - Idle...");
                        break;
                    case Player.STATE_READY:
                        Log.d(TAG, "(= STATUS PLAYBACK =) - Ready...");
                        //dialogBuffer.cancel();
                        bufferAmblas();
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
*/
    public void releasePlayer() {
        if (mPlayer != null) {
            mPlayer.release();
        }
        mPlayer = null;
    }

    public void goToBackground() {
        if (mPlayer != null) {
            isPlayerPlaying = mPlayer.getPlayWhenReady();
            mPlayer.setPlayWhenReady(false);
        }
    }

    public void goToForeground() {
        if (mPlayer != null) {
            mPlayer.setPlayWhenReady(isPlayerPlaying);
            bufferInitialize(mContext);
        }
    }

    public void delikneController(PlayerView mPlayerView) {
        mPlayerView.hideController();
    }

    public void tampilController(PlayerView mPlayerView) {
        mPlayerView.showController();
    }
/*
    public void notoPBBuffering() {
        //============ PROGRESSBAR START =============

        RelativeLayout layoutPB = new RelativeLayout(ctx);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50,50);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        pbBuffering = new ProgressBar(ctx, null, android.R.attr.progressBarStyleLarge);
        //pbBuffering.setIndeterminate(true);
        //pbBuffering.setVisibility(View.VISIBLE);
        layoutPB.addView(pbBuffering, params);
        playerView.removeView(layoutPB);
        playerView.addView(layoutPB);

        //============ PROGRESSBAR END =============
    }

    private void pbBufferingShow() {
        if (!pbBuffering.isShown()) {
            pbBuffering.setVisibility(View.VISIBLE);
        }
    }

    private void pbBufferingHide() {
        if (pbBuffering.isShown()) {
            pbBuffering.setVisibility(View.GONE);
        }
    }
*/

    public void bufferInitialize(Context context) {
        dialogBuffer =  new SpotsDialog.Builder()
                .setContext(context)
                .setMessage("Buffering...")
                .setTheme(R.style.KangjiCustomAlertDialogBuffering)
                .build();
        RelativeLayout layout = new RelativeLayout(context);
    }

    public void bufferTampil() {
        dialogBuffer.show();
    }

    public void bufferAmblas() {
        if (dialogBuffer.isShowing()) {
            //dialogBuffer.cancel();
            dialogBuffer.dismiss();
        }
    }












    public void setPlayerListener(CallBacks.playerCallBack mPlayerCallBack) {
        listner = mPlayerCallBack;
    }
/*
    public PlayerView getPlayerView() {
        return mPlayerView;
    }
*/
    public void playStream(String urlToPlay) {

        uriString = urlToPlay;
        Uri mp4VideoUri = Uri.parse(uriString);
        MediaSource videoSource;
        String filenameArray[] = urlToPlay.split("\\.");
        if (uriString.toUpperCase().contains("M3U8")) {
            //videoSource = new HlsMediaSource(mp4VideoUri, dataSourceFactory, null, null);
            videoSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(mp4VideoUri);
        } else {
            mp4VideoUri = Uri.parse(urlToPlay);
            videoSource = new ExtractorMediaSource(mp4VideoUri, dataSourceFactory, new DefaultExtractorsFactory(),
                    null, null);
        }

        // Prepare the player with the source.
        mPlayer.prepare(videoSource);
        mPlayer.setPlayWhenReady(true);

    }

    public void setPlayerVolume(float vol) {
        mPlayer.setVolume(vol);
    }

    public void setUriString(String uri) {
        uriString = uri;
    }

    public void setPlaylist(ArrayList<String> uriArrayList, Integer index, CallBacks.playerCallBack callBack) {
        mPlayList = uriArrayList;
        playlistIndex = index;
        listner = callBack;
        playStream(mPlayList.get(playlistIndex));
    }


    public void playerPlaySwitch() {
        if (uriString != "") {
            mPlayer.setPlayWhenReady(!mPlayer.getPlayWhenReady());
        }
    }

    public void stopPlayer(boolean state) {
        mPlayer.setPlayWhenReady(!state);
    }

    public void destroyPlayer() {
        mPlayer.stop();
    }

    public Boolean isPlayerPlaying() {
        return mPlayer.getPlayWhenReady();
    }

    public ArrayList<String> readURLs(String url) {
        if (url == null) return null;
        ArrayList<String> allURls = new ArrayList<String>();
        try {

            URL urls = new URL(url);
            BufferedReader in = new BufferedReader(new InputStreamReader(urls
                    .openStream()));
            String str;
            while ((str = in.readLine()) != null) {
                allURls.add(str);
            }
            in.close();
            return allURls;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }




}
