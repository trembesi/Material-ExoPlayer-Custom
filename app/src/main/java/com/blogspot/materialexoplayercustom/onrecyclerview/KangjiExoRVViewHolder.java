package com.blogspot.materialexoplayercustom.onrecyclerview;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.materialexoplayercustom.R;
import com.bumptech.glide.RequestManager;

public class KangjiExoRVViewHolder extends RecyclerView.ViewHolder {
    /**
     * below view have public modifier because
     * we have access PlayerViewHolder inside the ExoPlayerRecyclerView
     */
    public FrameLayout onrvFrameContainer;
    public ImageView onrvIVThumbnail, onrvIVVolumeControl;
    public ProgressBar onrvProgressBar;
    public RequestManager onrvRequestManager;
    private TextView onrvTVHeader, onrvTVBody, onrvTVFooter, onrvTVErrorPlayer;
    private View parent;

    public KangjiExoRVViewHolder(@NonNull View itemView) {
        super(itemView);
        parent = itemView;

        onrvTVHeader = itemView.findViewById(R.id.onrv_tv_header);
        onrvTVBody = itemView.findViewById(R.id.onrv_tv_body);
        onrvTVFooter = itemView.findViewById(R.id.onrv_tv_footer);
        onrvIVThumbnail = itemView.findViewById(R.id.onrv_iv_thumbnail);
        onrvFrameContainer = itemView.findViewById(R.id.onrv_frame_container);
        onrvProgressBar = itemView.findViewById(R.id.onrv_pb);
        onrvIVVolumeControl = itemView.findViewById(R.id.onrv_iv_volume_control);
        onrvTVErrorPlayer = itemView.findViewById(R.id.onrv_tv_error_player);
    }

    void onBind(KangjiExoRVItem mediaObject, RequestManager requestManager) {
        this.onrvRequestManager = requestManager;
        parent.setTag(this);
        /*
        onrvTVHeader.setText(mediaObject.getHeader());
        onrvTVBody.setText(mediaObject.getBody());
        onrvTVFooter.setText(mediaObject.getFooter());
        this.onrvRequestManager
                .load(mediaObject.getThumbnail())
                .into(onrvIVThumbnail);
         */

        String header = mediaObject.getHeader();
        String body = mediaObject.getBody();
        String footer = mediaObject.getFooter();
        String thumbnail = mediaObject.getThumbnail();
        String videoLink = mediaObject.getVideoURL();

        if (header.isEmpty()) {
            onrvTVHeader.setVisibility(View.GONE);
        }
        else {
            onrvTVHeader.setText(header);
            onrvTVHeader.setVisibility(View.VISIBLE);
        }

        if (body.isEmpty()) {
            onrvTVBody.setVisibility(View.GONE);
        }
        else {
            onrvTVBody.setText(body);
            onrvTVBody.setVisibility(View.VISIBLE);
        }

        if (footer.isEmpty()) {
            onrvTVFooter.setVisibility(View.GONE);
        }
        else {
            onrvTVFooter.setText(footer);
            onrvTVFooter.setVisibility(View.VISIBLE);
        }

        if (videoLink.isEmpty()) {
            onrvFrameContainer.setVisibility(View.GONE);
            onrvIVVolumeControl.setVisibility(View.GONE);
            onrvIVThumbnail.setVisibility(View.GONE);
        }
        else {
            onrvFrameContainer.setVisibility(View.VISIBLE);
            onrvIVVolumeControl.setVisibility(View.VISIBLE);
            onrvIVThumbnail.setVisibility(View.VISIBLE);
        }

        if (! thumbnail.isEmpty()) {
            this.onrvRequestManager
                    .load(mediaObject.getThumbnail())
                    .into(onrvIVThumbnail);
        }

    }
}
