package com.psserver.app.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.psserver.app.model.ComponentModel;
import com.psserver.app.model.LoginModel;
import com.psserver.app.model.PillModel;

@Component
public class LoginDao implements Dao<LoginModel> {
	private JdbcTemplate jdbcTemplate;
	
	public LoginDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	// ȸ������ ó��
	@Override
	public Boolean create(LoginModel loginModel) {
		String sql = "INSERT INTO MEMBER VALUES(?, ?)";
		try {
			System.out.println(">>> ȸ������ ��û�� ���Խ��ϴ�. ID = " + loginModel.getId() + " PW = " + loginModel.getPw());
			jdbcTemplate.update(sql, loginModel.getId(), loginModel.getPw());
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	// ��й�ȣ üũ
	@Override
	public String get(String id) {
		String pw = "";
		String sql = "SELECT PW FROM MEMBER WHERE ID = ?";
		try {
			System.out.println(">>> �α��� ��û�� ���Խ��ϴ�. ID = " + id);
			pw = jdbcTemplate.queryForObject(sql, String.class, id);
			return pw;
		} catch(Exception e) {
			return null;
		}
	}
	
	// �̹��� ���� �����ͺ��̽��� ����
	@Override
	public void upload(String id, String filename){
		String sql = "INSERT INTO IMAGE VALUES (?, ?)";
		try {
			jdbcTemplate.update(sql, id, filename);
		} catch (Exception e) {
			System.out.println(">>> �̹��� ������ DB�� ����ϴ� ���� �����Ͽ����ϴ�.");
		}
	}
	
	// �˾� ���� �����ͺ��̽����� ã��
	@Override
	public PillModel pillInfo(Vector<String> infoStr) {
		PillModel pillModel = new PillModel();
		String sql = "";
		try {
			System.out.println(">>> ����, ����, ǥ�⳻������ �˻��մϴ�.");
			sql = "SELECT PILL_NAME, PILL_SERIAL, IS_PRESCRIPTION, APPEARANCE, BUSINESS_NAME, CLASSIFY, COMPONENT FROM PILL WHERE SHAPE=? AND COLOR_FRONT=? AND TEXT_FRONT LIKE ?";
			pillModel = jdbcTemplate.queryForObject(sql, prm, infoStr.elementAt(infoStr.size()-3), infoStr.elementAt(infoStr.size()-2), "%"+infoStr.elementAt(infoStr.size()-1)+"%");
			return pillModel;
		} catch (EmptyResultDataAccessException e) {
			try {
				System.out.println(">>> ǥ�� �������� �ٽ� �˻��մϴ�.");
				sql = "SELECT PILL_NAME, PILL_SERIAL, IS_PRESCRIPTION, APPEARANCE, BUSINESS_NAME, CLASSIFY, COMPONENT FROM PILL WHERE TEXT_FRONT LIKE ?";
				List<PillModel> pillModelList = jdbcTemplate.query(sql, prm, "%"+infoStr.elementAt(infoStr.size()-1)+"%");
				return pillModelList.get(0);
			} catch (Exception e2) {
				System.out.println(">>> �˻� ����� �������� �ʽ��ϴ�.");
				return null;
			}
		}
	}
	// �ּ��� ���� �����ͺ��̽����� ã��
	public ComponentModel getComponent(String name) {
		String sql = "";
		try {
			sql = "SELECT * FROM COMPONENT WHERE COMPONENT_NAME=? AND INJECTION_ROOT=?";
			List<ComponentModel> componentModelList = jdbcTemplate.query(sql, crm, name, "�汸");
			System.out.println(">>> �ּ����� ã�ҽ��ϴ�.");
			return componentModelList.get(0);
		} catch (EmptyResultDataAccessException e2) {
			System.out.println(">>> �ּ��� ������ �������� �ʽ��ϴ�.");
			return null;
		}
	}
	
	RowMapper<PillModel> prm = new RowMapper<PillModel>() {
		public PillModel mapRow(ResultSet rs, int rowNum) throws SQLException {
			PillModel pillModel = new PillModel();
			pillModel.setPill_name(rs.getString("PILL_NAME"));
			pillModel.setPill_serial(rs.getString("PILL_SERIAL"));
			pillModel.setIs_prescription(rs.getString("IS_PRESCRIPTION"));
			pillModel.setAppearance(rs.getString("APPEARANCE"));
			pillModel.setBusiness_name(rs.getString("BUSINESS_NAME"));
			pillModel.setClassify(rs.getString("CLASSIFY"));
			pillModel.setComponent(rs.getString("COMPONENT"));
			return pillModel;
		}
	};
	
	RowMapper<ComponentModel> crm = new RowMapper<ComponentModel>() {
		public ComponentModel mapRow(ResultSet rs, int rowNum) throws SQLException {
			ComponentModel componentModel = new ComponentModel();
			componentModel.setComponent_code(rs.getString("COMPONENT_CODE"));
			componentModel.setComponent_name(rs.getString("COMPONENT_NAME"));
			componentModel.setComponent_name_eng(rs.getString("COMPONENT_NAME_ENG"));
			componentModel.setType_code(rs.getString("TYPE_CODE"));
			componentModel.setType_name(rs.getString("TYPE_NAME"));
			componentModel.setInjection_root(rs.getString("INJECTION_ROOT"));
			componentModel.setInjection_unit(rs.getString("INJECTION_UNIT"));
			componentModel.setInjection_day(rs.getString("INJECTION_DAY"));
			return componentModel;
		}
	};
}
