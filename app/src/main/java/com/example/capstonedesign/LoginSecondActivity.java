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
    // XML Object
    public static String serverUrl = "http://1.249.71.81:8080/";
    public static String loginId, loginPw;
    private ImageView signup_btn, login_btn, another_btn;

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
                startActivity(new Intent(getApplicationContext(), SignupFirstActivity.class));
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
                startActivity(new Intent(getApplicationContext(), LoginFirstActivity.class));
            }
        });
    }

    // 로그인 기능
    private void login() {
        EditText idText = (EditText)findViewById(R.id.id_input_edit);
        EditText pwText = (EditText)findViewById(R.id.password_input_edit);
        String id = idText.getText().toString();
        String pw = pwText.getText().toString();
        Gson gson = new GsonBuilder().setLenient().create();
        Toast failedToast = Toast.makeText(getApplicationContext(), "이메일이나 비밀번호가 잘못되었습니다.", Toast.LENGTH_LONG);
        Toast serverErrorToast = Toast.makeText(getApplicationContext(), "서버가 응답하지 않습니다.", Toast.LENGTH_LONG);

        // 서버 통신을 위한 Retrofit 설정
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        // Retrofit에 Login 정보를 담아서 서비스 생성
        LoginService service = retrofit.create(LoginService.class);

        // 서버 통신
        Call<String> call = service.login(id,pw);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()) {
                    String result = response.body();
                    if(result.equals("SUCCESS")) {
                        loginId = id;
                        loginPw = pw;
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        Log.e("Login","onResponse: 성공, 결과: " + result);
                    }
                    else {
                        failedToast.show();
                    }
                }
                else {
                    serverErrorToast.show();
                    Log.e("Login", "onResponse: 실패");
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                serverErrorToast.show();
                Log.e("Login", "onFailure " + t.getMessage());
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
