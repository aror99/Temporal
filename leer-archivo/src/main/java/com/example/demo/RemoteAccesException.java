package com.example.demo;

public class RemoteAccesException extends RuntimeException{

	//TODO Probar si se anida


	private static final long serialVersionUID = 1L;

	
	public RemoteAccesException(Throwable cause) {
	        super(cause);
	}

	public RemoteAccesException(String msg) {
		super(msg);
	}


	public RemoteAccesException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
