package com.blogspot.materialexoplayercustom.uborampe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.materialexoplayercustom.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class GVAlbumAdapter extends RecyclerView.Adapter<GVAlbumAdapter.ViewHolderKu> {

    private Context mContext;
    private List<ModelAlbum> albumList;

    public class ViewHolderKu extends RecyclerView.ViewHolder {
        public TextView tvJudul, tvSubjudul;
        public ImageView gbrPoster, overflow;

        public ViewHolderKu(View view) {
            super(view);
            tvJudul = view.findViewById(R.id.tv_item_judul);
            tvSubjudul = view.findViewById(R.id.tv_item_subjudul);
            gbrPoster = view.findViewById(R.id.iv_item_gambar);
        }
    }

    public GVAlbumAdapter(Context mContext, List<ModelAlbum> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public ViewHolderKu onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_album, parent, false);

        return new ViewHolderKu(itemView);
    }

    @Override
    public void onBindViewHolder(final  ViewHolderKu holderKu, int position) {
        ModelAlbum album = albumList.get(position);
        holderKu.tvJudul.setText(album.get_judul());
        holderKu.tvSubjudul.setText(album.get_subjudul());
        Glide.with(mContext).load(album.get_gambarposter()).into(holderKu.gbrPoster);
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }



}
