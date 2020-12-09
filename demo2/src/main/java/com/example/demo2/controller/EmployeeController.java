package com.example.demo2.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.example.demo2.dto.EmployeeDTO;
import com.example.demo2.service.EmployeeService;

/**
 * @className EmployeeController
 * @description 아래 예제는 Employee 정보를 관리하는 서비스 Controller입니다.
 *              GET Method와 POST Method를 가지고 있습니다.
 */

@RestController
public class EmployeeController{
    Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService service;

    /**
     * @methodName getAllEmployeesInfo
     * @throws     Exception
     * @description GET Request를 받아서 전체 Employee의 정보를 조회하는 메소드
     */
    @RequestMapping(value="/AllEmployeeInfo", method=RequestMethod.GET)
    public ResponseEntity<EmployeeDTO.Response> getAllEmployeesInfo() throws Exception{
        EmployeeDTO.Response responseBody = null;
        try {
            responseBody = service.getAllEmployeeInfo();
        }catch(Exception e) {
            throw e;
        }
        return ResponseEntity.ok().body(responseBody);
    }

    /**
     * @methodName getEmployeesInfo
     * @param      String
     * @return     ResponseEntity
     * @throws     Exception
     * @description GET Request를 받아서 Employee의 정보를 조회하는 메소드
     */
    @RequestMapping(value="/EmployeeInfo", method=RequestMethod.GET)
    public ResponseEntity<EmployeeDTO.Response> getEmployeesInfo(@RequestParam Integer id) throws Exception{
        EmployeeDTO.Response  responseBody = null;
        try {
            responseBody = service.getEmployeeInfo(id);
        }catch(Exception e) {
            throw e;
        }
        return ResponseEntity.ok().body(responseBody);
    }

    /**
     * @methodName cudEmployeesInfo
     * @throws     Exception
     * @description GET Request를 받아서 Employee의 정보를 조회하는 메소드
     */
    @RequestMapping(value="/EmployeeInfo", method=RequestMethod.POST)
    public ResponseEntity<EmployeeDTO.Response> cudEmployeesInfo(@RequestBody EmployeeDTO.Request request) throws Exception{
        // cudFlag 값에 의해 분기처리
        EmployeeDTO.Response response = null;
        try {
            if("C".equals(request.getCudFlag()) || "U".equals(request.getCudFlag())) {
                response = service.mergeEmployeeInfo(request);
            } else if("D".equals(request.getCudFlag())) {
                response = service.deleteEmployeeInfo(request);
            }
        }catch(Exception e) {
            throw e;
        }
        return ResponseEntity.ok().body(response);
    }
}
