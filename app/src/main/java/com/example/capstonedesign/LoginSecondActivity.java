package com.example.capstonedesign;

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

public class LoginSecondActivity extends AppCompatActivity {
    ImageView signup_btn, login_btn, another_btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        applyColors();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_second);

        // 회원가입 버튼
        signup_btn = findViewById(R.id.signup_btn);
        signup_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignupFirstActivity.class);
                startActivity(intent);
            }
        });

        // 로그인 버튼
        login_btn = findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                login();
            }
        });

        // 다른 방법으로 로그인 버튼
        another_btn = findViewById(R.id.another_btn);
        another_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginFirstActivity.class);
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

    // 로그인 기능
    private void login() {
        EditText idText = (EditText)findViewById(R.id.sample_EditText);
        EditText pwText = (EditText)findViewById(R.id.sample_EditText2);
        String id = idText.getText().toString();
        String pw = pwText.getText().toString();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        Gson gson = new GsonBuilder().setLenient().create();
        Toast failedToast = Toast.makeText(getApplicationContext(), "LOGIN FAILED", Toast.LENGTH_LONG);
        Toast serverErrorToast = Toast.makeText(getApplicationContext(), "SERVER ERROR", Toast.LENGTH_LONG);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.35.219:8080/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        LoginService service = retrofit.create(LoginService.class);

        Call<String> call = service.login(id,pw);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()) {
                    String result = response.body();
                    if(result.equals("SUCCESS")) {
                        startActivity(intent);
                        Log.d("Login","onResponse: 성공, 결과:\n"+result.toString());
                    }
                    else {
                        failedToast.show();
                    }
                } else {
                    failedToast.show();
                    Log.d("Login", "onResponse: 실패");
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                serverErrorToast.show();
                Log.d("TAG", "onFailure " + t.getMessage());
            }
        });
    }
}
