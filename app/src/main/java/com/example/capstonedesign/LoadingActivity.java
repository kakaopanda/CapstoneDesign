package com.example.capstonedesign;

import static com.example.capstonedesign.LoginSecondActivity.loginId;
import static com.example.capstonedesign.MainActivity.filePath;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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

    // JAVA Object
    private Animation rotateAnim;

    // XML Object
    private ImageView glass_image;
    private TextView progress_description;
    private TextView first_step, second_step, third_step, fourth_step;
    private ProgressBar first_circle, second_circle, third_circle, fourth_circle;
    private ImageView first_check, second_check, third_check, fourth_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        progress_description = findViewById(R.id.progress_description);
        rotateAnim = AnimationUtils.loadAnimation(this, R.anim.rotate4);
        glass_image = findViewById(R.id.glass_image);
        glass_image.setAnimation(rotateAnim);

        first_step = findViewById(R.id.first_step);
        second_step = findViewById(R.id.second_step);
        third_step = findViewById(R.id.third_step);
        fourth_step = findViewById(R.id.fourth_step);

        first_circle = findViewById(R.id.first_circle);
        second_circle = findViewById(R.id.second_circle);
        third_circle = findViewById(R.id.third_circle);
        fourth_circle = findViewById(R.id.fourth_circle);

        first_check = findViewById(R.id.first_check);
        second_check = findViewById(R.id.second_check);
        third_check = findViewById(R.id.third_check);
        fourth_check = findViewById(R.id.fourth_check);
        load();
    }

    public void load() {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    // 이미지 정보(ID, 파일 경로) 데이터베이스에 저장
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // runOnUiThread를 추가하고 그 안에 UI작업을 한다.
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progress_description.setText("이미지 정보 데이터베이스 업로드 중..");
                                    first_circle.setVisibility(View.VISIBLE);
                                    first_step.setTextColor(Color.WHITE);
                                }
                            });
                        }
                    }).start();
                    Callable<String> saveImageInfo = new SaveImageInfo(loginId, filePath);
                    Future<String> siFuture = executorService.submit(saveImageInfo);
                    siFuture.get();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // runOnUiThread를 추가하고 그 안에 UI작업을 한다.
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progress_description.setText("이미지 정보 서버 업로드 중..");
                                    first_check.setVisibility(View.VISIBLE);
                                    first_circle.setVisibility(View.INVISIBLE);

                                    second_step.setTextColor(Color.WHITE);
                                    second_circle.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    }).start();
                    Callable<String> uploadImage = new UploadImage(filePath);
                    Future<String> uiFuture = executorService.submit(uploadImage);
                    uiFuture.get();

                    // 이미지 분석
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // runOnUiThread를 추가하고 그 안에 UI작업을 한다.
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progress_description.setText("이미지 정보 분석 중..");
                                    second_check.setVisibility(View.VISIBLE);
                                    second_circle.setVisibility(View.INVISIBLE);

                                    third_step.setTextColor(Color.WHITE);
                                    third_circle.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    }).start();
                    Callable<PillModel> analyzeImage = new AnalyzeImage(loginId, filePath);
                    Future<PillModel> pillModelFuture = executorService.submit(analyzeImage);
                    pillModel = pillModelFuture.get();

                    // 분석 결과에 맞는 알약의 이미지를 비트맵 형태로 다운로드
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // runOnUiThread를 추가하고 그 안에 UI작업을 한다.
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progress_description.setText("분석 결과 다운로드 중..");
                                    third_check.setVisibility(View.VISIBLE);
                                    third_circle.setVisibility(View.INVISIBLE);

                                    fourth_step.setTextColor(Color.WHITE);
                                    fourth_circle.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    }).start();
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

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // runOnUiThread를 추가하고 그 안에 UI작업을 한다.
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progress_description.setText("분석 결과 다운로드 완료");
                                    fourth_check.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    }).start();

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