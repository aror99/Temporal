package com.example.demo;

import java.io.InputStream;

public interface InputStreamExtractor<T> {
	
	T extractData(InputStream is);

}
