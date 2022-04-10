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

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AnalyzeActivity extends AppCompatActivity {
    ImageView subtitute_btn, capture_btn, update, medicine_circle, medicine_circle2, medicine_circle3;
    Animation opacityAnim, scaleAnim, scaleAnim2, scaleAnim3;
    TextView result_name2;

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
}
