package com.blogspot.materialexoplayercustom.player.playeronrv;

import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
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

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.blogspot.materialexoplayercustom.KangjiSaringanTahu;
import com.blogspot.materialexoplayercustom.R;
import com.blogspot.materialexoplayercustom.player.ConfigPlayerKangji;
import com.blogspot.materialexoplayercustom.player.KangjiBuildMediaSource;
import com.bumptech.glide.RequestManager;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSourceFactory;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
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
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.AssetDataSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.util.Util;

import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.Objects;

public class ExoKangjiRecyclerView extends RecyclerView implements KangjiSaringanTahu.KangjiSaringanTahuListener {

    private static final String TAG = "=== " + ExoKangjiRecyclerView.class.getSimpleName() + " ===";
    /**
     * PlayerViewHolder UI component
     * Watch PlayerViewHolder class
     */
    private ImageView onrvThumbnail, ivVolumeAnimate;
    private ProgressBar onrvProgressBar;
    private View viewHolderParent;
    private FrameLayout onrvMediaContainer;
    private PlayerView playerView;
    private SimpleExoPlayer player;
    private PlayerControlView controlView;
    private FrameLayout btnFrameVolume;
    private ImageView ivVolume;
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    //private MediaSource mediaSource;

    //private URI xURI;
    //private String scheme;
    //private String outputSource;
    private Uri mUri;
    private String fileExtUppercase;

    private TextDrawable textDrawable;
    private ColorGenerator colorGenerator = ColorGenerator.MATERIAL;


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
    /*
    private OnClickListener videoViewClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            toggleVolume();
        }
    };
     */


    public ExoKangjiRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    public void onHasilSaringan(String outputSource, boolean isYTSource) {
        //ExoKangjiNew.getSharedInstance().playContent(outputSource, isYTSource);

        /*
        Uri uri = Uri.parse(outputSource);
        // Create a data source factory.
        DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory(Util.getUserAgent(context, ConfigPlayerKangji.USER_AGENT));
        // Create a progressive media source pointing to a stream uri.
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
        player.prepare(mediaSource);
        player.setPlayWhenReady(true);
         */



        player.prepare(new KangjiBuildMediaSource(context, outputSource, isYTSource).buildMediaSource());
        player.setPlayWhenReady(true);
    }

