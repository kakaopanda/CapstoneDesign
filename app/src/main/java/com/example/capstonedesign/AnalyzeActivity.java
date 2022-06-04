package com.example.capstonedesign;

import static android.widget.Toast.makeText;

import static com.example.capstonedesign.LoadingActivity.componentModel;
import static com.example.capstonedesign.LoadingActivity.pillBitmap;
import static com.example.capstonedesign.LoadingActivity.pillModel;
import static com.example.capstonedesign.TextAnalyzeActivity.textComponentModel;
import static com.example.capstonedesign.TextAnalyzeActivity.textPillBitmap;
import static com.example.capstonedesign.TextAnalyzeActivity.textPillModel;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AnalyzeActivity extends AppCompatActivity {
    // JAVA Object
    private Animation opacityAnim, scaleAnim, scaleAnim2, rotateAnim;
    private int textPhase = 1;
    private long backKeyPressedTime = 0;
    private boolean infoFlag = true;

    // XML Object
    private ImageView subtitute_btn, capture_btn, medicine_front, medicine_background1, medicine_background2, size_btn, analyze_result_box;
    private String name, serial, division, appearance, business_name, classification, component_name;
    private String component_code, type_name, injection_root, injection_unit, injection_day;
    private TextView name_text, serial_text, division_text, appearance_text, business_name_text, classification_text, component_text;
    private TextView name_info, serial_info, division_info, appearance_info, business_name_info, classification_info, component_info;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        applyColors();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);

        // 컴포넌트 설정
        medicine_front = findViewById((R.id.medicine_front));
        medicine_background1 = findViewById(R.id.medicine_background1);
        scaleAnim = AnimationUtils.loadAnimation(this, R.anim.scale);
        medicine_background1.setAnimation(scaleAnim);
        medicine_background2 = findViewById(R.id.medicine_background2);
        scaleAnim2 = AnimationUtils.loadAnimation(this, R.anim.scale2);
        medicine_background2.setAnimation(scaleAnim2);

        name_info = findViewById(R.id.name);
        opacityAnim = AnimationUtils.loadAnimation(this, R.anim.opacity);
        name_info.setAnimation(opacityAnim);

        name_text = findViewById(R.id.name);
        serial_text = findViewById(R.id.serial);
        serial_info = findViewById(R.id.serial_info);
        division_text = findViewById(R.id.division);
        division_info = findViewById(R.id.division_info);
        business_name_text = findViewById(R.id.pharmacist);
        business_name_info = findViewById(R.id.pharmacist_info);
        classification_text = findViewById(R.id.classification);
        classification_info = findViewById(R.id.classification_info);
        component_text = findViewById(R.id.ingredient);
        component_info = findViewById(R.id.ingredient_info);
        appearance_text = findViewById(R.id.appearance);
        appearance_info = findViewById(R.id.appearance_info);

        // 이미지로 분석한 경우
        if (pillModel != null) {
            name = pillModel.pill_name;
            serial = pillModel.pill_serial;
            appearance = pillModel.appearance;
            classification = pillModel.classify;
            business_name = pillModel.business_name;
            division = pillModel.is_prescription;
            component_name = pillModel.component;
            medicine_front.setImageBitmap(pillBitmap);
            // 주성분 정보가 있는 경우
            if(componentModel != null) {
                component_code = componentModel.component_code;
                type_name = componentModel.type_name;
                injection_root = componentModel.injection_root;
                injection_unit = componentModel.injection_unit;
                injection_day = componentModel.injection_day;
            }
        }
        // 유저가 텍스트로 직접 입력하여 분석한 경우
        else {
            name= textPillModel.pill_name;
            serial = textPillModel.pill_serial;
            appearance = textPillModel.appearance;
            classification = textPillModel.classify;
            business_name = textPillModel.business_name;
            division = textPillModel.is_prescription;
            component_name = textPillModel.component;
            medicine_front.setImageBitmap(textPillBitmap);
            // 주성분 정보가 있는 경우
            if(textComponentModel != null) {
                component_code = textComponentModel.component_code;
                type_name = textComponentModel.type_name;
                injection_root = textComponentModel.injection_root;
                injection_unit = textComponentModel.injection_unit;
                injection_day = textComponentModel.injection_day;
            }
        }

        // 알약 정보 출력
        name_info.setText(name);
        serial_info.setText(serial);
        appearance_info.setText(appearance);
        classification_info.setText(classification);
        business_name_info.setText(business_name);
        division_info.setText(division);
        component_info.setText(component_name);
        
        // 정보박스 터치 시 표기정보 전환
        analyze_result_box = findViewById(R.id.analyze_result_box);
        analyze_result_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(componentModel != null || textComponentModel != null) {
                    if (infoFlag) {
                        // 현재 출력된 정보가 의약품 분석결과 중 일련번호, 구분, 성상, 제약사, 분류명, 주성분을 제시하는 경우
                        serial_text.setText("성분코드");
                        division_text.setText("성  분  명");
                        component_text.setText("제  형  명");
                        appearance_text.setText("일투여량");
                        business_name_text.setText("투여경로");
                        classification_text.setText("투여단위");
                        serial_info.setText(component_code);
                        division_info.setText(component_name);
                        component_info.setText(type_name);
                        business_name_info.setText(injection_root);
                        classification_info.setText(injection_unit);
                        appearance_info.setText(injection_day);
                        infoFlag = false;
                    } else {
                        // 현재 출력된 정보가 의약품 분석결과 중 일련번호, 구분, 성상, 투여경로, 투여단위, 1일 최대투여량을 제시하는 경우
                        serial_text.setText("일련번호");
                        division_text.setText("구        분");
                        component_text.setText("주  성  분");
                        business_name_text.setText("제  약  사");
                        classification_text.setText("분  류  명");
                        appearance_text.setText("성        상");
                        serial_info.setText(serial);
                        component_info.setText(component_name);
                        classification_info.setText(classification);
                        business_name_info.setText(business_name);
                        division_info.setText(division);
                        appearance_info.setText(appearance);
                        infoFlag = true;
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "주성분 정보가 없습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });

        // 글씨 크기 조절 버튼
        size_btn = findViewById(R.id.size_btn);
        size_btn.setOnClickListener(new View.OnClickListener() {
            int textSize = 11;
            @Override
            public void onClick(View view) {
                if(textSize < 13){
                    ++textSize;
                    ++textPhase;
                }
                else{
                    textSize = 11;
                    textPhase = 1;
                }
                serial_text.setTextSize(textSize);
                serial_info.setTextSize(textSize);
                division_text.setTextSize(textSize);
                division_info.setTextSize(textSize);
                appearance_text.setTextSize(textSize);
                appearance_info.setTextSize(textSize);
                business_name_text.setTextSize(textSize);
                business_name_info.setTextSize(textSize);
                classification_text.setTextSize(textSize);
                classification_info.setTextSize(textSize);
                component_text.setTextSize(textSize);
                component_info.setTextSize(textSize);
                makeText(getApplicationContext(), "글자 크기를 "+textPhase+"단계로 변경합니다.",Toast.LENGTH_SHORT).show();
            }
        });
        rotateAnim = AnimationUtils.loadAnimation(this, R.anim.rotate2);
        size_btn.setAnimation(rotateAnim);

        // 대체성분 분석 버튼
        subtitute_btn = findViewById(R.id.subtitute_btn);
        subtitute_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "준비 중입니다.", Toast.LENGTH_SHORT).show();
                // startActivity(new Intent(getApplicationContext(), SubstituteActivity.class));
            }
        });

        // 분석결과 공유 버튼
        capture_btn = findViewById(R.id.capture_btn);
        capture_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScreenShot();
            }
        });
    }

    // FileProvider의 Content URI를 통해 스크린샷 이미지를 공유한다.
    public void ScreenShot(){
        View view = getWindow().getDecorView().getRootView(); // 전체화면을 담은, 현재 Activity의 최상위 View(Root View)를 가져온다.
        view.setDrawingCacheEnabled(true); // Cache에 해당 View의 이미지를 저장한다.
        // 현재 Cache에 저장되어있는 이미지를 Bitmap 형태로 변환하여 저장한다.
        Bitmap screenBitmap = Bitmap.createBitmap(view.getDrawingCache());
        try {
            // 애플리케이션의 임시 Cache 파일을 저장하는 디렉토리를 생성한다.
            File cachePath = new File(getApplicationContext().getCacheDir(), "images");
            cachePath.mkdirs();

            // 이미지 파일을 쓰기 위한 스트림을 선언한 뒤, compress()를 통해 스트림에 Bitmap을 저장한다.
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png");
            screenBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            // 이미지 파일의 Bitmap이 존재하는 cachePath에 문자열로 경로를 생성하여 File 객체를 생성한다.
            File newFile = new File(cachePath, "image.png");
            // FileProvider.getUriForFile()에 File 객체를 넘긴 뒤, Content URI를 반환받는다.
            Uri contentUri = FileProvider.getUriForFile(getApplicationContext(),
                    "com.example.capstonedesign.fileprovider", newFile);

            // 공유 인텐트(ACTION_SEND)를 지정한 새로운 Intent를 생성하여, Activity 간 데이터 전송을 수행한다.
            Intent Sharing_intent = new Intent(Intent.ACTION_SEND);
            Sharing_intent.setType("image/png");

            // Extra Data는 인텐트 간 공유할 실제 데이터를 저장하며, Key/Value 쌍의 구조이다.
            // Key는 이미지 파일의 MIME 형식(전송되는 문서의 형식)인 Intent.EXTRA_STREAM 으로 지정한다.
            Sharing_intent.putExtra(Intent.EXTRA_STREAM, contentUri);
            startActivity(Intent.createChooser(Sharing_intent, "Share image"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    private void applyColors() {
        getWindow().setStatusBarColor(Color.parseColor("#FF1073B4"));
        getWindow().setNavigationBarColor(Color.parseColor("#FF1073B4"));
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "한번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG).show();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
            NotificationManagerCompat.from(this).cancel(1);
            finishAffinity(); // 해당 애플리케이션의 루트 액티비티 종료
            moveTaskToBack(true); // 태스크를 백그라운드로 이동
            finishAndRemoveTask(); // 액티비티 종료 + 태스크 리스트에서 지우기
            android.os.Process.killProcess(android.os.Process.myPid()); // 앱 프로세스 종료
        }
    }
}
