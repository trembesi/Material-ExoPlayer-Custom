package com.blogspot.materialexoplayercustom.player;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blogspot.materialexoplayercustom.LayarCilikActivity;
import com.blogspot.materialexoplayercustom.R;
import com.blogspot.materialexoplayercustom.tls.MeksoTLS;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
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
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.AssetDataSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

public class ExoKangjiNew {
    /**
     * declare some usable variable
     */

    private static final String TAG = "=== " + ExoKangjiNew.class.getSimpleName() + " ===";

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private DataSource.Factory dataSourceFactory;
    private static ExoKangjiNew mInstance = null;
    private SimpleExoPlayer mPlayer;
    private boolean isPlayerPlaying;
    private Uri mUri;
    CallBacks.playerCallBack listner;
    ArrayList<String> mPlayList = null;
    Integer playlistIndex = 0;
    //String uriString = "";

    PlayerView mPlayerView;
    private String userAgent = "ExoKangji";
    private Context mContext;

    private TextView tvError;
    private String outputLink;

    /**
     * private constructor
     * @param mContext
     */
    private ExoKangjiNew(Context mContext) {
        this.mContext = mContext;
    }

    public static ExoKangjiNew getSharedInstance(Context mContext) {
        if (mInstance == null) {
            mInstance = new ExoKangjiNew(mContext);
        }
        return mInstance;
    }

    public void initializePlayer(PlayerView mPlayerView, ProgressBar progressBarBuffering) {
        this.mPlayerView = mPlayerView;
        MeksoTLS meksoTLS = new MeksoTLS(mContext);
        meksoTLS.peksoTLSv12();

        if (mContext == null || mPlayerView == null) {
            return;
        }

        //if (mPlayerView != null) {
            tvError = new TextView(mContext);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            );
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            tvError.setLayoutParams(params);
            tvError.setGravity(Gravity.CENTER);
            tvError.setText("HAHAHAHAHAH");
            tvError.setTextColor(Color.parseColor("#FFFFFF"));
            mPlayerView.addView(tvError);
        //}

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
                    true
            );

            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
            TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
            DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(mContext);

            // 2. Create Player
            //mPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector/*, loadControl*/);
            mPlayer = ExoPlayerFactory.newSimpleInstance(mContext, renderersFactory, trackSelector, loadControl);

            mPlayerView.setPlayer(mPlayer);
            progressBarBuffering.setVisibility(View.GONE);
            playerListener(progressBarBuffering);

            // play intro video
            //Uri uriIntro = Uri.parse("asset:///intro.MP4");
            //playLocalContent(uriIntro);
            //playStreamingContent(ConfigPlayerKangji.INTRO);
            //mPlayer.setPlayWhenReady(true);

        }

    }

    public MediaSource buildMediaSource(String inputVideoString) {
        /*
        // these are reused for both media sources we create below
        //DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        DefaultHttpDataSourceFactory defaultHttpDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent);
        DefaultDashChunkSource.Factory dashChunkSourceFactory = new DefaultDashChunkSource.Factory(new DefaultHttpDataSourceFactory(userAgent, BANDWIDTH_METER));
        DefaultHttpDataSourceFactory manifestDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent);

        if (uri.getLastPathSegment().toUpperCase().contains("MP3") || uri.getLastPathSegment().toUpperCase().contains("MP4")) {
            return new ProgressiveMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(uri);
            //return mediaSource;
        }
        else if (uri.getLastPathSegment().toUpperCase().contains("M3U8")) {
            return new HlsMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(uri);
        }
        else {
            return new DashMediaSource.Factory(dashChunkSourceFactory, manifestDataSourceFactory).createMediaSource(uri);
        }
        */

/*
        mUri = Uri.parse(inputVideoString);
        // Produces DataSource instances through which media data is loaded.
        dataSourceFactory = new DefaultDataSourceFactory(mContext, Util.getUserAgent(mContext, mContext.getString(R.string.app_name)), BANDWIDTH_METER);
        // This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(mUri);

        //== OPO IKI YAAA ====
        String filenameArray[] = inputVideoString.split("\\.");
        if (inputVideoString.toUpperCase().contains("M3U8")) {
            //videoSource = new HlsMediaSource(mUri, dataSourceFactory, null, null);
            videoSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(mUri);
        }
        else {
            videoSource = new ExtractorMediaSource(mUri, dataSourceFactory, new DefaultExtractorsFactory(), null, null);
        }
        return videoSource;
*/


        // these are reused for both media sources we create below
        //DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        DefaultHttpDataSourceFactory defaultHttpDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent);
        DefaultDashChunkSource.Factory dashChunkSourceFactory = new DefaultDashChunkSource.Factory(new DefaultHttpDataSourceFactory(userAgent, BANDWIDTH_METER));
        DefaultHttpDataSourceFactory manifestDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent);

        mUri = Uri.parse(inputVideoString);

        if (inputVideoString.toUpperCase().contains("MP3") || inputVideoString.toUpperCase().contains("MP4")) {
            return new ProgressiveMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(mUri);
            //return mediaSource;
        }
        else if (inputVideoString.toUpperCase().contains("M3U8")) {
            return new HlsMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(mUri);
        }
        else {
            return new DashMediaSource.Factory(dashChunkSourceFactory, manifestDataSourceFactory).createMediaSource(mUri);
        }

    }

    public void playStreamingContent(String stringContentLink) {
        //mPlayer.stop();
        //mPlayer.seekTo(0L);
        MediaSource mediaSource = buildMediaSource(stringContentLink);
        mPlayer.clearVideoSurface();
        mPlayer.setVideoTextureView((TextureView) mPlayerView.getVideoSurfaceView());
        mPlayer.seekTo(mPlayer.getCurrentPosition() + 1);
        mPlayer.prepare(mediaSource);
        mPlayer.setPlayWhenReady(true);
    }

    public void changeAndPlayStreamingContent(String stringContentLink) {
        mPlayer.stop();
        mPlayer.seekTo(0L);
        MediaSource mediaSource = buildMediaSource(stringContentLink);
        mPlayer.clearVideoSurface();
        mPlayer.setVideoTextureView((TextureView) mPlayerView.getVideoSurfaceView());
        //mPlayer.seekTo(mPlayer.getCurrentPosition() + 1);
        mPlayer.prepare(mediaSource);
        mPlayer.setPlayWhenReady(true);
    }

    public void playLocalContent(Uri uriLocalContent) {
        DataSpec dataSpec = new DataSpec(uriLocalContent);
        final AssetDataSource assetDataSource = new AssetDataSource(mContext);
        try {
            assetDataSource.open(dataSpec);
        }
        catch (AssetDataSource.AssetDataSourceException e) {
            e.printStackTrace();
        }

        DataSource.Factory factory = new DataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                //return null;
                return assetDataSource;
            }
        };

        MediaSource mediaSource = new ExtractorMediaSource(
                assetDataSource.getUri(),
                factory,
                new DefaultExtractorsFactory(),
                null,
                null
        );

        mPlayer.clearVideoSurface();
        mPlayer.setVideoTextureView((TextureView) mPlayerView.getVideoSurfaceView());
        mPlayer.seekTo(mPlayer.getCurrentPosition() + 1);
        //mPlayer.prepare(mediaSource);
        mPlayer.prepare(mediaSource);
        mPlayer.setPlayWhenReady(true);
    }
