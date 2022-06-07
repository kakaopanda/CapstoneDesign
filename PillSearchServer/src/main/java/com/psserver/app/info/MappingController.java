package com.psserver.app.info;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Vector;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.psserver.app.dao.Dao;
import com.psserver.app.dao.LoginDao;
import com.psserver.app.file.StorageService;
import com.psserver.app.model.ComponentModel;
import com.psserver.app.model.LoginModel;
import com.psserver.app.model.PillModel;
import com.psserver.app.python.ExePython;

@RestController
public class MappingController {
	private StorageService storageService;
	private static Dao<LoginModel> loginDao;
	public static String fileId = new String();
	
	public MappingController(StorageService storageService, LoginDao logindao) {
		this.storageService = storageService;
		MappingController.loginDao = logindao;
	}
	
	// 로그인 요청 처리
	@GetMapping("login/{id}/{pw}")
	public String login(@PathVariable String id, @PathVariable String pw) {
		String getPw = loginDao.get(id);
		if(getPw.equals(pw)) {
			System.out.println(">>> 로그인 정보 일치!");
			return "SUCCESS";
		} else {
			System.out.println(">>> 로그인 정보 불일치!");
			return "FAIL";
		}
	}
	
	// 회원가입 요청 처리
	@GetMapping("signup/{id}/{pw}")
	public String signup(@PathVariable String id, @PathVariable String pw) {
		LoginModel model = new LoginModel(id, pw);
		if(loginDao.create(model)) {
			System.out.println(">>> 회원가입 성공!");
			return "SUCCESS";
		} else {
			System.out.println(">>> 회원가입 실패!");
			return "FAIL";
		}
	}
	
	// 어플로부터 받은 알약 이미지를 서버에 저장
	@PostMapping("upload")
	public ResponseEntity<String> upload(MultipartFile file) throws IllegalStateException, IOException{
		System.out.println(">>> 어플로부터 이미지를 전송받았습니다.");
		storageService.store(file);
		System.out.println(">>> 이미지 저장 성공!");
		return new ResponseEntity<>("", HttpStatus.OK);
	}
	
	// 어플로부터 받은 알약 이미지를 데이터베이스에 등록
	@GetMapping("upload/{id}/{filename}")
	public String upload(@PathVariable String id, @PathVariable String filename) {
		System.out.println(">>> 전송받은 이미지의 정보롤 DB에 등록합니다.");
		MappingController.fileId = id;
		loginDao.upload(id, filename);
		System.out.println(">>> 이미지 정보 DB 등록 성공!");
		return "SUCCESS";
	}
	
	// 어플에로부터 받은 알약 이미지를 분석모델로 보내서 분석
	@GetMapping("analyze/{id}/{filename}")
	@ResponseBody
	public PillModel analyze(@PathVariable String id, @PathVariable String filename) {
		System.out.println(">>> 전송받은 이미지를 분석하기 위해 분석모델을 실행합니다.");
		ExePython exePython = new ExePython(id, filename);
		exePython.start();
		try {
			exePython.join();
		} catch (Exception e) {
			System.out.println(">>> 분석모델에서 오류가 발생하였습니다. " + e.toString());
		}
		System.out.println(">>> 분석 완료!");
		System.out.println(">>> 분석 결과를 기반으로 검색을 시작합니다.");
		PillModel pillModel = loginDao.pillInfo(exePython.getResult());
		System.out.println(">>> 검색이 완료되었습니다.");
		System.out.println(">>> 알약 모델: " + pillModel);
		return pillModel;
	}
	
	// 분석된 결과에 알맞는 알약 이미지 어플로 전송
	@GetMapping("image/{filename}")
	public ResponseEntity<Resource> findImage (@PathVariable("filename") String serial) {
		Path path = Paths.get("C:\\users\\lukai\\desktop\\pillImage\\");
		HttpHeaders header = new HttpHeaders();
		Resource resource = new FileSystemResource(path + "\\" + serial + ".jpg");
		System.out.println(">>> 알약의 이미지가 서버에 있습니까?: " + resource.exists());
		System.out.println(">>> 분석 결과에 맞는 알약의 이미지를 어플로 전송합니다.");
		if(!resource.exists())
			return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND);	
		try {
			header.add("Content-type", Files.probeContentType(path));
			System.out.println(">>> 이미지 전송 성공!");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
	}
	
	// 앞선 분석에 실패하여 사용자가 직접 입력한 알약 데이터를 기반으로 검색
	@GetMapping("text/{color}/{shape}/{text}")
	@ResponseBody
	public PillModel text(@PathVariable String color, @PathVariable String shape, @PathVariable String text) {
		System.out.println(">>> 사용자가 직접 입력한 알약 데이터를 기반으로 검색을 시작합니다.");
		Vector<String> v = new Vector<String>();
		v.add(color); v.add(shape); v.add(text);
		PillModel pillModel = loginDao.pillInfo(v);
		System.out.println(">>> 검색이 완료되었습니다.");
		System.out.println(">>> 알약 모델: " + pillModel);
		return pillModel;
	}
	
	// 주성분 정보 어플로 전송
	@GetMapping("component/{name}")
	public ComponentModel component(@PathVariable String name) {
		System.out.println(">>> 검색된 알약을 기반으로 주성분을 검색합니다.");
		ComponentModel componentModel = loginDao.getComponent(name);
		System.out.println(">>> 주성분 모델: " + componentModel);
		return componentModel;
	}
}
