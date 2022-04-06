package com.example.capstonedesign;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    ImageView upload, medicine, camera;
    Uri AnalyzeImage = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        applyColors();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        upload = findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),AnalyzeImage.toString(),Toast.LENGTH_LONG).show();
                /*
                Intent intent = new Intent(getApplicationContext(), AnalyzeActivity.class);
                startActivity(intent);
                 */
            }
        });

        medicine = findViewById(R.id.medicine);
        medicine.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(),"분석할 이미지를 업로드 해주세요.",Toast.LENGTH_LONG).show();
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            launcher.launch(intent);
        });

        camera = findViewById(R.id.camera);
        camera.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(),"이미지는 밝고 평평한 곳에서 촬영해주세요.",Toast.LENGTH_LONG).show();
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            camera_launcher.launch(cameraIntent);
        });
    }

    ActivityResultLauncher<Intent> camera_launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result)
                {
                    if (result.getResultCode() == RESULT_OK)
                    {
                        Intent intent = result.getData();
                        Bundle extras = intent.getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초");
                        MediaStore.Images.Media.insertImage(getContentResolver(), imageBitmap, simpleDateFormat.format(new Date()), "");
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File("/sdcard/DCIM/image.jpg"))));
                        medicine.setImageBitmap(imageBitmap);
                    }
                }
            });


    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result)
                {
                    if (result.getResultCode() == RESULT_OK)
                    {
                        Log.e(TAG, "result : " + result);
                        Intent intent = result.getData();
                        Log.e(TAG, "intent : " + intent);
                        Uri uri = intent.getData();
                        AnalyzeImage = uri;
                        Log.e(TAG, "uri : " + uri);
                        medicine.setImageURI(uri);
                    }
                }
            });

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
        //super.onBackPressed();
    }
}
