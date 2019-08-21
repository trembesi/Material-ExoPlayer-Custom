package com.blogspot.materialexoplayercustom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.blogspot.materialexoplayercustom.player.ConfigPlayerKangji;
import com.blogspot.materialexoplayercustom.tls.MeksoTLS;
import com.blogspot.materialexoplayercustom.uborampe.GVAlbumAdapter;
import com.blogspot.materialexoplayercustom.uborampe.ModelAlbum;
import com.blogspot.materialexoplayercustom.uborampe.RecyclerMakCenulListener;
import com.blogspot.materialexoplayercustom.uborampe.StelKendo;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String TAG = "=== " + MainActivity.class.getSimpleName() + " ===";

    private String judul, subjudul, url, genre, gambarPoster, gambarFull, deskripsi;
    private EditText etURL;
    private Button btnPlay;

    private CoordinatorLayout coordinatorLayoutkonten;
    private Snackbar snackbarDaftarKonten;
    private SpinKitView spinKitViewlist;

    private RecyclerView rvAlbum;
    private GVAlbumAdapter adapter;
    private List<ModelAlbum> albumList;

    private StelKendo stelKendo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        MeksoTLS meksoTLS = new MeksoTLS(this);
        meksoTLS.peksoTLSv12();
        notoAlbum();
    }


    @Override
    protected void onPause() {
        super.onPause();
        //mAdviewMain.pause();
        //Toast.makeText(this, TAG + " - " + "ON PAUSE", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mAdviewMain.resume();
        //Toast.makeText(this, TAG + " - " + "ON RESUME", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        //Toast.makeText(this, TAG + " - " + "ON DESTROY", Toast.LENGTH_SHORT).show();
    }

    private void notoMain() {

    }

    private void notoAlbum() {
        stelKendo = new StelKendo(this);

        coordinatorLayoutkonten = findViewById(R.id.coordinat_layout_main);
        spinKitViewlist = findViewById(R.id.spinkit_album);
        spinKitViewlist.setVisibility(View.GONE);
        rvAlbum = findViewById(R.id.rv_album);
        etURL = findViewById(R.id.et_go);
        btnPlay = findViewById(R.id.btn_go_play);

        albumList = new ArrayList<>();
        adapter = new GVAlbumAdapter(this, albumList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        rvAlbum.setLayoutManager(mLayoutManager);
        rvAlbum.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        rvAlbum.setItemAnimator(new DefaultItemAnimator());
        rvAlbum.setAdapter(adapter);
        notoGrid();

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = etURL.getText().toString().trim();
                if (url.equals(null)) {
                    Toast.makeText(MainActivity.this, "EMPTY DATA !!", Toast.LENGTH_SHORT).show();
                }
                else {
                    //Toast.makeText(GamblangActivity.this, dataURL, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, LayarGedhiActivity.class);
                    intent.putExtra(ConfigPlayerKangji.KEY_VIDEO_URI, url);
                    startActivity(intent);
                }
            }
        });


        rvAlbum.addOnItemTouchListener(new RecyclerMakCenulListener(this, rvAlbum, new RecyclerMakCenulListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                ModelAlbum modelAlbum = albumList.get(position);
                //Toast.makeText(MainActivity.this, modelAlbum.get_url(), Toast.LENGTH_SHORT).show();
                //ndelokLayarTancep(modelAlbum.get_url());
                Intent intent = new Intent(MainActivity.this, LayarCilikActivity.class);
                intent.putExtra(ConfigPlayerKangji.KEY_VIDEO_URI, modelAlbum.get_url());
                intent.putExtra(ConfigPlayerKangji.KEY_VIDEO_JUDUL, modelAlbum.get_judul());
                intent.putExtra(ConfigPlayerKangji.KEY_VIDEO_DESKRIPSI, modelAlbum.get_deskripsi());
                intent.putExtra(ConfigPlayerKangji.KEY_VIDEO_GAMBAR_FULL, modelAlbum.get_gambarfull());
                startActivity(intent);

            }

            @Override
            public void onLongClick(View view, int position) {
                ModelAlbum modelAlbum = albumList.get(position);
                Toast.makeText(MainActivity.this,
                        "title: " + modelAlbum.get_judul() + "\n"
                                + "desc: " + modelAlbum.get_subjudul() + "\n"
                                + "genre: " + modelAlbum.get_genre(), Toast.LENGTH_LONG).show();
            }
        }));
    }

    private void notoGrid() {
        spinKitViewlist.setVisibility(View.VISIBLE);
        //String urlKonten = StelKendo.URL_KONTEN;
        String urlKonten = stelKendo.getServerKontenURL();
        StringRequest stringRequestKonten = new StringRequest(urlKonten, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                spinKitViewlist.setVisibility(View.GONE);
                try {

                    JSONArray jsonArray = null;
                    JSONObject jsonObject = new JSONObject(response);
                    jsonArray = jsonObject.getJSONArray(StelKendo.KEY_ARRAY_RESPONSE);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jo = jsonArray.getJSONObject(i);
                        judul = jo.getString(StelKendo.KEY_ARRAY_JUDUL);
                        subjudul = jo.getString(StelKendo.KEY_ARRAY_SUBJUDUL);
                        genre = jo.getString(StelKendo.KEY_ARRAY_GENRE);
                        url = jo.getString(StelKendo.KEY_ARRAY_URL);
                        gambarPoster = jo.getString(StelKendo.KEY_ARRAY_GAMBAR_POSTER);
                        gambarFull = jo.getString(StelKendo.KEY_ARRAY_GAMBAR_FULL);
                        deskripsi = jo.getString(StelKendo.KEY_ARRAY_DESKRIPSI);

                        albumList.add(new ModelAlbum(judul, subjudul, genre, url, gambarPoster, gambarFull, deskripsi));
                    }
                    adapter.notifyDataSetChanged();
                    //golekDuitMain();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                snackbarDaftarKonten = Snackbar.make(coordinatorLayoutkonten, getString(R.string.notif_server_error0)
                        + "\n"
                        + getString(R.string.notif_server_error1) , Snackbar.LENGTH_INDEFINITE);
                View sbView = snackbarDaftarKonten.setAction(getString(R.string.snackbar_btn_tutup), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbarDaftarKonten.dismiss();
                        finish();
                    }
                }).getView();

                //TextView tvSnack = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                TextView tvSnack = (TextView) sbView.findViewById(com.google.android.material.R.id.snackbar_text);
                tvSnack.setTextColor(Color.YELLOW);
                snackbarDaftarKonten.show();
                spinKitViewlist.setVisibility(View.GONE);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        stringRequestKonten.setShouldCache(false);
        stringRequestKonten.setRetryPolicy(new DefaultRetryPolicy(
                StelKendo.TAG_SOCKET_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(stringRequestKonten);

    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


}
