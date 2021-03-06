package com.example.capstonedesign;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

public class LoginFirstActivity extends AppCompatActivity{
    // JAVA Object
    private Animation scaleAnim, rotateAnim;
    private Toast toast;
    private long backKeyPressedTime = 0;

    // XML Object
    private ImageView email_login, medicine_image, glass_image;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyColors();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_first);

        email_login = findViewById(R.id.email_login);
        medicine_image = findViewById(R.id.medicine_image);

        rotateAnim = AnimationUtils.loadAnimation(this, R.anim.rotate3);
        medicine_image.setAnimation(rotateAnim);

        glass_image = findViewById(R.id.glass_image);
        scaleAnim = AnimationUtils.loadAnimation(this, R.anim.scale4);
        glass_image.setAnimation(scaleAnim);

        // 이메일로 로그인 버튼
        email_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginSecondActivity.class);
                startActivity(intent);
            }
        });
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