package com.blogspot.materialexoplayercustom.player;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.blogspot.materialexoplayercustom.R;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSourceFactory;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.DefaultHlsExtractorFactory;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.upstream.AssetDataSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.util.Util;

import org.apache.commons.io.FilenameUtils;

public class KangjiBuildMediaSource {

    private String TAG = "=== " + KangjiBuildMediaSource.class.getSimpleName() + " ===";
    private Context mContext;
    private String inputVideoString;
    private boolean isYTSource;
    private Uri mUri;
    private String fileExtLowercase;
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();


    public KangjiBuildMediaSource(Context mContext, String inputVideoString, boolean isYtSource) {
        this.mContext = mContext;
        this.inputVideoString = inputVideoString;
        this.isYTSource = isYtSource;
    }

    public MediaSource buildMediaSource() {

        mUri = Uri.parse(inputVideoString);

        if (isYTSource) {
            // Create a data source factory.
            DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory(Util.getUserAgent(mContext, mContext.getString(R.string.app_name)));
            // Create a progressive media source pointing to a stream uri.
            //mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(xUri);
            Log.e(TAG, "isYTSource: " + isYTSource);
            return new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mUri);
        }
        else {
            //mediaSource = buildMediaSource(inputSource);
            Log.e(TAG, "isYTSource: " + isYTSource);


            //mUri = Uri.parse(inputVideoString);
            fileExtLowercase = getFileExtension(mUri).toLowerCase();
            Log.e(TAG, "FILE EXT: " + fileExtLowercase);
            DataSpec dataSpec = new DataSpec(mUri);

            // these are reused for both media sources we create below
            DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            DefaultHttpDataSourceFactory defaultHttpDataSourceFactory = new DefaultHttpDataSourceFactory(ConfigPlayerKangji.USER_AGENT);
            DefaultDashChunkSource.Factory dashChunkSourceFactory = new DefaultDashChunkSource.Factory(new DefaultHttpDataSourceFactory(ConfigPlayerKangji.USER_AGENT, BANDWIDTH_METER));
            DefaultHttpDataSourceFactory manifestDataSourceFactory = new DefaultHttpDataSourceFactory(ConfigPlayerKangji.USER_AGENT);
            RtmpDataSourceFactory rtmpDataSourceFactory = new RtmpDataSourceFactory();

            DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(mContext, ConfigPlayerKangji.USER_AGENT);
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

                            //if (fileExtUppercase.equalsIgnoreCase(".MP3")) {
                            if (fileExtLowercase.equalsIgnoreCase(".mp3")) {
                                Log.e(TAG, "MEDIA TYPE - OTHER - STREAMING - .mp3");
                                return new ProgressiveMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(mUri);
                            }
                            //else if (fileExtUppercase.equalsIgnoreCase(".MP4")) {
                            else if (fileExtLowercase.equalsIgnoreCase(".mp4")) {
                                Log.e(TAG, "MEDIA TYPE - OTHER - STREAMING - .mp4");
                                return new ProgressiveMediaSource.Factory(defaultHttpDataSourceFactory).createMediaSource(mUri);
                            }
                            //else if (fileExtUppercase.equalsIgnoreCase(".M3U8")) {
                            else if (fileExtLowercase.equalsIgnoreCase(".m3u8")) {
                                Log.e(TAG, "MEDIA TYPE - OTHER - STREAMING - .m3u8");
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

    }

    private String getFileExtension(Uri uri) {
        String file = uri.toString();
        String fileExtension = "." + FilenameUtils.getExtension(FilenameUtils.getName(file));
        return fileExtension;
    }


}
