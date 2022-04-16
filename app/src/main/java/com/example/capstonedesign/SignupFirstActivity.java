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

public class SignupFirstActivity extends AppCompatActivity {
    ImageView submit_btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        applyColors();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_first);

        submit_btn = findViewById(R.id.submit_btn);
        submit_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                EditText pwText = (EditText)findViewById(R.id.sample_EditText8);
                EditText pw2Text = (EditText)findViewById(R.id.sample_EditText3);
                String pw = pwText.getText().toString();
                String pw2 = pw2Text.getText().toString();
                if(pw.equals(pw2)) {
                    signup();
                } else{
                    Toast failedToast = Toast.makeText(getApplicationContext(),"CHECK PASSWORD",Toast.LENGTH_LONG);
                    failedToast.show();
                }
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

    /*
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
     */

    public void signup() {
        EditText idText = (EditText)findViewById(R.id.sample_EditText9);
        EditText pwText = (EditText)findViewById(R.id.sample_EditText8);
        EditText pw2Text = (EditText)findViewById(R.id.sample_EditText3);
        String id = idText.getText().toString();
        String pw = pwText.getText().toString();
        String pw2 = pw2Text.getText().toString();
        Gson gson = new GsonBuilder().setLenient().create();
        Toast failedToast = Toast.makeText(getApplicationContext(), "SIGNUP FAILED", Toast.LENGTH_LONG);
        Intent intent = new Intent(getApplicationContext(), SignupSecondActivity.class);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.35.216:8080/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        SignupService service = retrofit.create(SignupService.class);

        Call<String> call = service.signup(id,pw);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()) {
                    String result = response.body();
                    if(result.equals("SUCCESS")) {
                        startActivity(intent);
                    }
                    else {
                        failedToast.show();
                    }
                    Log.d("Login","onResponse: 성공, 결과:\n"+result.toString());
                } else {
                    failedToast.show();
                    Log.d("Login", "onResponse: 실패");
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("TAG", "onFailure " + t.getMessage());
            }
        });
    }
}