    private void init(Context context) {
        this.context = context.getApplicationContext();
        Display display = ((WindowManager) Objects.requireNonNull(
                getContext().getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);

        videoSurfaceDefaultHeight = point.x;
        screenDefaultHeight = point.y;

        playerView = new PlayerView(this.context);
        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);

        controlView = playerView.findViewById(R.id.exo_controller);
        ivVolume = controlView.findViewById(R.id.exo_volume_icon);
        btnFrameVolume = controlView.findViewById(R.id.exo_volume_button);
        btnFrameVolume.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                toggleVolume();
            }
        });

        //BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        //Create the player using ExoPlayerFactory
        player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
        // Enable Player Control
        playerView.setUseController(true);
        // Bind the player to the view.
        playerView.setPlayer(player);
        // set player listener
        rvPlayerListener();
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

    }


    public MediaSource buildMediaSource(String inputVideoString) {
        Log.e(TAG, "(== buildMediaSource: " + inputVideoString);


        mUri = Uri.parse(inputVideoString);
        fileExtUppercase = getFileExtension(mUri).toUpperCase();
        Log.e(TAG, "FILE EXT: " + fileExtUppercase);
        DataSpec dataSpec = new DataSpec(mUri);

        // these are reused for both media sources we create below
        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        DefaultHttpDataSourceFactory defaultHttpDataSourceFactory = new DefaultHttpDataSourceFactory(ConfigPlayerKangji.USER_AGENT);
        DefaultDashChunkSource.Factory dashChunkSourceFactory = new DefaultDashChunkSource.Factory(new DefaultHttpDataSourceFactory(ConfigPlayerKangji.USER_AGENT, BANDWIDTH_METER));
        DefaultHttpDataSourceFactory manifestDataSourceFactory = new DefaultHttpDataSourceFactory(ConfigPlayerKangji.USER_AGENT);
        RtmpDataSourceFactory rtmpDataSourceFactory = new RtmpDataSourceFactory();

        DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(context, ConfigPlayerKangji.USER_AGENT);
        DefaultHlsExtractorFactory defaultHlsExtractorFactory = new DefaultHlsExtractorFactory();
        DefaultSsChunkSource.Factory ssChunkSourceFactory = new DefaultSsChunkSource.Factory(new DefaultHttpDataSourceFactory(ConfigPlayerKangji.USER_AGENT, BANDWIDTH_METER));


        int type = Util.inferContentType(mUri);
        switch (type) {
            case C.TYPE_DASH: {
                Log.e(TAG, "MEDIA TYPE - DASH");
                return new DashMediaSource.Factory(dashChunkSourceFactory, manifestDataSourceFactory).createMediaSource(mUri);
            }
            case C.TYPE_SS: {
                Log.e(TAG, "MEDIA TYPE - SS");
                return new SsMediaSource.Factory(ssChunkSourceFactory, manifestDataSourceFactory).createMediaSource(mUri);
            }
            case C.TYPE_HLS: {
                Log.e(TAG, "MEDIA TYPE - HLS");
                return new HlsMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(mUri);
            }
            case C.TYPE_OTHER: {

                if (mUri.getScheme().toUpperCase().equals("RTP")) {
                    Log.e(TAG, "MEDIA TYPE - OTHER - RTP");
                    return new ProgressiveMediaSource.Factory(rtmpDataSourceFactory).createMediaSource(mUri);
                }
                else if (mUri.getScheme().toUpperCase().equals("RTSP")) {
                    Log.e(TAG, "MEDIA TYPE - OTHER - RTSP");
                    return new ProgressiveMediaSource.Factory(rtmpDataSourceFactory).createMediaSource(mUri);
                }
                else if (mUri.getScheme().toUpperCase().equals("RTMP")) {
                    Log.e(TAG, "MEDIA TYPE - OTHER - RTMP");
                    return new ProgressiveMediaSource.Factory(rtmpDataSourceFactory).createMediaSource(mUri);
                }

                else {

                    if (mUri.getScheme().toUpperCase().equals("ASSET")) {
                        Log.e(TAG, "MEDIA TYPE - OTHER - LOCAL ASSET");

                        // ======== LOCAL MEDIA ASSET ========
                        final AssetDataSource assetDataSource = new AssetDataSource(context);
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
                        Log.e(TAG, "MEDIA TYPE - OTHER - LOCAL FILE");

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
                        //Log.e(TAG, "MEDIA TYPE - OTHER - STREAMING");
                        //return new ProgressiveMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(mUri);

                        Log.e(TAG, "MEDIA TYPE - OTHER - STREAMING");

                        if (fileExtUppercase.equalsIgnoreCase(".MP3")) {
                            Log.e(TAG, "MEDIA TYPE - OTHER - STREAMING - .MP3");
                            return new ProgressiveMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(mUri);
                        }
                        else if (fileExtUppercase.equalsIgnoreCase(".MP4")) {
                            Log.e(TAG, "MEDIA TYPE - OTHER - STREAMING - .MP4");
                            return new ProgressiveMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(mUri);
                        }
                        else if (fileExtUppercase.equalsIgnoreCase(".M3U8")) {
                            Log.e(TAG, "MEDIA TYPE - OTHER - STREAMING - .M3U8");
                            return new HlsMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(mUri);
                        }
                        else {
                            Log.e(TAG, "MEDIA TYPE - OTHER - STREAMING - ELSE -> PAKSA HLS");
                            //return new DashMediaSource.Factory(dashChunkSourceFactory, manifestDataSourceFactory).createMediaSource(mUri);
                            return new HlsMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(mUri);
                            //return new ExtractorMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(mUri);
                        }

                    }

                }

            }
            default: {
                Log.e(TAG, "MEDIA TYPE - UNSUPPORTED TYPE: " + type);
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }

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
        if (playerView == null) {
            return;
        }

        // remove any old surface views from previously playing videos
        playerView.setVisibility(INVISIBLE);
        removeVideoView(playerView);

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
        ivVolumeAnimate = holder.onrvIVVolumeControl;
        viewHolderParent = holder.itemView;
        onrvRequestManager = holder.onrvRequestManager;
        onrvMediaContainer = holder.onrvFrameContainer;

        playerView.setPlayer(player);
        //viewHolderParent.setOnClickListener(videoViewClickListener);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                context, Util.getUserAgent(context, TAG));

        String mediaUrl = mediaObjects.get(targetPosition).getVideoURL();
        String mediaThumbnail = mediaObjects.get(targetPosition).getThumbnail();
        String mediaHeader = mediaObjects.get(targetPosition).getHeader();
        final int color = colorGenerator.getColor(mediaHeader);
        textDrawable = TextDrawable.builder()
                .buildRect(String.valueOf(mediaHeader).substring(0,2), color);

        if (mediaThumbnail.isEmpty()) {
            playerView.setDefaultArtwork(textDrawable);
        }

        if (mediaUrl != null) {
            //MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
            //        .createMediaSource(Uri.parse(mediaUrl));
            //player.prepare(videoSource);
            //player.setPlayWhenReady(true);

            if (! mediaUrl.isEmpty()) {
                //saringanTahu(mediaUrl);
                new KangjiSaringanTahu(context, mediaUrl, this::onHasilSaringan);
            }
            else {
                stopPlayer();
            }

        }
    }
/*
    public void saringanTahu(String inputMediaURL) {

        try {
            xURI = new URI(inputMediaURL);
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
                //outputSource = inputSource;
                //ExoKangjiNew.getSharedInstance().playContent(outputSource, false);
                //MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                //        .createMediaSource(Uri.parse(outputSource));
                outputSource = inputMediaURL;
                player.prepare(buildMediaSource(outputSource));
                player.setPlayWhenReady(true);
                break;
            }

            default: {
                try {

                    URL url = new URL(inputMediaURL);
                    String baseUrl = url.getHost().toLowerCase();
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
                                                Log.e(TAG, "(==Extractor==) - YT SOURCE: " + outputSource);
                                                //ExoKangjiNew.getSharedInstance().playContent(outputSource, true);
                                                //return;
                                                //MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                                                //        .createMediaSource(Uri.parse(outputSource));

                                                Uri uri = Uri.parse(outputSource);
                                                // Create a data source factory.
                                                DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory(Util.getUserAgent(context, ConfigPlayerKangji.USER_AGENT));
                                                // Create a progressive media source pointing to a stream uri.
                                                MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
                                                player.prepare(mediaSource);
                                                player.setPlayWhenReady(true);
                                            }
                                        }
                                    }
                                }

                            }
                        }.extract(inputMediaURL, true, true);

                    }
                    else {
                        // LANGSUNG VIDEO LINK, GAK PERLU EXTRACTOR
                        //ExoKangjiNew.getSharedInstance().playContent(outputSource, false);
                        Log.e(TAG, "(==Extractor==) - LANGSUNG VIDEO LINK, GAK PERLU EXTRACTOR" + "\n" + inputMediaURL);
                        //MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                        //        .createMediaSource(Uri.parse(mediaUrl));
                        outputSource = inputMediaURL;
                        player.prepare(buildMediaSource(outputSource));
                        player.setPlayWhenReady(true);
                    }

                }
                catch (MalformedURLException e) {
                    e.printStackTrace();
                    Log.e(TAG, "(= EXTRAK MANGGIS =) - " + e.toString());
                }
                break;
            }
        }

    }

 */

    public void rvPlayerListener() {

        player.addListener(new Player.EventListener() {
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
                        //screenOffFlag();

                        break;
                    case Player.STATE_ENDED:
                        Log.d(TAG, "onPlayerStateChanged: Video ended.");
                        //player.seekTo(0);
                        //screenOffFlag();
                        break;
                    case Player.STATE_IDLE:
                        //screenOffFlag();
                        break;
                    case Player.STATE_READY:
                        Log.e(TAG, "onPlayerStateChanged: Ready to play.");

                        if (onrvProgressBar != null) {
                            onrvProgressBar.setVisibility(GONE);
                        }
                        if (!isVideoViewAdded) {
                            addVideoView();
                        }
                        //screenOnFlag();
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
                /*
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
                 */
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


    public String getFileExtension(Uri uri) {
        String file = uri.toString();
        String fileExtension = "." + FilenameUtils.getExtension(FilenameUtils.getName(file));
        return fileExtension;
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
        onrvMediaContainer.addView(playerView);
        isVideoViewAdded = true;
        playerView.requestFocus();
        playerView.setVisibility(VISIBLE);
        playerView.setAlpha(1);
        onrvThumbnail.setVisibility(GONE);
    }

    private void resetVideoView() {
        if (isVideoViewAdded) {
            removeVideoView(playerView);
            playPosition = -1;
            playerView.setVisibility(INVISIBLE);
            onrvThumbnail.setVisibility(VISIBLE);
        }
    }

    public void releasePlayer() {

        if (player != null) {
            player.release();
            player = null;
        }

        viewHolderParent = null;
    }

    public void stopPlayer() {
        if (player != null) {
            player.stop();
        }
    }

    public void onPausePlayer() {
        if (player != null) {
            player.stop(true);
        }
    }

    private void toggleVolume() {
        if (player != null) {
            if (volumeState == VolumeState.OFF) {
                Log.d(TAG, "togglePlaybackState: enabling volume.");
                setVolumeControl(VolumeState.ON);
                ivVolume.setImageResource(R.drawable.ic_vol_on);
            } else if (volumeState == VolumeState.ON) {
                Log.d(TAG, "togglePlaybackState: disabling volume.");
                setVolumeControl(VolumeState.OFF);
                ivVolume.setImageResource(R.drawable.ic_vol_off);
            }
        }
    }

    private void setVolumeControl(VolumeState state) {
        volumeState = state;
        if (state == VolumeState.OFF) {
            player.setVolume(0f);
            animateVolumeControl();
        } else if (state == VolumeState.ON) {
            player.setVolume(1f);
            animateVolumeControl();
        }
    }

    private void animateVolumeControl() {
        if (ivVolumeAnimate != null) {
            ivVolumeAnimate.bringToFront();
            if (volumeState == VolumeState.OFF) {
                onrvRequestManager.load(R.drawable.ic_vol_off)
                        .into(ivVolumeAnimate);
            } else if (volumeState == VolumeState.ON) {
                onrvRequestManager.load(R.drawable.ic_vol_on)
                        .into(ivVolumeAnimate);
            }
            ivVolumeAnimate.animate().cancel();

            ivVolumeAnimate.setAlpha(1f);

            ivVolumeAnimate.animate()
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
    private void screenOnFlag() {
        ((Activity)context).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void screenOffFlag() {
        ((Activity)context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }     */





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
