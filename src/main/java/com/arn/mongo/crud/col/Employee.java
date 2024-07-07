package com.arn.mongo.crud.col;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document("employee")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
	
	private String _id;

	private String name;
	
	private String department;
	
	private float salary;
	
	private Address addr;
}
