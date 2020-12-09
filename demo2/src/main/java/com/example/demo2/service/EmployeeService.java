package com.example.demo2.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.demo2.dao.EmployeeDAO;
import com.example.demo2.dto.EmployeeDTO;
import com.example.demo2.repository.EmployeeRepository;
import com.example.demo2.util.ServiceUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @className   EmployeeService
 * @mehtod      getAllEmployeeInfo, getEmployeeInfo, mergeEmployeeInfo, deleteEmployeeInfo
 *              parsingDTO, getFlaskService, postFlaskService
 * @description 아래 예제는 Employee 정보를 관리하는 서비스 Service 입니다.
 */
@Service
public class EmployeeService { 

    Logger logger = LoggerFactory.getLogger(EmployeeService.class);
    private RestTemplate restTemplate;

    @Autowired
     EmployeeRepository repository;

    @Autowired
    public EmployeeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    /**
     * @methodName  getAllEmployeeInfo
     * @return      EmployeeDTO.Response
     * @throws      Exception
     * @description 전체 Employeee 정보를 가져오는 Mehtod 입니다.
     *              employee table에서 가져온 정보와 Python Flask 서비스와 연동하여 가져온 employee_detail table의 정보를 종합한 전체 데이터 리턴.
     */
    public EmployeeDTO.Response getAllEmployeeInfo() throws Exception{ 

        EmployeeDTO.Response response = new EmployeeDTO.Response();
        List<EmployeeDTO.Response> inner_list = new ArrayList<EmployeeDTO.Response>();
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            // employee table에서 데이터를 가져와서 for문을 돌려 employee데이터 response에 담는다.
            for(EmployeeDAO dao : repository.findAll()) {
                EmployeeDTO.Response inner_response = new EmployeeDTO.Response();
                inner_response.setId(dao.getId());
                inner_response.setName(dao.getName());
                inner_response.setEmployee_number(String.valueOf(dao.getEmployee_number()));
                if(dao.getSign_up_date() != null) inner_response.setSign_up_date(transFormat.format(dao.getSign_up_date()));
                inner_response.setPosition(dao.getPosition());
                inner_list.add(inner_response);
            }
            response.setList(inner_list);

        }catch (Exception e) {
            e.printStackTrace();
            throw e;
            
        }
        
