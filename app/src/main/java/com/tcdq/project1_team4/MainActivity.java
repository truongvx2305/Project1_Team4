package com.tcdq.project1_team4;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

/** @noinspection ALL*/
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Chuyển sang màn hình login sau 2s
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent i = new Intent(MainActivity.this, Started.class);
            startActivity(i);
            finish();
        }, 2000);
    }
}