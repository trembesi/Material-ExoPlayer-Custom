package com.blogspot.materialexoplayercustom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.blogspot.materialexoplayercustom.onrecyclerview.PlayOnRVActivity;
import com.blogspot.materialexoplayercustom.onstandart.PlayOnStandartActivity;

public class MainActivity extends AppCompatActivity {

    private String TAG = "=== " + MainActivity.class.getSimpleName() + " ===";
    private Button btnActStandart, btnActRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notoMain();
    }

    private void notoMain() {
        btnActStandart = findViewById(R.id.main_btn_act_standart);
        btnActStandart.setText("Standart Activity");
        btnActStandart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PlayOnStandartActivity.class));
            }
        });

        btnActRV = findViewById(R.id.main_btn_act_rv);
        btnActRV.setText("RecyclerView Activity");
        btnActRV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PlayOnRVActivity.class));
            }
        });
    }
}
