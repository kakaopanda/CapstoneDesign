package com.example.capstonedesign;

import static com.example.capstonedesign.LoginFirstActivity.serverUrl;
import static com.example.capstonedesign.SignupFirstActivity.signupId;
import static com.example.capstonedesign.SignupFirstActivity.signupPw;
import static com.example.capstonedesign.SignupFirstActivity.signupCode;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignupSecondActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        applyColors();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_second);

        // 제출하기 버튼
        ImageView submit_btn = findViewById(R.id.submit_btn);
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText codeText = (EditText) findViewById(R.id.sample_EditText);
                String code = codeText.getText().toString();
                if(code.equals(signupCode)) {
                    signup();
                }
                else {
                    Toast.makeText(getApplicationContext(), "회원가입 실패", Toast.LENGTH_SHORT).show();
                    Log.e("code", "code: " + code + "signupCode: " +signupCode);
                }
            }
        });

        // 인증번호 재전송 버튼
        ImageView resend_btn = findViewById(R.id.resend_btn);
        resend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mailResend(signupId, signupCode);
                Toast.makeText(getApplicationContext(), "메일 재전송 완료", Toast.LENGTH_SHORT).show();
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
        //super.onBackPressed();
    }

    // 회원가입
    public void signup() {
        Gson gson = new GsonBuilder().setLenient().create();
        Toast failedToast = Toast.makeText(getApplicationContext(), "SIGNUP FAILED", Toast.LENGTH_LONG);
        Intent intent = new Intent(getApplicationContext(), SignupThirdActivity.class);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        SignupService service = retrofit.create(SignupService.class);

        Call<String> call = service.signup(signupId,signupPw);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String result = response.body();
                    if (result.equals("SUCCESS")) {startActivity(intent);}
                    else {failedToast.show();}
                    Log.e("Login","onResponse: 성공, 결과: " + result.toString());
                }
                else {
                    failedToast.show();
                    Log.e("Login", "onResponse: 실패");
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("Login", "onFailure " + t.getMessage());
            }
        });
    }
    
    // 인증번호 재전송
    public void mailResend(String id, String code) {
        try {
            GMailSender sender = new GMailSender("lukai7501@gmail.com", "nktfhnxrbqmqewzt");
            sender.sendMail("[Search Pill] EMAIL CONFIRM CODE",
                    "The code is " + code,
                    "lukai7501@gmail.com",
                    id);
        } catch (Exception e) {
            Log.e("mail", "mail error " + e.getMessage() + " code: " + code);
        }
    }
}
