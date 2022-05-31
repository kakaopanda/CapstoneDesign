package com.example.capstonedesign;

import android.util.Log;

import java.util.concurrent.Callable;

public class SendMails implements Callable<String> {
    private String id;
    private String str[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    private String code = "";

    public SendMails(String id) {
        this.id = id;
    }

    // GMailSender를 활용하여 인증 메일 전송
    public String call() {
        try {
            GMailSender sender = new GMailSender("lukai7501@gmail.com", "nktfhnxrbqmqewzt");
            sender.sendMail("[Search Pill] 이메일 인증 코드입니다.",
                    "이메일 인증코드는 [" + code + "]입니다.\n어플리케이션 화면에 4자리를 입력해주세요.",
                    "SearchPill@noreply.com",
                    id);
        } catch (Exception e) {
            Log.e("mail", "Mail error " + e.getMessage() + "\nCode: " + code);
        }
        return code;
    }

    // 0 ~ 9의 정수로 이루어진 4자리 코드 랜덤 생성
    public void newCode() {
        for(int i = 0; i < 4; i++) {
            int temp = (int) (Math.random() * 9);
            code += str[temp];
        }
    }

    // 이메일 재전송을 위해 기존 코드 받아오기
    public void setCode(String code) {
        this.code = code;
    }
}
