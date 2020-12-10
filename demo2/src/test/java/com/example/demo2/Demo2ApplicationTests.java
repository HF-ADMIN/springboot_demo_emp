package com.example.demo2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.text.SimpleDateFormat;

import com.example.demo2.dao.EmployeeDAO;
import com.example.demo2.repository.EmployeeRepository;
import com.example.demo2.service.EmployeeService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Demo2ApplicationTests {

	@Autowired
	EmployeeRepository repository;

	@Autowired
	EmployeeService service;

	@Test
	void contextLoads() {
	}

	@Test
	public void employeeTest() throws SQLException {

		try {
			SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
			EmployeeDAO emp = new EmployeeDAO();
			
			emp.setEmployee_number(2017112);
			emp.setName("SoonDoMan");
			emp.setPosition("111");
			emp.setSign_up_date(transFormat.parse("1234-11-03"));

			Integer what = repository.update(emp);
			assertEquals(0, what);
		}catch(Exception e) {
			e.printStackTrace(); 
		}
		
	}

	@Test
	public void employeeGetTest() throws Exception {
		try{
			service.getEmployeeInfo(2);
		}catch(Exception e ) {
			e.printStackTrace();
		}
	}

	@Test
	public void employeeUpdateTest() throws Exception {
		try {
			SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
			EmployeeDAO emp = new EmployeeDAO();
			
			emp.setEmployee_number(2017112);
			emp.setName("SoonDoMan");
			emp.setPosition("111");
			emp.setSign_up_date(transFormat.parse("1234-11-03"));

			Integer what = repository.update(emp);
			assertEquals(0, what);
		}catch(Exception e) {
			e.printStackTrace(); 
		}
	}

}
