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
	
	// �˾��� �ν��ϴ� ���� Pill_Analysis.py�� ProcessBuilder�� �����Ų �� ���ڰ����� �м��� �̹��� ��θ� �ְ�
	// �м� ������� ǥ��������� �о�鿩�� ���ڿ� ���Ϳ� ���� �� �����Ѵ�.
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
			System.out.println(">>> �м��𵨷κ��� ���� ��³���:");
			while((line = br.readLine()) != null) {
				System.out.println(line);
				result.add(line);
			}
			if(exitVal != 0) {
				System.out.println("���μ��� ����������");
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