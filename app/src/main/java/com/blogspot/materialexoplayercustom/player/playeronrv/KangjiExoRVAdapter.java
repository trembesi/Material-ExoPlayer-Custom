package com.blogspot.materialexoplayercustom.player.playeronrv;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.materialexoplayercustom.R;
import com.bumptech.glide.RequestManager;
import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;

public class KangjiExoRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<KangjiExoRVItem> mediaObjects;
    private RequestManager requestManager;
    private RequestCreator requestCreator;

    public KangjiExoRVAdapter(ArrayList<KangjiExoRVItem> mediaObjects,
                              RequestManager requestManager) {
        this.mediaObjects = mediaObjects;
        this.requestManager = requestManager;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new KangjiExoRVViewHolder(
                LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_play_on_rv, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((KangjiExoRVViewHolder) viewHolder).onBind(mediaObjects.get(i), requestManager);
    }

    @Override
    public int getItemCount() {
        return mediaObjects.size();
    }

}
