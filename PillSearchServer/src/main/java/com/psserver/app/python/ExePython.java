package com.psserver.app.python;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder;
import java.util.Vector;

import java.lang.Process;

public class ExePython extends Thread{
	Vector<String> result = new Vector<String>();
	String id;
	String filename;
	
	public ExePython(String id, String filename) {
		this.id = id;
		this.filename = filename;
	}
	
	public void run() {
		readPython();
	}
	
	// 알약을 인식하는 모델인 Pill_Analysis.py를 ProcessBuilder로 실행시킨 후 인자값으로 분석할 이미지 경로를 주고
	// 분석 결과값을 표준출력으로 읽어들여서 문자열 벡터에 담은 뒤 리턴한다.
	public void readPython() {
		String arg1 = "python";
		String arg2 = "C:\\Users\\lukai\\desktop\\Pill_Analysis.py";
		String arg3 = "C:\\Users\\lukai\\Desktop\\PillSearchServer\\images\\" + id;
		String arg4 = "\\" + filename;
		String line;
		
		ProcessBuilder builder = new ProcessBuilder(arg1, arg2, arg3, arg4);
		try {
			builder.redirectErrorStream(true);
			Process process = builder.start();
			int exitVal = process.waitFor();
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			System.out.println(">>> 분석모델로부터 받은 출력내용:");
			while((line = br.readLine()) != null) {
				System.out.println(line);
				result.add(line);
			}
			if(exitVal != 0) {
				System.out.println("프로세스 비정상종료");
			}
		}
		catch(Exception e) {
			System.out.print(e.toString());
		}
	}
	
	public Vector<String> getResult() {
		return result;
	}
}