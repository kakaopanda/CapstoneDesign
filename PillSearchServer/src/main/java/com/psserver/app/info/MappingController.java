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
	
	// �α��� ��û ó��
	@GetMapping("login/{id}/{pw}")
	public String login(@PathVariable String id, @PathVariable String pw) {
		String getPw = loginDao.get(id);
		if(getPw.equals(pw)) {
			System.out.println(">>> �α��� ���� ��ġ!");
			return "SUCCESS";
		} else {
			System.out.println(">>> �α��� ���� ����ġ!");
			return "FAIL";
		}
	}
	
	// ȸ������ ��û ó��
	@GetMapping("signup/{id}/{pw}")
	public String signup(@PathVariable String id, @PathVariable String pw) {
		LoginModel model = new LoginModel(id, pw);
		if(loginDao.create(model)) {
			System.out.println(">>> ȸ������ ����!");
			return "SUCCESS";
		} else {
			System.out.println(">>> ȸ������ ����!");
			return "FAIL";
		}
	}
	
	// ���÷κ��� ���� �˾� �̹����� ������ ����
	@PostMapping("upload")
	public ResponseEntity<String> upload(MultipartFile file) throws IllegalStateException, IOException{
		System.out.println(">>> ���÷κ��� �̹����� ���۹޾ҽ��ϴ�.");
		storageService.store(file);
		System.out.println(">>> �̹��� ���� ����!");
		return new ResponseEntity<>("", HttpStatus.OK);
	}
	
	// ���÷κ��� ���� �˾� �̹����� �����ͺ��̽��� ���
	@GetMapping("upload/{id}/{filename}")
	public String upload(@PathVariable String id, @PathVariable String filename) {
		System.out.println(">>> ���۹��� �̹����� ������ DB�� ����մϴ�.");
		MappingController.fileId = id;
		loginDao.upload(id, filename);
		System.out.println(">>> �̹��� ���� DB ��� ����!");
		return "SUCCESS";
	}
	
	// ���ÿ��κ��� ���� �˾� �̹����� �м��𵨷� ������ �м�
	@GetMapping("analyze/{id}/{filename}")
	@ResponseBody
	public PillModel analyze(@PathVariable String id, @PathVariable String filename) {
		System.out.println(">>> ���۹��� �̹����� �м��ϱ� ���� �м����� �����մϴ�.");
		ExePython exePython = new ExePython(id, filename);
		exePython.start();
		try {
			exePython.join();
		} catch (Exception e) {
			System.out.println(">>> �м��𵨿��� ������ �߻��Ͽ����ϴ�. " + e.toString());
		}
		System.out.println(">>> �м� �Ϸ�!");
		System.out.println(">>> �м� ����� ������� �˻��� �����մϴ�.");
		PillModel pillModel = loginDao.pillInfo(exePython.getResult());
		System.out.println(">>> �˻��� �Ϸ�Ǿ����ϴ�.");
		System.out.println(">>> �˾� ��: " + pillModel);
		return pillModel;
	}
	
	// �м��� ����� �˸´� �˾� �̹��� ���÷� ����
	@GetMapping("image/{filename}")
	public ResponseEntity<Resource> findImage (@PathVariable("filename") String serial) {
		Path path = Paths.get("C:\\users\\lukai\\desktop\\pillImage\\");
		HttpHeaders header = new HttpHeaders();
		Resource resource = new FileSystemResource(path + "\\" + serial + ".jpg");
		System.out.println(">>> �˾��� �̹����� ������ �ֽ��ϱ�?: " + resource.exists());
		System.out.println(">>> �м� ����� �´� �˾��� �̹����� ���÷� �����մϴ�.");
		if(!resource.exists())
			return new ResponseEntity<Resource>(HttpStatus.NOT_FOUND);	
		try {
			header.add("Content-type", Files.probeContentType(path));
			System.out.println(">>> �̹��� ���� ����!");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new ResponseEntity<Resource>(resource, header, HttpStatus.OK);
	}
	
	// �ռ� �м��� �����Ͽ� ����ڰ� ���� �Է��� �˾� �����͸� ������� �˻�
	@GetMapping("text/{color}/{shape}/{text}")
	@ResponseBody
	public PillModel text(@PathVariable String color, @PathVariable String shape, @PathVariable String text) {
		System.out.println(">>> ����ڰ� ���� �Է��� �˾� �����͸� ������� �˻��� �����մϴ�.");
		Vector<String> v = new Vector<String>();
		v.add(color); v.add(shape); v.add(text);
		PillModel pillModel = loginDao.pillInfo(v);
		System.out.println(">>> �˻��� �Ϸ�Ǿ����ϴ�.");
		System.out.println(">>> �˾� ��: " + pillModel);
		return pillModel;
	}
	
	// �ּ��� ���� ���÷� ����
	@GetMapping("component/{name}")
	public ComponentModel component(@PathVariable String name) {
		System.out.println(">>> �˻��� �˾��� ������� �ּ����� �˻��մϴ�.");
		ComponentModel componentModel = loginDao.getComponent(name);
		System.out.println(">>> �ּ��� ��: " + componentModel);
		return componentModel;
	}
}
