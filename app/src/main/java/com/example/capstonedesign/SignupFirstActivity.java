package com.example.capstonedesign;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class SignupFirstActivity extends AppCompatActivity {
    // XML Object
    private ImageView submit_btn;
    public static String signupId = new String();
    public static String signupPw = new String();
    public static String signupCode = new String();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        applyColors();
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_first);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder() .permitDiskReads() .permitDiskWrites() .permitNetwork().build());

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
                if(pw.equals(pw2)) {
                    signupId = id;
                    signupPw = pw;
                    sendMails(id);
                    Intent intent = new Intent(getApplicationContext(), SignupSecondActivity.class);
                    startActivity(intent);
                } else{
                    Toast.makeText(getApplicationContext(),"비밀번호가 다릅니다.",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void sendMails(String id) {
        String str[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        String code = new String();

        for(int i = 0; i < 4; i++) {
            int temp = (int)(Math.random()*9);
            code += str[temp];
        }
        signupCode = code;
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
