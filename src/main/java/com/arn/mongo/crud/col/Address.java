package com.arn.mongo.crud.col;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Address {
	
	private String addrLine1;
	
	private String addrLine2;
	
	private String city;
	
	private String state;
	
	private String country;
	
	private String zipCd;
	

}
