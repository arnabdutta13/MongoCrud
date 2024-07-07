package com.arn.mongo.crud.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.arn.mongo.crud.col.Employee;

public interface EmployeeRepo extends MongoRepository<Employee, String> {
	
	@Query("{name : '?0'}")
	Employee findEmployeeByName(String name);
	
	long count();

}
