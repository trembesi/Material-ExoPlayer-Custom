package com.blogspot.materialexoplayercustom.onrecyclerview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blogspot.materialexoplayercustom.ConfigMediaOnRV;
import com.blogspot.materialexoplayercustom.R;
import com.blogspot.materialexoplayercustom.player.playeronrv.ExoKangjiRecyclerView;
import com.blogspot.materialexoplayercustom.player.playeronrv.KangjiExoRVAdapter;
import com.blogspot.materialexoplayercustom.player.playeronrv.KangjiExoRVItem;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class PlayOnRVActivity extends AppCompatActivity {

    private static final String TAG = "=== " + PlayOnRVActivity.class.getSimpleName() + " ===";

    private ProgressBar pbOnRV;
    private TextView tvStatusOnRV;
    private ExoKangjiRecyclerView rvExoKangji;
    private KangjiExoRVItem mediaItem;
    private ArrayList<KangjiExoRVItem> mediaObjectList = new ArrayList<>();
    private KangjiExoRVAdapter adapter;
    private boolean firsttime = true;
    private String thumbnail, header, body, footer, mediaVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_on_rv);
        notoPlayOnRV();
        muatDataMedia();
    }
    @Override
    protected void onDestroy() {
        if (rvExoKangji != null) {
            rvExoKangji.releasePlayer();
        }
        super.onDestroy();
    }

    private void notoPlayOnRV() {

        pbOnRV = findViewById(R.id.act_onrv_pb);
        pbOnRV.setVisibility(View.GONE);

        tvStatusOnRV = findViewById(R.id.act_onrv_tv_status);
        tvStatusOnRV.setVisibility(View.GONE);

        rvExoKangji = findViewById(R.id.act_onrv_rv);
        rvExoKangji.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rvExoKangji.setLayoutManager(llm);
        rvExoKangji.setItemAnimator(new DefaultItemAnimator());
        //rvExoKangji.addItemDecoration(new KangjiRvDividerItemDecoration(PlayOnRVActivity.this, DividerItemDecoration.VERTICAL, 50));

        //rvExoKangji.setMediaObjects(mediaObjectList);
        //adapter = new KangjiExoRVAdapter(mediaObjectList, notoGlide());
        //rvExoKangji.setAdapter(adapter);

        if (firsttime) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    rvExoKangji.playVideo(false);
                }
            });
            firsttime = false;
        }
    }

    private RequestManager notoGlide() {
        RequestOptions options = new RequestOptions();
        return Glide.with(this).setDefaultRequestOptions(options);
    }

    private String loadJSONFromAsset(String filenameWithExtension) {
        String json = null;
        try {
            InputStream is = getAssets().open(filenameWithExtension);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    private void muatDataMedia() {

        pbOnRV.setVisibility(View.VISIBLE);
        tvStatusOnRV.setVisibility(View.GONE);

        try {
            pbOnRV.setVisibility(View.GONE);

            JSONObject jsonObject = new JSONObject(loadJSONFromAsset("media_on_rv.json"));
            JSONArray jsonArray = jsonObject.getJSONArray(ConfigMediaOnRV.ARRAY_RESPONSE);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);

                JSONArray jsonArrayDataMedia = object.getJSONArray(ConfigMediaOnRV.ARRAY_DATA_MEDIA);
                for (int idm = 0; idm < jsonArrayDataMedia.length(); idm++) {
                    JSONObject jsonObjectDataMedia = jsonArrayDataMedia.getJSONObject(idm);
                    header = jsonObjectDataMedia.getString(ConfigMediaOnRV.OBJECT_MEDIA_HEADER);
                    body = jsonObjectDataMedia.getString(ConfigMediaOnRV.OBJECT_MEDIA_BODY);
                    footer = jsonObjectDataMedia.getString(ConfigMediaOnRV.OBJECT_MEDIA_FOOTER);
                    thumbnail = jsonObjectDataMedia.getString(ConfigMediaOnRV.OBJECT_MEDIA_THUMBNAIL);
                    mediaVideo = jsonObjectDataMedia.getString(ConfigMediaOnRV.OBJECT_MEDIA_VIDEO_PATH);

                    //standartItem = new StandartItem(mediaGambar, mediaJudul, mediaDeskripsi, mediaVideoPath);
                    //standartList.add(standartItem);
                    mediaItem = new KangjiExoRVItem(header, body, footer, thumbnail, mediaVideo);
                    mediaObjectList.add(mediaItem);

                    Log.e(TAG, jsonObjectDataMedia.toString());
                }

                Log.e(TAG, object.toString());
            }
            //rvStandart.setAdapter(adapter);
            //adapter.notifyDataSetChanged();
            rvExoKangji.setMediaObjects(mediaObjectList);
            adapter = new KangjiExoRVAdapter(mediaObjectList, notoGlide());
            rvExoKangji.setAdapter(adapter);
        }
        catch (JSONException e) {
            pbOnRV.setVisibility(View.GONE);
            tvStatusOnRV.setText("ERROR");
            tvStatusOnRV.setVisibility(View.VISIBLE);
            e.printStackTrace();
        }

    }


}
