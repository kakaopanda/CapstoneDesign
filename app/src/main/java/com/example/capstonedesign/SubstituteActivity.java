package com.example.capstonedesign;

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

import de.hdodenhof.circleimageview.CircleImageView;

public class SubstituteActivity extends AppCompatActivity {
    private static final String CAPTURE_PATH = "/storage/emulated/0/DCIM/Screenshots";
    ImageView reanalyze_btn3, capture_btn, update, medicine_circle, medicine_circle1, medicine_circle2, medicine_circle3, medicine_circle4;
    CircleImageView circle_iv4;
    TextView result_name4, result_name5;
    Animation opacityAnim, scaleAnim, scaleAnim2, scaleAnim3;

    // 마지막으로 뒤로 가기 버튼을 눌렀던 시간 저장
    private long backKeyPressedTime = 0;
    // 첫 번째 뒤로 가기 버튼을 누를 때 표시
    private Toast toast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        applyColors();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_substitution_temp);

        reanalyze_btn3 = findViewById(R.id.reanalyze_btn3);
        reanalyze_btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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

        circle_iv4 = findViewById(R.id.circle_iv4);
        circle_iv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        medicine_circle1 = findViewById(R.id.medicine_circle1);
        scaleAnim = AnimationUtils.loadAnimation(this, R.anim.scale);
        medicine_circle1.setAnimation(scaleAnim);

        medicine_circle2 = findViewById(R.id.medicine_circle2);
        scaleAnim2 = AnimationUtils.loadAnimation(this, R.anim.scale2);
        medicine_circle2.setAnimation(scaleAnim2);

        medicine_circle3 = findViewById(R.id.medicine_circle3);
        scaleAnim = AnimationUtils.loadAnimation(this, R.anim.scale);
        medicine_circle3.setAnimation(scaleAnim);

        medicine_circle4 = findViewById(R.id.medicine_circle4);
        scaleAnim2 = AnimationUtils.loadAnimation(this, R.anim.scale2);
        medicine_circle4.setAnimation(scaleAnim2);

        result_name4 = findViewById(R.id.result_name4);
        opacityAnim = AnimationUtils.loadAnimation(this, R.anim.opacity);
        result_name4.setAnimation(opacityAnim);

        result_name5 = findViewById(R.id.result_name5);
        opacityAnim = AnimationUtils.loadAnimation(this, R.anim.opacity);
        result_name5.setAnimation(opacityAnim);
    }

    public void ScreenShot(){
        View view = getWindow().getDecorView().getRootView();
        view.setDrawingCacheEnabled(true);  //화면에 뿌릴때 캐시를 사용하게 한다
        //캐시를 비트맵으로 변환
        Bitmap screenBitmap = Bitmap.createBitmap(view.getDrawingCache());
        try {
            File cachePath = new File(getApplicationContext().getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            screenBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            File newFile = new File(cachePath, "image.png");
            Uri contentUri = FileProvider.getUriForFile(getApplicationContext(),
                    "com.example.capstonedesign.fileprovider", newFile);

            Intent Sharing_intent = new Intent(Intent.ACTION_SEND);
            Sharing_intent.setType("image/png");
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
