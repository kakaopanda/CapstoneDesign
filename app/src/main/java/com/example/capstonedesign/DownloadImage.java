package com.example.capstonedesign;

import static com.example.capstonedesign.LoginSecondActivity.serverUrl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;

// 분석 결과에 맞는 알약의 이미지를 비트맵 형태로 다운로드
public class DownloadImage implements Callable<Bitmap> {
    String filename;
    Bitmap bitmap;

    public DownloadImage(String filename) {
        this.filename = filename;
    }

    public Bitmap call() {
        URL url = null;
        try {
            url = new URL(serverUrl + "image/" + filename);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
