package com.example.capstonedesign;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.TimeAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class IntroActivity extends AppCompatActivity {
    Animation splashAnim,splashAnim2, splashAnim3;
    View gradientPreloaderView;
    TextView splash_title, splash_title2, splash_title3, splash_title4;
    boolean alarm_status = false;

    public static void startAnimation(final int view, final Activity activity) {
        final int start = Color.parseColor("#0099FF");
        final int mid = Color.parseColor("#FF1073B4");
        final int end = Color.parseColor("#000080");
        final ArgbEvaluator evaluator = new ArgbEvaluator();
        View preloader = activity.findViewById(R.id.gradientPreloaderView);
        preloader.setVisibility(View.VISIBLE);
        final GradientDrawable gradient = (GradientDrawable) preloader.getBackground();
        ValueAnimator animator = TimeAnimator.ofFloat(0.0f, 1.0f);
        animator.setDuration(1500);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float fraction = valueAnimator.getAnimatedFraction();
                int newStrat = (int) evaluator.evaluate(fraction, start, end);
                int newMid = (int) evaluator.evaluate(fraction, mid, start);
                int newEnd = (int) evaluator.evaluate(fraction, end, mid);
                int[] newArray = {newStrat, newMid, newEnd}; gradient.setColors(newArray);
            } }); animator.start();
    }

    public static void stopAnimation(final int view, final Activity activity){
        ObjectAnimator.ofFloat(activity.findViewById(view), "alpha", 0f).setDuration(125).start();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro); //xml , java 소스 연결
        startAnimation(0,this);
        splashAnim = AnimationUtils.loadAnimation(this, R.anim.splash);
        splashAnim2 = AnimationUtils.loadAnimation(this, R.anim.splash2);
        splashAnim3 = AnimationUtils.loadAnimation(this, R.anim.splash3);

        splash_title = findViewById(R.id.splash_title);
        splash_title2 = findViewById(R.id.splash_title2);
        splash_title3 = findViewById(R.id.splash_title3);
        splash_title4 = findViewById(R.id.splash_title4);

        splash_title.setAnimation(splashAnim3);
        splash_title2.setAnimation(splashAnim3);
        splash_title3.setAnimation(splashAnim3);
        splash_title4.setAnimation(splashAnim2);

        gradientPreloaderView = findViewById(R.id.gradientPreloaderView);
        gradientPreloaderView.setAnimation(splashAnim);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), LoginFirstActivity.class);
                startService(new Intent(getApplicationContext(), TerminationService.class));
                createNotification();
                startActivity(intent); //인트로 실행 후 바로 MainActivity로 넘어감.
                finish();
            }
        }, 3000); //1초 후 인트로 실행 기본 설정
    }

    private void createNotification() {
        if (!alarm_status) {
            alarm_status = true;
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default").setOngoing(true);

            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle("서치필(Search Pill)");
            builder.setContentText("현재 애플리케이션이 동작중입니다.");
            builder.setColor(ContextCompat.getColor(this, R.color.french_blue));
            builder.setAutoCancel(true);
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
            }
            notificationManager.notify(1, builder.build());
        }
    }

    private void removeNotification() {
        if(alarm_status) {
            alarm_status = false;
            NotificationManagerCompat.from(this).cancel(1);
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
}