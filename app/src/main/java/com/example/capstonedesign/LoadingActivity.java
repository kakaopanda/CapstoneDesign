package com.example.capstonedesign;

import static com.example.capstonedesign.LoginSecondActivity.loginId;
import static com.example.capstonedesign.MainActivity.filePath;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LoadingActivity extends AppCompatActivity {
    private ExecutorService executorService = Executors.newFixedThreadPool(2);
    public static PillModel pillModel;
    public static Bitmap pillBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        getPill();
    }

    public void getPill() {
        Callable cb = new AnalyzeImage(loginId, filePath);
        Future<PillModel> pillModelFuture = executorService.submit(cb);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    pillModel = pillModelFuture.get();
                    downloadImage();
                } catch(Exception e) {
                    Log.e("Call", e.toString());
                }
            }
        });
    }

    public void downloadImage() {
        Callable<Bitmap> cb = new DownloadImage(pillModel.pill_serial);
        Future<Bitmap> future = executorService.submit(cb);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    pillBitmap = future.get();
                    startActivity(new Intent(getApplicationContext(), AnalyzeActivity.class));
                } catch (Exception e) {
                    Log.e("Download Image", e.toString());
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }
}