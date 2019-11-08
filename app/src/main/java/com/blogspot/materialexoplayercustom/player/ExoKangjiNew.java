package com.blogspot.materialexoplayercustom.player;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSourceFactory;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.DefaultHlsExtractorFactory;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.AssetDataSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.util.Util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class ExoKangjiNew {
    /**
     * declare some usable variable
     */

    private static final String TAG = "=== " + ExoKangjiNew.class.getSimpleName() + " ===";

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static ExoKangjiNew mInstance = null;
    private SimpleExoPlayer mPlayer;
    private Uri mUri;
    ArrayList<String> mPlayList = null;
    Integer playlistIndex = 0;

    PlayerView mPlayerView;
    private String userAgent = "ExoKangji";
    private Context mContext;
    private TextView tvError;
    private String scheme;

    public static ExoKangjiNew getSharedInstance() {
        if (mInstance == null) {
            mInstance = new ExoKangjiNew();
        }
        return mInstance;
    }

    public void initializePlayer(Context mContext, PlayerView mPlayerView, @Nullable ProgressBar progressBar, @Nullable Drawable drawableArtWork) {
        this.mContext = mContext;
        this.mPlayerView = mPlayerView;
        mPlayerView.setDefaultArtwork(drawableArtWork);

        if (mContext == null || mPlayerView == null) {
            return;
        }
        /*
        if (mPlayerView != null) {
            tvError = new TextView(mContext);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            tvError.setLayoutParams(params);
            tvError.setGravity(Gravity.CENTER);
            tvError.setText("HAHAHAHAHAH");
            tvError.setTextColor(Color.parseColor("#FFFFFF"));
            mPlayerView.addView(tvError);
        }
         */

        if (mPlayer == null) {
            // Create a new player if the player is null or
            // we want to play a new video
            // Do all the standard ExoPlayer code here...

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

            mPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector, loadControl);
            mPlayerView.setPlayer(mPlayer);

        }

    }

    public MediaSource buildMediaSource(String inputVideoString) {

        mUri = Uri.parse(inputVideoString);
        DataSpec dataSpec = new DataSpec(mUri);

        // these are reused for both media sources we create below
        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        DefaultHttpDataSourceFactory defaultHttpDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent);
        DefaultDashChunkSource.Factory dashChunkSourceFactory = new DefaultDashChunkSource.Factory(new DefaultHttpDataSourceFactory(userAgent, BANDWIDTH_METER));
        DefaultHttpDataSourceFactory manifestDataSourceFactory = new DefaultHttpDataSourceFactory(userAgent);
        RtmpDataSourceFactory rtmpDataSourceFactory = new RtmpDataSourceFactory();

        DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(mContext, userAgent);
        DefaultHlsExtractorFactory defaultHlsExtractorFactory = new DefaultHlsExtractorFactory();
        DefaultSsChunkSource.Factory ssChunkSourceFactory = new DefaultSsChunkSource.Factory(new DefaultHttpDataSourceFactory(userAgent, BANDWIDTH_METER));
        /*
        try {
            URI uriX = new URI(inputVideoString);
            scheme = uriX.getScheme().toUpperCase();
            Log.d(TAG, "========" + scheme);
        }
        catch (URISyntaxException e) {
        }
         */

        int type = Util.inferContentType(mUri);
        switch (type) {
            case C.TYPE_DASH: {
                Log.d(TAG, "MEDIA TYPE - DASH");
                return new DashMediaSource.Factory(dashChunkSourceFactory, manifestDataSourceFactory).createMediaSource(mUri);
            }
            case C.TYPE_SS: {
                Log.d(TAG, "MEDIA TYPE - SS");
                return new SsMediaSource.Factory(ssChunkSourceFactory, manifestDataSourceFactory).createMediaSource(mUri);
            }
            case C.TYPE_HLS: {
                Log.d(TAG, "MEDIA TYPE - HLS");
                return new HlsMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(mUri);
            }
            case C.TYPE_OTHER: {
                if (mUri.getScheme().toUpperCase().equals("RTP")) {
                    Log.d(TAG, "MEDIA TYPE - OTHER - RTP");
                    return new ProgressiveMediaSource.Factory(rtmpDataSourceFactory).createMediaSource(mUri);
                }
                else if (mUri.getScheme().toUpperCase().equals("RTSP")) {
                    Log.d(TAG, "MEDIA TYPE - OTHER - RTSP");
                    return new ProgressiveMediaSource.Factory(rtmpDataSourceFactory).createMediaSource(mUri);
                }
                else if (mUri.getScheme().toUpperCase().equals("RTMP")) {
                    Log.d(TAG, "MEDIA TYPE - OTHER - RTMP");
                    return new ProgressiveMediaSource.Factory(rtmpDataSourceFactory).createMediaSource(mUri);
                }

                else {

                    if (mUri.getScheme().toUpperCase().equals("ASSET")) {
                        Log.d(TAG, "MEDIA TYPE - OTHER - LOCAL ASSET");

                        // ======== LOCAL MEDIA ASSET ========
                        final AssetDataSource assetDataSource = new AssetDataSource(mContext);
                        try {
                            assetDataSource.open(dataSpec);
                        }
                        catch (AssetDataSource.AssetDataSourceException e) {
                            e.printStackTrace();
                        }
                        DataSource.Factory assetFactory = new DataSource.Factory() {
                            @Override
                            public DataSource createDataSource() {
                                return assetDataSource;
                            }
                        };
                        return new ProgressiveMediaSource.Factory(assetFactory).createMediaSource(mUri);
                    }

                    else if (mUri.getScheme().toUpperCase().equals("FILE")) {
                        Log.d(TAG, "MEDIA TYPE - OTHER - LOCAL FILE");

                        // =========== LOCAL MEDIA FILE ============
                        final FileDataSource fileDataSource = new FileDataSource();
                        try {
                            fileDataSource.open(dataSpec);
                        }
                        catch (FileDataSource.FileDataSourceException e) {
                            e.printStackTrace();
                        }
                        DataSource.Factory fileFactory = new DataSource.Factory() {
                            @Override
                            public DataSource createDataSource() {
                                return fileDataSource;
                            }
                        };
                        return new ProgressiveMediaSource.Factory(fileFactory).createMediaSource(mUri);
                    }
                    else {
                        Log.d(TAG, "MEDIA TYPE - OTHER - STREAMING");
                        return new ProgressiveMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(mUri);
                    }

                }

            }
            default: {
                Log.d(TAG, "MEDIA TYPE - UNSUPPORTED TYPE: " + type);
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }

        /*
        switch (scheme) {
            case "RTP" :
            case "RTSP" :
            case "RTMP" : {
                Log.d(TAG, "Play RTMP");
                return new ProgressiveMediaSource.Factory(rtmpDataSourceFactory).createMediaSource(mUri);
            }
            default: {

                if (extUpperCase.equals(".MP3") || extUpperCase.equals(".MP4")) {
                    Log.d(TAG, "Play GLOBAL - MP3/MP4");
                    return new ProgressiveMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(mUri);
                }
                else if (extUpperCase.equals(".M3U8")) {
                    Log.d(TAG, "Play GLOBAL - M3U8");
                    return new HlsMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(mUri);
                }
                else {
                    Log.d(TAG, "Play GLOBAL - DASH");
                    return new DashMediaSource.Factory(dashChunkSourceFactory, manifestDataSourceFactory).createMediaSource(mUri);
                }

                /*
                if (inputVideoString.toUpperCase().contains("MP3") || inputVideoString.toUpperCase().contains("MP4")) {
                    Log.d(TAG, "Play GLOBAL - MP3/MP4");
                    return new ProgressiveMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(mUri);
                }
                else if (inputVideoString.toUpperCase().contains("M3U8")) {
                    return new HlsMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(mUri);
                }
                else {
                    Log.d(TAG, "Play GLOBAL - DASH");
                    return new DashMediaSource.Factory(dashChunkSourceFactory, manifestDataSourceFactory).createMediaSource(mUri);
                }

            }
        }
        */

    }

    public void switchScreen(PlayerView oldPlayerView, PlayerView newPlayerView) {
        PlayerView.switchTargetView(mPlayer, oldPlayerView, newPlayerView);
    }

    public void playContent(String inputSource) {
        mPlayer.stop();
        MediaSource mediaSource = buildMediaSource(inputSource);
        mPlayer.clearVideoSurface();
        mPlayer.setVideoSurfaceView((SurfaceView) mPlayerView.getVideoSurfaceView());
        mPlayer.seekTo(mPlayer.getCurrentPosition() + 1);
        mPlayer.prepare(mediaSource, true, false);
        mPlayer.setPlayWhenReady(true);
    }
    /*
    public void playLocalContent(Uri uriLocalContent) {

        mPlayer.stop();

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
        mPlayer.setVideoSurfaceView((SurfaceView) mPlayerView.getVideoSurfaceView());
        mPlayer.seekTo(mPlayer.getCurrentPosition() + 1);
        mPlayer.prepare(mediaSource);
        mPlayer.setPlayWhenReady(true);
    }
     */

    private void playerListener(ProgressBar progressBarBuffering) {

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

}























/**

 /**
 /* private constructor
 /* @param mContext
 /*
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
            mPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector, loadControl);
            //mPlayer = ExoPlayerFactory.newSimpleInstance(mContext, renderersFactory, trackSelector, loadControl);

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
*/