/*
    public void checkLinkAndPlayStreaming(String inputLink) {

        try {
            URL url = new URL(inputLink);
            //String baseUrl = url.getProtocol() + "://" + url.getHost();
            String baseUrl = url.getHost();
            if (baseUrl.equals(ConfigPlayerKangji.YT_BASE_URL_1) ||
                    baseUrl.equals(ConfigPlayerKangji.YT_BASE_URL_2) ||
                    baseUrl.equals(ConfigPlayerKangji.YT_BASE_URL_3)) {

                new YouTubeExtractor(mContext) {
                    @Override
                    public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                        if (ytFiles != null) {
                            int itag = 22;
                            //ExoKangji.getSharedInstance().persiapanExoPlayer(LayarCilikActivity.this, mPlayerViewCilik, ytFiles.get(itag).getUrl(), pbBuffer);
                            //fullscreenVideoLink = ytFiles.get(itag).getUrl();
                            playStreamingContent(ytFiles.get(itag).getUrl());
                        }
                    }
                }.extract(inputLink, true, true);

            } else {
                // LANGSUNG VIDEO LINK, GAK PERLU EXTRACTOR
                //ExoKangji.getSharedInstance().playStream(videoLink);
                playStreamingContent(inputLink);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(TAG,"(=EXTRAK MANGGIS=) " + e.toString());
        }
    }
*/
    public void playerListener(ProgressBar progressBarBuffering) {

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
                        tvError.setVisibility(View.GONE);
                        if (progressBarBuffering != null) {
                            progressBarBuffering.setVisibility(View.VISIBLE);
                        }
                        break;
                    case Player.STATE_ENDED:
                        Log.d(TAG, "(= STATUS PLAYBACK =) - Ended...");
                        tvError.setVisibility(View.GONE);
                        // Activate the force enable
                        if (progressBarBuffering != null) {
                            progressBarBuffering.setVisibility(View.GONE);}
                        break;
                    case Player.STATE_IDLE:
                        Log.d(TAG, "(= STATUS PLAYBACK =) - Idle...");
                        //tvError.setVisibility(View.GONE);
                        break;
                    case Player.STATE_READY:
                        Log.d(TAG, "(= STATUS PLAYBACK =) - Ready...");
                        tvError.setVisibility(View.GONE);
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
                progressBarBuffering.setVisibility(View.GONE);
                tvError.setVisibility(View.VISIBLE);

                switch (error.type) {
                    case ExoPlaybackException.TYPE_SOURCE:
                        //tvError.setText("TYPE_SOURCE: " + error.getSourceException().getMessage());
                        tvError.setText("Error: TYPE_SOURCE");
                        //tvError.setVisibility(View.VISIBLE);
                        Log.e(TAG, "TYPE_SOURCE: " + error.getSourceException().getMessage());
                        break;

                    case ExoPlaybackException.TYPE_RENDERER:
                        //tvError.setText("TYPE_RENDERER: " + error.getRendererException().getMessage());
                        tvError.setText("Error: TYPE_RENDERER");
                        //tvError.setVisibility(View.VISIBLE);
                        Log.e(TAG, "TYPE_RENDERER: " + error.getRendererException().getMessage());
                        break;

                    case ExoPlaybackException.TYPE_UNEXPECTED:
                        //tvError.setText("TYPE_UNEXPECTED: " + error.getUnexpectedException().getMessage());
                        tvError.setText("Error: TYPE_UNEXPECTED");
                        //tvError.setVisibility(View.VISIBLE);
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
            //bufferInitialize(mContext);
        }
    }


}
