package com.arn.mongo.crud.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.bson.BSONDecoder;
import org.bson.BSONObject;
import org.bson.BasicBSONDecoder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arn.mongo.crud.col.Address;
import com.arn.mongo.crud.col.Employee;
import com.arn.mongo.crud.repo.EmployeeRepo;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/employee")
public class EmployeeController {

	private final EmployeeRepo employeeRepo;
	
	private final KafkaTemplate<String, Object> kafkaTemplate;
	
	@GetMapping("/createEmployee/{nums}")
	public String createEmployees(@PathVariable("nums")int nums) {
		long start = System.currentTimeMillis();
		int batchSize = 500;
		int numOfBatch = nums / batchSize;
		int remainder = nums < batchSize ? nums : nums % batchSize;
		System.out.println("" + numOfBatch + " " + remainder);
		/**/
		for (int i = 0; i < numOfBatch; i++) {
			insertData(batchSize);
		}
		if (remainder > 0) {
			insertData(remainder);
		}
		log.debug("Repords inserted");
		return "SUCCESS - done in " + (System.currentTimeMillis() - start) + "millis";
	}
	
	private void insertData(int nums) {
		List<Employee> list = new ArrayList<>();
		long count = employeeRepo.count() + 1;
		for (long i = count; i < (count + nums); i++) {
			Address addr = new Address("AddrLine1-" + i, "AddrLine2-" + i, "City1" + i, "State-" + i, "Country-" + i, "Zip-" + i);
			Employee emp = new Employee(null, "Name-" + i, "Dept-" + i, i, addr);
			list.add(emp);				
		}
		employeeRepo.insert(list);
	}
	
	@GetMapping("/readBson")
	public String readBson() throws FileNotFoundException, InterruptedException, ExecutionException, ClassNotFoundException {
		long start = System.currentTimeMillis();
		File file = new File("C:\\STS\\Java\\Mongo\\dump\\test\\employee.bson");
        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

        BSONDecoder decoder = new BasicBSONDecoder();
        int count = 0;	
        try {
            while (inputStream.available() > 0) {

                BSONObject obj = decoder.readObject(inputStream);
                if(obj == null) {
                    break;
                }
                System.out.println(obj);
                ObjectMapper om = new ObjectMapper();
                String json = bsonToJson(obj);
                Class cls = Class.forName(obj.get("_class").toString());
                Object em = om.readValue(json, cls);
                System.out.println(cls.getSimpleName() + " : " + em);
                kafkaTemplate.send(cls.getSimpleName().toLowerCase(), em).get();
                count++;
                
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
            }
        }
        System.err.println(String.format("%s objects read", count));
        return "SUCCESS - done in " + (System.currentTimeMillis() - start) + "millis";
	}
	
	private String bsonToJson(BSONObject bson) {
		StringBuilder sb = new StringBuilder("{");
		boolean isFirst = true;
		for (String key : bson.keySet()) {
			if ("_class".equals(key)) {
				continue;
			}
			if (isFirst) {
				isFirst = false;
			} else {
				sb.append(",");
			}
			sb.append("\"").append(key).append("\":");
			if (bson.get(key) instanceof BSONObject) {
				sb.append(bsonToJson((BSONObject)bson.get(key))).toString();
			} else {
				sb.append("\"" + bson.get(key) + "\"");
			}
			
		}
		sb.append("}");
		System.out.println(sb.toString());
		return sb.toString();
	}
}