        return response;
    }


    /**
     * @methodName  getEmployeeInfo
     * @param       Integer
     * @return      EmployeeDTO.Response
     * @throws      Exception
     * @description Employeee 정보를 가져오는 Mehtod 입니다.
     *              employee table에서 가져온 정보와 Python Flask 서비스와 연동하여 가져온 employee_detail table의 정보를 종합한 데이터 리턴.
     */
    public EmployeeDTO.Response getEmployeeInfo(Integer id) throws Exception{

        EmployeeDTO.Response response = new EmployeeDTO.Response();
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
        Map<String, Object> map = null;
        JSONObject json = null;

        // request에서 id를 가지고와서 employee table에서 데이터를 조회한다.
        try {
            // test
            EmployeeDAO dao =  repository.findById(id);
            logger.info("=====================> [EmployeeService / getEmployeeInfo] dao : " + dao);

            // flask getMethod Call
            // detail을 arg로 넣어서 flask Method를 태운다.
            String baseURL = "http://" + ServiceUtil.FLASK_URI + "/" + ServiceUtil.FLASK_GET_SERVICE + "?employee_number=" + dao.getEmployee_number();
            logger.info("=====================> [EmployeeService / getEmployeeInfo] baseURL : " + baseURL);
            ResponseEntity<String> flaskResponse = restTemplate.getForEntity(
                baseURL, String.class);  
            
            
            if(flaskResponse != null && flaskResponse.getBody() != null) {
                logger.info(
                        "========================== > ㄸ로로롱 ");
                // From String to JSONOBject
                JSONParser jsonParser = new JSONParser();
                json = (JSONObject)jsonParser.parse(flaskResponse.getBody());
            }
            
            // flaskResponse의 resultCode가 200일 때 
            if("200".equals(json.get("resultCode").toString())) {   
                logger.info("================================================================== 200");
                response.setId(dao.getId());
                response.setName(dao.getName());
                response.setEmployee_number(String.valueOf(dao.getEmployee_number()));
                if(dao.getSign_up_date() != null) response.setSign_up_date(transFormat.format(dao.getSign_up_date()));
                response.setPosition(dao.getPosition());
                response.setAddress((String)json.get("addres"));
                response.setEmail((String)json.get("email"));
                response.setAge(Integer.valueOf(json.get("age").toString()));
                response.setPhoneNum((String)json.get("phoneNum"));
                response.setMemo((String)json.get("memo"));
            } 
            // flaskResponse의 resultCode가 200일 때 
            else if("500".equals(json.get("resultCode"))) {
                response.setResultCode("500");
            }
        
        }catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        logger.info("============================================> response : " + response);
        return response;
    }


    /**
     * @methodName  insertEmployeeInfo
     * @param       EmployeeDTO.Request
     * @return      EmployeeDTO.Response
     * @throws      Exception
     * @description Employees 정보를 Insert하는 Mehtod입니다.
     *              employee table과 employe_detail(flask) 에 데이터를 모두 Insert합니다.
     */
   public EmployeeDTO.Response mergeEmployeeInfo(EmployeeDTO.Request request) throws Exception {

       // employee table insert
       // 단순 demo용이기 때문에 방어로직은 생략
       EmployeeDTO.Response response = new EmployeeDTO.Response();
       
       try {
            // employee table에 merge
            EmployeeDAO dao = new EmployeeDAO();
            parsingDTO(request, dao);
            repository.save(dao);
            Map<String, Object> detailMap = new HashMap<>();

            logger.info("====================> [EmployeeService / insertEmployeeInfo] jsonObject");
            // for(String key : jsonObject.keySet()) {
            //     logger.info("                   key( "+ key + " ) : value( " + jsonObject.get(key) + " )");
            // }

            

            // TODO Auto-generated method stub
            // MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
            // Map map = new HashMap<String, String>();
            // map.put("Content-Type", "application/json");
            // headers.setAll(map);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            
            Map<String, Object> req_payload = new HashMap<>();
            req_payload.put("cudFlag", request.getCudFlag());
            req_payload.put("id", request.getId());
            req_payload.put("employee_number", request.getEmployee_number());
            req_payload.put("age", request.getAge());
            req_payload.put("email", request.getEmail());
            req_payload.put("phoneNum", request.getPhoneNum());
            req_payload.put("address", request.getAddress());
            req_payload.put("memo", request.getMemo());

            HttpEntity<?> requestEntity = new HttpEntity<>(req_payload, headers);

            // detail을 arg로 넣어서 flask Method를 태운다.
            ResponseEntity<String> flaskResponse = restTemplate.postForEntity(
                "http://" + ServiceUtil.FLASK_URI + "/" + ServiceUtil.FLASK_POST_SERVICE, requestEntity, String.class);  
                
            logger.info(
                    "========================== > flaskResponse : " + flaskResponse.getBody());

            // From String to JSONOBject
            JSONParser jsonParser = new JSONParser();
            JSONObject json = (JSONObject)jsonParser.parse(flaskResponse.getBody());

            if(flaskResponse != null && flaskResponse.getBody() != null) response.setResultCode((String)json.get("resultCode"));
            

       }catch(Exception e) {
            e.printStackTrace(); 
            throw e;
       }

       logger.info("============================================> response : " + response);
       return response;
   }


    /**
     * @methodName  deleteEmployeeInfo
     * @param       EmployeeDTO.Request
     * @return      EmployeeDTO.Response
     * @throws      Exception
     * @description Employees 정보를 Delete하는 Mehtod입니다.
     *              employee table과 employe_detail(flask) 에 데이터를 모두 Delete합니다.
     */
    public EmployeeDTO.Response deleteEmployeeInfo(EmployeeDTO.Request request) throws Exception{

        // employee table delete
        EmployeeDTO.Response response = new EmployeeDTO.Response();
        try {
            // employee table에서 데이터를 삭제하는 로직
            EmployeeDAO dao = new EmployeeDAO();
            parsingDTO(request, dao);
            repository.delete(dao);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            
            Map<String, Object> req_payload = new HashMap<>();
            req_payload.put("cudFlag", request.getCudFlag());
            req_payload.put("employee_number", request.getEmployee_number());

            HttpEntity<?> requestEntity = new HttpEntity<>(req_payload, headers);
            
            // detail을 arg로 넣어서 flask Method를 태운다.
            ResponseEntity<String> flaskResponse = restTemplate.postForEntity(
                "http://" + ServiceUtil.FLASK_URI + "/" + ServiceUtil.FLASK_POST_SERVICE, requestEntity, String.class);  
            logger.info(
                    "========================== > flaskResponse : " + flaskResponse.getBody());

            // From String to JSONOBject        
            JSONParser jsonParser = new JSONParser();
            JSONObject json = (JSONObject)jsonParser.parse(flaskResponse.getBody());

            if(flaskResponse != null && flaskResponse.getBody() != null) response.setResultCode((String)json.get("resultCode"));

        }catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
        
        logger.info("============================================> response : " + response);
        return response;
    }


    /**
     * @methodName  parsingDTO
     * @param       Object, Object
     * @throws      Exception
     * @description DTO를 parsing하는 Method입니다.
     */
    private void parsingDTO(Object from, Object to) {

        if(from instanceof EmployeeDTO.Request) {
            EmployeeDTO.Request request = (EmployeeDTO.Request) from;

            SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
            if(to instanceof EmployeeDAO) {
                EmployeeDAO temp = (EmployeeDAO) to;
                try {
                    //temp = new EmployeeDAO(request.getId(), request.getName(), request.getEmployee_number(), transFormat.parse(request.getSign_up_date()), request.getPosition());
                    temp.setId(request.getId());
                    temp.setName(request.getName());
                    temp.setEmployee_number(Integer.valueOf(request.getEmployee_number()));
                    if(request.getSign_up_date() != null) temp.setSign_up_date(transFormat.parse(request.getSign_up_date()));
                    temp.setPosition(request.getPosition());
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
        } else if(from instanceof EmployeeDTO.Detail) {
            EmployeeDTO.Detail detail = (EmployeeDTO.Detail) from;
            if(to instanceof EmployeeDTO.Request) {
                EmployeeDTO.Request req = (EmployeeDTO.Request) to;
                req.setId(detail.getId());
                req.setEmployee_number(detail.getEmployee_number());
                req.setAge(detail.getAge());
                req.setEmail(detail.getEmail());
                req.setPhoneNum(detail.getPhoneNum());
                req.setAddress(detail.getAddress());
            }
        }
        
    }

    /**
     * @methodName  getFlaskService
     * @return      JSONObject
     * @throws      Exception
     * @description DTO를 parsing하는 Method입니다.
     */
    public JSONObject getFlaskService() throws Exception {
        return new JSONObject();
    }


    /**
     * @methodName  postFlaskService
     * @param       EmployeeDTO.Detail
     * @return      JSONObject
     * @throws      Exception
     * @description DTO를 parsing하는 Method입니다.
     */
    public JSONObject postFlaskService(EmployeeDTO.Detail detail) throws Exception {
        return new JSONObject();
    }
}
