package com.blogspot.materialexoplayercustom.player.playeronrv;

import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.materialexoplayercustom.R;
import com.blogspot.materialexoplayercustom.onrecyclerview.KangjiExoRVItem;
import com.blogspot.materialexoplayercustom.onrecyclerview.KangjiExoRVViewHolder;
import com.blogspot.materialexoplayercustom.player.ConfigPlayerKangji;
import com.bumptech.glide.RequestManager;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

public class ExoKangjiRecyclerView extends RecyclerView {

    private static final String TAG = "=== " + ExoKangjiRecyclerView.class.getSimpleName() + " ===";
    /**
     * PlayerViewHolder UI component
     * Watch PlayerViewHolder class
     */
    private ImageView onrvThumbnail, onrvVolumeControl;
    private ProgressBar onrvProgressBar;
    private View viewHolderParent;
    private FrameLayout onrvMediaContainer;
    private PlayerView videoSurfaceView;
    private SimpleExoPlayer videoPlayer;

    private URI xURI;
    private String scheme;
    private String outputSource;


    /**
     * variable declaration
     */
    // Media List
    private ArrayList<KangjiExoRVItem> mediaObjects = new ArrayList<>();
    private int videoSurfaceDefaultHeight = 0;
    private int screenDefaultHeight = 0;
    private Context context;
    private int playPosition = -1;
    private boolean isVideoViewAdded;
    private RequestManager onrvRequestManager;
    // controlling volume state
    private VolumeState volumeState;
    private OnClickListener videoViewClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            toggleVolume();
        }
    };


    public ExoKangjiRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.context = context.getApplicationContext();
        Display display = ((WindowManager) Objects.requireNonNull(
                getContext().getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);

        videoSurfaceDefaultHeight = point.x;
        screenDefaultHeight = point.y;

        videoSurfaceView = new PlayerView(this.context);
        videoSurfaceView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        //Create the player using ExoPlayerFactory
        videoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
        // Disable Player Control
        videoSurfaceView.setUseController(false);
        // Bind the player to the view.
        videoSurfaceView.setPlayer(videoPlayer);
        // Turn on Volume
        setVolumeControl(VolumeState.ON);

        addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (onrvThumbnail != null) {
                        // show the old thumbnail
                        onrvThumbnail.setVisibility(VISIBLE);
                    }

                    // There's a special case when the end of the list has been reached.
                    // Need to handle that with this bit of logic
                    if (!recyclerView.canScrollVertically(1)) {
                        playVideo(true);
                    } else {
                        playVideo(false);
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        addOnChildAttachStateChangeListener(new OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                if (viewHolderParent != null && viewHolderParent.equals(view)) {
                    resetVideoView();
                }
            }
        });

        videoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups,
                                        TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {

                    case Player.STATE_BUFFERING:
                        Log.e(TAG, "onPlayerStateChanged: Buffering video.");
                        if (onrvProgressBar != null) {
                            onrvProgressBar.setVisibility(VISIBLE);
                        }

                        break;
                    case Player.STATE_ENDED:
                        Log.d(TAG, "onPlayerStateChanged: Video ended.");
                        videoPlayer.seekTo(0);
                        break;
                    case Player.STATE_IDLE:

                        break;
                    case Player.STATE_READY:
                        Log.e(TAG, "onPlayerStateChanged: Ready to play.");

                        if (onrvProgressBar != null) {
                            onrvProgressBar.setVisibility(GONE);
                        }
                        if (!isVideoViewAdded) {
                            addVideoView();
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

                switch (error.type) {
                    case ExoPlaybackException.TYPE_SOURCE:
                        ExoKangjiRecyclerView..setText("Error: TYPE_SOURCE");
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

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });
    }
    public void playVideo(boolean isEndOfList) {

        int targetPosition;

        if (!isEndOfList) {
            int startPosition = ((LinearLayoutManager) Objects.requireNonNull(
                    getLayoutManager())).findFirstVisibleItemPosition();
            int endPosition = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();

            // if there is more than 2 list-items on the screen, set the difference to be 1
            if (endPosition - startPosition > 1) {
                endPosition = startPosition + 1;
            }

            // something is wrong. return.
            if (startPosition < 0 || endPosition < 0) {
                return;
            }

            // if there is more than 1 list-item on the screen
            if (startPosition != endPosition) {
                int startPositionVideoHeight = getVisibleVideoSurfaceHeight(startPosition);
                int endPositionVideoHeight = getVisibleVideoSurfaceHeight(endPosition);

                targetPosition =
                        startPositionVideoHeight > endPositionVideoHeight ? startPosition : endPosition;
            } else {
                targetPosition = startPosition;
            }
        } else {
            targetPosition = mediaObjects.size() - 1;
        }

        Log.d(TAG, "playVideo: target position: " + targetPosition);

        // video is already playing so return
        if (targetPosition == playPosition) {
            return;
        }

        // set the position of the list-item that is to be played
        playPosition = targetPosition;
        if (videoSurfaceView == null) {
            return;
        }

        // remove any old surface views from previously playing videos
        videoSurfaceView.setVisibility(INVISIBLE);
        removeVideoView(videoSurfaceView);

        int currentPosition =
                targetPosition - ((LinearLayoutManager) Objects.requireNonNull(
                        getLayoutManager())).findFirstVisibleItemPosition();

        View child = getChildAt(currentPosition);
        if (child == null) {
            return;
        }

        KangjiExoRVViewHolder holder = (KangjiExoRVViewHolder) child.getTag();
        if (holder == null) {
            playPosition = -1;
            return;
        }
        onrvThumbnail = holder.onrvIVThumbnail;
        onrvProgressBar = holder.onrvProgressBar;
        onrvVolumeControl = holder.onrvIVVolumeControl;
        viewHolderParent = holder.itemView;
        onrvRequestManager = holder.onrvRequestManager;
        onrvMediaContainer = holder.onrvFrameContainer;

        videoSurfaceView.setPlayer(videoPlayer);
        viewHolderParent.setOnClickListener(videoViewClickListener);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                context, Util.getUserAgent(context, TAG));
        String mediaUrl = mediaObjects.get(targetPosition).getVideoURL();
        if (mediaUrl != null) {
            //MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
            //        .createMediaSource(Uri.parse(mediaUrl));
            //videoPlayer.prepare(videoSource);
            //videoPlayer.setPlayWhenReady(true);

            if (! mediaUrl.isEmpty()) {

                try {

                    if (! mediaUrl.isEmpty()) {

                        URL url = new URL(mediaUrl);
                        String baseUrl = url.getHost().toUpperCase();
                        if (baseUrl.equals(ConfigPlayerKangji.YT_BASE_URL_1) ||
                                baseUrl.equals(ConfigPlayerKangji.YT_BASE_URL_2) ||
                                baseUrl.equals(ConfigPlayerKangji.YT_BASE_URL_3)) {

                            new YouTubeExtractor(context) {
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
                                                    Log.e(TAG, outputSource);
                                                    //ExoKangjiNew.getSharedInstance().playContent(outputSource, true);
                                                    //return;
                                                    MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                                                            .createMediaSource(Uri.parse(outputSource));
                                                    videoPlayer.prepare(videoSource);
                                                    videoPlayer.setPlayWhenReady(true);
                                                }
                                            }
                                        }
                                    }

                                }
                            }.extract(mediaUrl, true, true);

                        }
                        else {
                            // LANGSUNG VIDEO LINK, GAK PERLU EXTRACTOR
                            //ExoKangjiNew.getSharedInstance().playContent(outputSource, false);
                            Log.e(TAG, mediaUrl);
                            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                                    .createMediaSource(Uri.parse(mediaUrl));
                            videoPlayer.prepare(videoSource);
                            videoPlayer.setPlayWhenReady(true);
                        }

                    }

                }
                catch (MalformedURLException e) {
                    e.printStackTrace();
                    Log.e(TAG, "(= EXTRAK MANGGIS =) - " + e.toString());
                }

            }
            else {
                stopPlayer();
            }




        }
    }

    /**
     * Returns the visible region of the video surface on the screen.
     * if some is cut off, it will return less than the @videoSurfaceDefaultHeight
     */
    private int getVisibleVideoSurfaceHeight(int playPosition) {
        int at = playPosition - ((LinearLayoutManager) Objects.requireNonNull(
                getLayoutManager())).findFirstVisibleItemPosition();
        Log.d(TAG, "getVisibleVideoSurfaceHeight: at: " + at);

        View child = getChildAt(at);
        if (child == null) {
            return 0;
        }

        int[] location = new int[2];
        child.getLocationInWindow(location);

        if (location[1] < 0) {
            return location[1] + videoSurfaceDefaultHeight;
        } else {
            return screenDefaultHeight - location[1];
        }
    }

    // Remove the old player
    private void removeVideoView(PlayerView videoView) {
        ViewGroup parent = (ViewGroup) videoView.getParent();
        if (parent == null) {
            return;
        }

        int index = parent.indexOfChild(videoView);
        if (index >= 0) {
            parent.removeViewAt(index);
            isVideoViewAdded = false;
            viewHolderParent.setOnClickListener(null);
        }
    }

    private void addVideoView() {
        onrvMediaContainer.addView(videoSurfaceView);
        isVideoViewAdded = true;
        videoSurfaceView.requestFocus();
        videoSurfaceView.setVisibility(VISIBLE);
        videoSurfaceView.setAlpha(1);
        onrvThumbnail.setVisibility(GONE);
    }

    private void resetVideoView() {
        if (isVideoViewAdded) {
            removeVideoView(videoSurfaceView);
            playPosition = -1;
            videoSurfaceView.setVisibility(INVISIBLE);
            onrvThumbnail.setVisibility(VISIBLE);
        }
    }

    public void releasePlayer() {

        if (videoPlayer != null) {
            videoPlayer.release();
            videoPlayer = null;
        }

        viewHolderParent = null;
    }

    public void stopPlayer() {
        if (videoPlayer != null) {
            videoPlayer.stop();
        }
    }

    public void onPausePlayer() {
        if (videoPlayer != null) {
            videoPlayer.stop(true);
        }
    }

    private void toggleVolume() {
        if (videoPlayer != null) {
            if (volumeState == VolumeState.OFF) {
                Log.d(TAG, "togglePlaybackState: enabling volume.");
                setVolumeControl(VolumeState.ON);
            } else if (volumeState == VolumeState.ON) {
                Log.d(TAG, "togglePlaybackState: disabling volume.");
                setVolumeControl(VolumeState.OFF);
            }
        }
    }

    private void setVolumeControl(VolumeState state) {
        volumeState = state;
        if (state == VolumeState.OFF) {
            videoPlayer.setVolume(0f);
            animateVolumeControl();
        } else if (state == VolumeState.ON) {
            videoPlayer.setVolume(1f);
            animateVolumeControl();
        }
    }

    private void animateVolumeControl() {
        if (onrvVolumeControl != null) {
            onrvVolumeControl.bringToFront();
            if (volumeState == VolumeState.OFF) {
                onrvRequestManager.load(R.drawable.ic_volume_off)
                        .into(onrvVolumeControl);
            } else if (volumeState == VolumeState.ON) {
                onrvRequestManager.load(R.drawable.ic_volume_on)
                        .into(onrvVolumeControl);
            }
            onrvVolumeControl.animate().cancel();

            onrvVolumeControl.setAlpha(1f);

            onrvVolumeControl.animate()
                    .alpha(0f)
                    .setDuration(600).setStartDelay(1000);
        }
    }

    public void setMediaObjects(ArrayList<KangjiExoRVItem> mediaObjects) {
        this.mediaObjects = mediaObjects;
    }

    /**
     * Volume ENUM
     */
    private enum VolumeState {
        ON, OFF
    }




    /*
    public void ekstrakManggis() {

        inputSource = linkSource;
        try {
            xURI = new URI(inputSource);
            scheme = xURI.getScheme().toUpperCase();
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "SCHEME: " + scheme);
        switch (scheme) {
            case "RTP" :
            case "RTSP" :
            case "RTMP" : {
                outputSource = inputSource;
                //ExoKangjiNew.getSharedInstance().playContent(outputSource, false);
            }
            default: {

                try {
                    URL url = new URL(inputSource);
                    String baseUrl = url.getHost().toUpperCase();
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
                                                Log.e(TAG, outputSource);
                                                //ExoKangjiNew.getSharedInstance().playContent(outputSource, true);
                                                //return;
                                            }
                                        }
                                    }
                                }

                            }
                        }.extract(linkSource, true, true);

                    }
                    else {
                        // LANGSUNG VIDEO LINK, GAK PERLU EXTRACTOR
                        //ExoKangjiNew.getSharedInstance().playContent(outputSource, false);
                        Log.e(TAG, outputSource);
                    }
                }
                catch (MalformedURLException e) {
                    e.printStackTrace();
                    Log.e(TAG, "(= EXTRAK MANGGIS =) - " + e.toString());
                }

            }
        }

    }

     */







}
