package com.example.capstonedesign;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TextAnalyzeActivity extends AppCompatActivity {
    public static PillModel textPillModel;
    public static Bitmap textPillBitmap;
    public static ComponentModel textComponentModel;
    private String colorText = "하양";
    private String shapeText = "장방형";
    private ExecutorService executorService = Executors.newFixedThreadPool(3);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        applyColors();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_analyze);

        EditText textTextView;
        Spinner colorSpinner, shapeSpinner;
        ImageView submitBtn = findViewById(R.id.text_submit_btn);
        String[] colorItems = {"하양", "검정", "노랑", "빨강", "초록", "파랑"};
        String[] shapeItems = {"장방형", "원형", "타원형", "사각형", "팔각형"};

        textTextView = findViewById(R.id.text_input_edit);
        colorSpinner = findViewById(R.id.color_spinner);
        shapeSpinner = findViewById(R.id.shape_spinner);
        ArrayAdapter<String> colorAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, colorItems);
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpinner.setAdapter(colorAdapter);
        ArrayAdapter<String> shapeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, shapeItems);
        shapeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shapeSpinner.setAdapter(shapeAdapter);

        // 색상 선택
        colorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                colorText = colorItems[i];
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // 제형 선택
        shapeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                shapeText = shapeItems[i];
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // 제출하기 버튼
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textText = textTextView.getText().toString();
                Toast analyzeFailedToast = Toast.makeText(getApplicationContext(),"일치하는 알약이 없습니다.",Toast.LENGTH_LONG);
                if(!colorText.isEmpty() && !shapeText.isEmpty() && !textText.isEmpty()) {
                    executorService.submit(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // 유저가 입력한 알약 정보로 이미지 분석
                                Callable<PillModel> analyzeText = new AnalyzeText(colorText, shapeText, textText);
                                Future<PillModel> pillModelFuture = executorService.submit(analyzeText);
                                textPillModel = pillModelFuture.get();
                                // 분석 결과에 맞는 알약의 이미지를 비트맵 형태로 다운로드
                                Callable<Bitmap> downloadImage = new DownloadImage(textPillModel.pill_serial);
                                Future<Bitmap> dlFuture = executorService.submit(downloadImage);
                                textPillBitmap = dlFuture.get();
                                // 주성분 정보 받아오기
                                if(textPillModel.component != null) {
                                    Callable<ComponentModel> getComponent = new GetComponent(textPillModel.component);
                                    Future<ComponentModel> cpFuture = executorService.submit(getComponent);
                                    textComponentModel = cpFuture.get();
                                }
                                // 모든 작업이 성공적으로 끝날 경우 다음 화면으로 전환
                                startActivity(new Intent(getApplicationContext(), AnalyzeActivity.class));
                            } catch(Exception e) {
                                // 분석이 실패할 경우 토스트메시지 출력
                                Log.e("Component","Component error: " + e.toString());
                                analyzeFailedToast.show();
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(getApplicationContext(),"빈칸 없이 입력해주세요.",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void applyColors() {
        getWindow().setStatusBarColor(Color.parseColor("#FF1073B4"));
        getWindow().setNavigationBarColor(Color.parseColor("#FF1073B4"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }
}
