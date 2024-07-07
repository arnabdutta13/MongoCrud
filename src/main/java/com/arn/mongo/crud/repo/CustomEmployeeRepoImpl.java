package com.arn.mongo.crud.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.arn.mongo.crud.col.Employee;
import com.mongodb.client.result.UpdateResult;

public class CustomEmployeeRepoImpl implements CustomEmployeeRepo {
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void updateEmployeeSalary(String employeeName, float salary) {
		Query query = new Query(Criteria.where("name").is(employeeName));
		Update update = new Update();
		update.set("salary", salary);
		
		UpdateResult result = mongoTemplate.updateFirst(query, update, Employee.class);
		
		if(result == null)
			System.out.println("No documents updated");
		else
			System.out.println(result.getModifiedCount() + " document(s) updated..");
	}

}
