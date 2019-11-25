package com.example.demo;


public interface RowFileMapper<T> {
	
	T mapRow(String row, int rowNum);

}
