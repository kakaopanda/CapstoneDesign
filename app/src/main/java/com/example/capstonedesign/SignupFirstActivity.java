package com.example.capstonedesign;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SignupFirstActivity extends AppCompatActivity {
    // XML Object
    public static String signupId, signupPw, signupCode;
    private ImageView submit_btn;
    private ExecutorService executorService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        applyColors();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_first);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder() .permitDiskReads() .permitDiskWrites() .permitNetwork().build());

        // 제출 버튼
        submit_btn = findViewById(R.id.submit_btn);
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText idText = (EditText)findViewById(R.id.email_edit);
                EditText pwText = (EditText)findViewById(R.id.password_edit);
                EditText pw2Text = (EditText)findViewById(R.id.password_confirm_edit);
                String id = idText.getText().toString();
                String pw = pwText.getText().toString();
                String pw2 = pw2Text.getText().toString();

                // 인증 메일 전송
                executorService = Executors.newFixedThreadPool(2);
                SendMails sendMails = new SendMails(id);
                sendMails.newCode();
                Future<String> future = executorService.submit(sendMails);
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            signupCode = future.get();
                            if(pw.equals(pw2)) {
                                signupId = id;
                                signupPw = pw;
                                startActivity(new Intent(getApplicationContext(), SignupSecondActivity.class));
                            } else{
                                Toast.makeText(getApplicationContext(),"비밀번호가 비밀번호 확인란과 일치하지 않습니다.",Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
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

}
