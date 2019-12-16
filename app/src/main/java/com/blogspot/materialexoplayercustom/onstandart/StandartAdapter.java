package com.blogspot.materialexoplayercustom.onstandart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.blogspot.materialexoplayercustom.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class StandartAdapter extends RecyclerView.Adapter<StandartAdapter.ViewHolderStandart> {

    private Context mContext;
    private List<StandartItem> standartList;
    private View itemView;
    private StandartAdapterListener listener;
    private TextDrawable textDrawable;
    private ColorGenerator colorGenerator = ColorGenerator.MATERIAL;

    public class ViewHolderStandart extends RecyclerView.ViewHolder {
        private ImageView ivGambar;
        private TextView tvJudul, tvDeskripsi;

        ViewHolderStandart(View view) {
            super(view);
            ivGambar = view.findViewById(R.id.item_standart_iv_gambar);
            tvJudul = view.findViewById(R.id.item_standart_tv_judul);
            tvDeskripsi = view.findViewById(R.id.item_standart_tv_deskripsi);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onStandartItemSelected(standartList.get(getAdapterPosition()));
                }
            });
        }
    }

    public StandartAdapter(Context mContext, List<StandartItem> standartList, StandartAdapterListener listener) {
        this.mContext = mContext;
        this.standartList = standartList;
        this.listener = listener;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ViewHolderStandart onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_standart, parent, false);
        return new ViewHolderStandart(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolderStandart holder, final int position) {
        final StandartItem item = standartList.get(position);

        holder.tvJudul.setText(item.getJudul());
        holder.tvDeskripsi.setText(item.getDeskripsi());

        final int color = colorGenerator.getColor(item.getJudul());
        if (item.getGambar().isEmpty()) {
            textDrawable = TextDrawable.builder()
                    .buildRect(String.valueOf(item.getJudul().substring(0,2)), color);
            //.buildRoundRect(String.valueOf(itemPL.getPlJudul().charAt(0)), color, 100);
            holder.ivGambar.setImageDrawable(textDrawable);
        }
        else {
            Picasso.get().load(item.getGambar()).into(holder.ivGambar, new Callback() {
                @Override
                public void onSuccess() {
                    //Toast.makeText(mContext, "ON SUCCESS", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(Exception e) {
                    textDrawable = TextDrawable.builder()
                            .buildRect(String.valueOf(item.getJudul().substring(0,2)), color);
                    //.buildRect(String.valueOf(itemPL.getPlJudul().charAt(0)), color);
                    holder.ivGambar.setImageDrawable(textDrawable);
                    //Toast.makeText(mContext, "ON ERROR: " + e, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return standartList.size();
    }

    public interface StandartAdapterListener {
        void onStandartItemSelected(StandartItem standartItem);
    }


}
