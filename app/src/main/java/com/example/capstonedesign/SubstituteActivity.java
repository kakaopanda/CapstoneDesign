package com.example.capstonedesign;

import static android.widget.Toast.makeText;

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
    // JAVA Object
    private Animation opacityAnim, scaleAnim, scaleAnim2, rotateAnim;
    private Toast toast;
    private int textPhase = 1;
    private long backKeyPressedTime = 0;
    private boolean status = false;

    // XML Object
    private CircleImageView analyze_medicine_front;
    private ImageView reanalyze_btn, capture_btn, small_medicine_background1, small_medicine_background2, big_medicine_background1, big_medicine_background2, size_btn, substitution_result_box;
    private TextView serial, division, appearance, pharmacist, classification, ingredient, analyze_result_name, substitution_result_name;
    private TextView serial_info, division_info, appearance_info, pharmacist_info, classification_info, ingredient_info;
    private String appearance_subcontent, pharmacist_subcontent, classification_subcontent, ingredient_subcontent;
    private String shape_subcontent, path_subcontent, unit_subcontent, daily_subcontent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        applyColors();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_substitution);

        reanalyze_btn = findViewById(R.id.reanalyze_btn);
        reanalyze_btn.setOnClickListener(new View.OnClickListener() {
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

        analyze_medicine_front = findViewById(R.id.analyze_medicine_front);
        analyze_medicine_front.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        small_medicine_background1 = findViewById(R.id.small_medicine_background1);
        scaleAnim = AnimationUtils.loadAnimation(this, R.anim.scale);
        small_medicine_background1.setAnimation(scaleAnim);

        small_medicine_background2 = findViewById(R.id.small_medicine_background2);
        scaleAnim2 = AnimationUtils.loadAnimation(this, R.anim.scale2);
        small_medicine_background2.setAnimation(scaleAnim2);

        big_medicine_background1 = findViewById(R.id.big_medicine_background1);
        scaleAnim = AnimationUtils.loadAnimation(this, R.anim.scale);
        big_medicine_background1.setAnimation(scaleAnim);

        big_medicine_background2 = findViewById(R.id.big_medicine_background2);
        scaleAnim2 = AnimationUtils.loadAnimation(this, R.anim.scale2);
        big_medicine_background2.setAnimation(scaleAnim2);

        analyze_result_name = findViewById(R.id.analyze_result_name);
        opacityAnim = AnimationUtils.loadAnimation(this, R.anim.opacity);
        analyze_result_name.setAnimation(opacityAnim);

        substitution_result_name = findViewById(R.id.substitution_result_name);
        opacityAnim = AnimationUtils.loadAnimation(this, R.anim.opacity);
        substitution_result_name.setAnimation(opacityAnim);

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

        substitution_result_box = findViewById(R.id.substitution_result_box);
        substitution_result_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!status){
                    // 현재 출력된 정보가 의약품 분석결과 중 일련번호, 구분, 성상, 제약사, 분류명, 주성분을 제시하는 경우
                    shape_subcontent = "정보 없음";
                    path_subcontent = "정보 없음";
                    unit_subcontent = "정보 없음";
                    daily_subcontent = "정보 없음";

                    appearance.setText("제  형  명");
                    appearance_info.setText(shape_subcontent);
                    pharmacist.setText("투여경로");
                    pharmacist_info.setText(path_subcontent);
                    classification.setText("투여단위");
                    classification_info.setText(unit_subcontent);
                    ingredient.setText("일투여량");
                    ingredient_info.setText(daily_subcontent);
                    status = true;
                }
                else{
                    // 현재 출력된 정보가 의약품 분석결과 중 일련번호, 구분, 성상, 투여경로, 투여단위, 1일 최대투여량을 제시하는 경우
                    appearance_subcontent = "회색의 원형 필름코팅정";
                    pharmacist_subcontent = "한국메나리니(주)";
                    classification_subcontent = "기타의 비뇨 생식기관 및 항문용 약";
                    ingredient_subcontent = "디폭세틴염산염";

                    appearance.setText("성        상");
                    appearance_info.setText(appearance_subcontent);
                    pharmacist.setText("제  약  사");
                    pharmacist_info.setText(pharmacist_subcontent);
                    classification.setText("분  류  명");
                    classification_info.setText(classification_subcontent);
                    ingredient.setText("주  성  분");
                    ingredient_info.setText(ingredient_subcontent);
                    status = false;
                }
            }
        });
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
