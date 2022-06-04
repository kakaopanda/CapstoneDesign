package com.example.capstonedesign;

import static com.example.capstonedesign.LoginSecondActivity.loginId;
import static com.example.capstonedesign.MainActivity.filePath;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LoadingActivity extends AppCompatActivity {
    private ExecutorService executorService = Executors.newFixedThreadPool(5);
    public static PillModel pillModel;
    public static Bitmap pillBitmap;
    public static ComponentModel componentModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        load();
    }

    public void load() {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    // 이미지 정보(ID, 파일 경로) 데이터베이스에 저장
                    Callable<String> saveImageInfo = new SaveImageInfo(loginId, filePath);
                    Future<String> siFuture = executorService.submit(saveImageInfo);
                    siFuture.get();
                    // 이미지 파일 서버로 업로드
                    Callable<String> uploadImage = new UploadImage(filePath);
                    Future<String> uiFuture = executorService.submit(uploadImage);
                    uiFuture.get();
                    // 이미지 분석
                    Callable<PillModel> analyzeImage = new AnalyzeImage(loginId, filePath);
                    Future<PillModel> pillModelFuture = executorService.submit(analyzeImage);
                    pillModel = pillModelFuture.get();
                    // 분석 결과에 맞는 알약의 이미지를 비트맵 형태로 다운로드
                    Callable<Bitmap> downloadImage = new DownloadImage(pillModel.pill_serial);
                    Future<Bitmap> dlFuture = executorService.submit(downloadImage);
                    pillBitmap = dlFuture.get();
                    // 주성분 정보 받아오기
                    if(pillModel.component != null) {
                        try {
                            Callable<ComponentModel> getComponent = new GetComponent(pillModel.component);
                            Future<ComponentModel> cpFuture = executorService.submit(getComponent);
                            componentModel = cpFuture.get();
                        } catch (Exception e) {
                            Log.e("Component", e.toString());
                        }
                    }
                    // 모든 작업이 성공적으로 끝날 경우 다음 화면으로 전환
                    startActivity(new Intent(getApplicationContext(), AnalyzeActivity.class));
                } catch(Exception e) {
                    // 분석이 실패할 경우 실패 화면으로 전환
                    startActivity(new Intent(getApplicationContext(), AnalyzeFailActivity.class));
                    Log.e("Call", e.toString());
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