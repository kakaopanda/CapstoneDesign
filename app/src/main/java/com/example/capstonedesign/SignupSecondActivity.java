package com.example.capstonedesign;

import static com.example.capstonedesign.LoginSecondActivity.serverUrl;
import static com.example.capstonedesign.SignupFirstActivity.signupCode;
import static com.example.capstonedesign.SignupFirstActivity.signupId;
import static com.example.capstonedesign.SignupFirstActivity.signupPw;

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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignupSecondActivity extends AppCompatActivity {
    // XML Object
    private ImageView submit_btn;
    private ExecutorService executorService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        applyColors();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_second);

        // 인증 코드 제출하기 버튼
        submit_btn = findViewById(R.id.submit_btn);
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText codeText = (EditText) findViewById(R.id.input_box_edit);
                String code = codeText.getText().toString();
                // 인증 코드가 일치하면 회원가입 진행
                if(code.equals(signupCode)) {
                    signup();
                }
                else {
                    Toast.makeText(getApplicationContext(), "인증코드가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                    Log.e("code", "Code: " + signupCode);
                }
            }
        });

        // 인증번호 재전송 버튼
        ImageView resend_btn = findViewById(R.id.resend_btn);
        resend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executorService = Executors.newFixedThreadPool(2);
                SendMails resendMails = new SendMails(signupId);
                resendMails.setCode(signupCode);
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Future<String> future = executorService.submit(resendMails);
                            future.get();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                Toast.makeText(getApplicationContext(), "인증코드 메일이 재전송 되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 회원가입
    public void signup() {
        Gson gson = new GsonBuilder().setLenient().create();
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
                    if (result.equals("SUCCESS")) {
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "사용할 수 없는 이메일입니다.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                    Log.e("Login","onResponse: 성공, 결과: " + result.toString());
                }
                else {
                    Toast.makeText(getApplicationContext(), "서버가 응답하지 않습니다.", Toast.LENGTH_LONG).show();
                    Log.e("Login", "onResponse: 실패");
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "서버가 응답하지 않습니다.", Toast.LENGTH_LONG).show();
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
