package com.example.capstonedesign;

import static com.example.capstonedesign.LoginFirstActivity.serverUrl;
import static com.example.capstonedesign.LoginSecondActivity.loginId;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    // JAVA Object
    private final int REQUEST_TAKE_PHOTO = 1;
    private final String TAG = this.getClass().getSimpleName();

    public Uri AnalyzeImage = null;
    private Toast toast;
    private String mCurrentPhotoPath;
    private long backKeyPressedTime = 0;
    public static String filePath;

    // XML Object
    private ImageView analyze_target, upload_btn, camera_btn;
    private TextView quantity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        applyColors();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        upload_btn = findViewById(R.id.upload_btn);
        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoadingActivity.class);
                startActivity(intent);
            }
        });

        analyze_target = findViewById(R.id.analyze_target);
        analyze_target.setOnClickListener(v -> {
            // 촬영시 주의사항을 담은 Toast Message 출력
            Toast.makeText(getApplicationContext(),"분석할 이미지를 업로드 해주세요.",Toast.LENGTH_LONG).show();

            // 갤러리에서 분석할 이미지 파일을 가져오는 Intent 객체 생성
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            gallery_launcher.launch(intent);
        });

        camera_btn = findViewById(R.id.camera_btn);
        camera_btn.setOnClickListener(v -> {
            dispatchTakePictureIntent();
        });

        quantity = findViewById(R.id.quantity);
        quantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AnalyzeFailActivity.class);
                startActivity(intent);
            }
        });
    }

    private void dispatchTakePictureIntent() {
        // 사진 촬영 인텐트(MediaStore.ACTION_IMAGE_CAPTURE)를 지정한 새로운 Intent를 생성한다.
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 사진 촬영을 수행할 수 있는 Activity를 탐색하고, 존재하지 않으면 null을 반환한다.
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                // 촬영한 사진을 이미지 파일로 저장한다.
                photoFile = createImageFile();
            } catch (IOException ex) {}
            if (photoFile != null) {
                // 촬영한 사진에 대해 이미지 파일이 정상적으로 생성된 경우,
                // FileProvider.getUriForFile()에 File 객체를 넘긴 뒤, PhotoURI를 반환받는다.
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.capstonedesign.fileprovider",
                        photoFile);
                // Extra Data는 인텐트 간 공유할 실제 데이터를 저장하며, Key/Value 쌍의 구조이다.
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                // Camera Activity로 전환되면서 주의사항이 담긴 Toast Message 출력
                Toast.makeText(getApplicationContext(),"이미지는 밝고 평평한 곳에서 촬영해주세요.",Toast.LENGTH_LONG).show();
                camera_launcher.launch(takePictureIntent);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        try {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO: {
                    if (resultCode == RESULT_OK) {
                        File file = new File(mCurrentPhotoPath);
                        Bitmap bitmap = MediaStore.Images.Media
                                .getBitmap(getContentResolver(), Uri.fromFile(file));
                        if (bitmap != null) {
                            analyze_target.setImageBitmap(bitmap);
                        }
                    }
                    break;
                }
            }

        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    // startActivity()는 Parent Activity에서 Child Activity의 결과를 처리하지 않아도 될때 사용하며, 처리가 필요한 경우 startActivityForResult()를 사용한다.
    // startActivityForResult()의 경우, RequestCode를 통해 각 Activity들을 구분했었는데, 가독성이 떨어지고 메모리 부족으로 인해 Parent Activity가 소멸될 수 있는 문제점이 존재했다.
    // 이러한 문제점의 극복을 위해, deprecated 처리되었으며 대체방안으로 ActivityResultLauncher가 추가되었다.
    // 처리 결과를 수신할 Activity에서 ActivityResultLauncher를 정의하고, launch()를 통해 Child Activity를 활성화할 수 있다.
    // Contract를 정의한 뒤, registerForActivityResult() 메소드 호출을 통해 Callback을 등록할 수 있고, 결과를 받기 위해 StartActivityForResult()를 인자로 호출한다.
    ActivityResultLauncher<Intent> camera_launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            // ActivityResultCallback은 ActivityResultContract에 정의된 출력 유형(ActivityResult)의 객체를 가져오는 onActivityResult() 메서드가 포함된 단일 메서드 인터페이스이다.
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result)
                {
                    if (result.getResultCode() == RESULT_OK)
                    {
                        // mCurrentPhotoPath는 현재 촬영을 통해 생성된 이미지파일이 위치한 절대경로를 나타내는 String 전역 변수이다.
                        File file = new File(mCurrentPhotoPath);
                        Bitmap bitmap = null;
                        // API LEVEL >= 28 (Android 9, Pie)인 경우, ImageDecoder를 이용하여 Decoding을 수행한다.
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                            try {
                                // 촬영된 이미지의 URI를 인수로 제공하여 Bitmap 형식으로 변환한다.
                                bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(),Uri.fromFile(file)));
                                // SimpleDateFormat을 통해, 촬영된 이미지의 이름 형식을 지정한다.
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초");
                                // MediaStore.Images.Media.insertImage -> Deprecated Method(API 29)
                                // MediaStore의 insertImage()를 이용하여, 시스템 갤러리에 촬영된 이미지를 저장한다.
                                MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, simpleDateFormat.format(new Date()), "");
                                // 촬영 후, 저장된 이미지가 갤러리에 저장되었음을 반영하기 위해, 미디어 스캐닝을 실시한다.
                                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File("/sdcard/DCIM/image.jpg"))));
                                /*
                                    // [Bitmap Image ReSizing] 모델로 분석이미지를 전송할 때, 이미지의 크기를 조정할 필요가 있는 경우 고려한다.
                                            Bitmap resized = Bitmap.createScaledBitmap(bitmap, 250, 250, true);
                                            SimpleDateFormat resizeDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초"+"(Resize)");
                                            MediaStore.Images.Media.insertImage(getContentResolver(), resized, resizeDateFormat.format(new Date()), "");
                                 */
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        // API LEVEL < 28인 경우, MediaStore를 이용하여 Decoding을 수행한다.
                        // 이외의 나머지 작업은 위의 작업과 동일하다.
                        else{
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.fromFile(file));
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초");
                                MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, simpleDateFormat.format(new Date()), "");
                                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File("/sdcard/DCIM/image.jpg"))));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (bitmap != null) {
                            analyze_target.setImageBitmap(bitmap);
                        }
                    }
                    filePath = mCurrentPhotoPath;
                    UploadImage uploadImage = new UploadImage(filePath);
                    uploadImage.start();
                    SaveImage saveImage = new SaveImage(loginId, filePath);
                    saveImage.start();
                }
            });

    ActivityResultLauncher<Intent> gallery_launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
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
                        analyze_target.setImageURI(uri);
                        Log.e(TAG, "PATH: " + getFullPathFromContentUri(getApplicationContext(), uri));
                        String path = getFullPathFromContentUri(getApplicationContext(), uri);
                        filePath = path;
                        UploadImage uploadImage = new UploadImage(filePath);
                        uploadImage.start();
                        SaveImage saveImage = new SaveImage(loginId, filePath);
                        saveImage.start();
                    }
                }
            });

    // URI에서 파일 절대경로 추출
    public static String getFullPathFromContentUri(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        String filePath = "";

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }//non-primary e.g sd card
                else {

                    if (Build.VERSION.SDK_INT > 20) {
                        //getExternalMediaDirs() added in API 21
                        File extenal[] = context.getExternalMediaDirs();
                        for (File f : extenal) {
                            filePath = f.getAbsolutePath();
                            if (filePath.contains(type)) {
                                int endIndex = filePath.indexOf("Android");
                                filePath = filePath.substring(0, endIndex) + split[1];
                            }
                        }
                    }else{
                        filePath = "/storage/" + type + "/" + split[1];
                    }
                    return filePath;
                }
            }
            // DownloadsProvider
            else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                Cursor cursor = null;
                final String column = "_data";
                final String[] projection = {
                        column
                };

                try {
                    cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                            null);
                    if (cursor != null && cursor.moveToFirst()) {
                        final int column_index = cursor.getColumnIndexOrThrow(column);
                        return cursor.getString(column_index);
                    }
                } finally {
                    if (cursor != null)
                        cursor.close();
                }
                return null;
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
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
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간에 2.5초를 더해 현재 시간과 비교 후
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간이 2.5초가 지났으면 Toast 출력
        // 2500 milliseconds = 2.5 seconds
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "한번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG);
            // ViewGroup group = (ViewGroup)toast.getView();
            // TextView msgTextView = (TextView)group.getChildAt(0);
            // msgTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
            toast.show();
            return;
        }
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간에 2.5초를 더해 현재 시간과 비교 후
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간이 2.5초가 지나지 않았으면 종료
        if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
            NotificationManagerCompat.from(this).cancel(1);
            finishAffinity(); // 해당 애플리케이션의 루트 액티비티 종료
            moveTaskToBack(true); // 태스크를 백그라운드로 이동
            finishAndRemoveTask(); // 액티비티 종료 + 태스크 리스트에서 지우기
            android.os.Process.killProcess(android.os.Process.myPid()); // 앱 프로세스 종료
        }
    }
}
