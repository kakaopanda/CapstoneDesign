package com.example.capstonedesign;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
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
    ImageView subtitute_btn, capture_btn, update, medicine_circle, medicine_circle2, medicine_circle3;
    Animation opacityAnim, scaleAnim, scaleAnim2, scaleAnim3;
    TextView result_name2;

    private long backKeyPressedTime = 0;
    private Toast toast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        applyColors();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze_temp);

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

        medicine_circle = findViewById(R.id.medicine_circle);
        scaleAnim = AnimationUtils.loadAnimation(this, R.anim.scale);
        medicine_circle.setAnimation(scaleAnim);

        medicine_circle2 = findViewById(R.id.medicine_circle2);
        scaleAnim2 = AnimationUtils.loadAnimation(this, R.anim.scale2);
        medicine_circle2.setAnimation(scaleAnim2);

        result_name2 = findViewById(R.id.result_name2);
        opacityAnim = AnimationUtils.loadAnimation(this, R.anim.opacity);
        result_name2.setAnimation(opacityAnim);
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
            toast = Toast.makeText(this, "한번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG);
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
