package com.example.capstonedesign;

import static android.widget.Toast.makeText;

import static com.example.capstonedesign.LoadingActivity.pillModel;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
    private Toast toast;
    private int textPhase = 1;
    private long backKeyPressedTime = 0;
    private boolean status = false;

    // XML Object
    private ImageView subtitute_btn, capture_btn, medicine_background1, medicine_background2, size_btn, analyze_result_box;
    private TextView name, serial, division, appearance, pharmacist, classification, ingredient;
    private TextView serial_info, division_info, appearance_info, pharmacist_info, classification_info, ingredient_info;
    private String appearance_content, pharmacist_content, classification_content, ingredient_content, name_content, serial_content;
    private String shape_content, path_content, unit_content, daily_content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        applyColors();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);

        subtitute_btn = findViewById(R.id.subtitute_btn);
        subtitute_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SubstituteActivity.class);
                startActivity(intent);
            }
        });

        capture_btn = findViewById(R.id.capture_btn);
        capture_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScreenShot();
            }
        });

        medicine_background1 = findViewById(R.id.medicine_background1);
        scaleAnim = AnimationUtils.loadAnimation(this, R.anim.scale);
        medicine_background1.setAnimation(scaleAnim);

        medicine_background2 = findViewById(R.id.medicine_background2);
        scaleAnim2 = AnimationUtils.loadAnimation(this, R.anim.scale2);
        medicine_background2.setAnimation(scaleAnim2);

        name = findViewById(R.id.name);
        opacityAnim = AnimationUtils.loadAnimation(this, R.anim.opacity);
        name.setAnimation(opacityAnim);

        serial = findViewById(R.id.serial);
        division = findViewById(R.id.division);
        appearance = findViewById(R.id.appearance);
        pharmacist = findViewById(R.id.pharmacist);
        classification = findViewById(R.id.classification);
        ingredient = findViewById(R.id.ingredient);

        serial_info = findViewById(R.id.serial_info);
        division_info = findViewById(R.id.division_info);
        appearance_info = findViewById(R.id.appearance_info);
        pharmacist_info = findViewById(R.id.pharmacist_info);
        classification_info = findViewById(R.id.classification_info);
        ingredient_info = findViewById(R.id.ingredient_info);

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

                serial.setTextSize(textSize);
                division.setTextSize(textSize);
                appearance.setTextSize(textSize);
                pharmacist.setTextSize(textSize);
                classification.setTextSize(textSize);
                ingredient.setTextSize(textSize);

                serial_info.setTextSize(textSize);
                division_info.setTextSize(textSize);
                appearance_info.setTextSize(textSize);
                pharmacist_info.setTextSize(textSize);
                classification_info.setTextSize(textSize);
                ingredient_info.setTextSize(textSize);
                makeText(getApplicationContext(), "글자 크기를 "+textPhase+"단계로 변경합니다.",Toast.LENGTH_SHORT).show();
            }
        });

        rotateAnim = AnimationUtils.loadAnimation(this, R.anim.rotate2);
        size_btn.setAnimation(rotateAnim);

        analyze_result_box = findViewById(R.id.analyze_result_box);
        analyze_result_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!status){
                    // 현재 출력된 정보가 의약품 분석결과 중 일련번호, 구분, 성상, 제약사, 분류명, 주성분을 제시하는 경우
                    shape_content = "경질캡슐제, 과립제";
                    path_content = "경구";
                    unit_content = "캡슐";
                    daily_content = "1";

                    appearance.setText("제  형  명");
                    appearance_info.setText(shape_content);
                    pharmacist.setText("투여경로");
                    pharmacist_info.setText(path_content);
                    classification.setText("투여단위");
                    classification_info.setText(unit_content);
                    ingredient.setText("일투여량");
                    ingredient_info.setText(daily_content+"회");

                    status = true;
                }
                else{
                    // 현재 출력된 정보가 의약품 분석결과 중 일련번호, 구분, 성상, 투여경로, 투여단위, 1일 최대투여량을 제시하는 경우
                    appearance_content = "흰색의 원형 정제";
                    pharmacist_content = "(주)에스트라";
                    classification_content = "기타의 비뇨 생식기관 및 항문용 약";
                    ingredient_content = "탐스로신염산염";

                    appearance.setText("성        상");
                    appearance_info.setText(appearance_content);
                    pharmacist.setText("제  약  사");
                    pharmacist_info.setText(pharmacist_content);
                    classification.setText("분  류  명");
                    classification_info.setText(classification_content);
                    ingredient.setText("주  성  분");
                    ingredient_info.setText(ingredient_content);
                    status = false;
                }
            }
        });
        name.setText(pillModel.pill_name);
        serial_info.setText(pillModel.pill_serial);
        appearance_info.setText(pillModel.appearance);
        classification_info.setText(pillModel.classify);
        pharmacist_info.setText(pillModel.business_name);
        division_info.setText(pillModel.is_prescription);
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
            toast = makeText(this, "한번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG);
            toast.show();
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
