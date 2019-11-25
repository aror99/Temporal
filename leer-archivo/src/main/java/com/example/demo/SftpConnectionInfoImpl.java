package com.example.demo;

import java.util.Map;


public class SftpConnectionInfoImpl implements SftpConnectionInfo {

	private String passPhrase;
	private String password;
	private String host;
	private String username;
	private int    port;
	private Map<String, String> options;
	
	//TODO convendria un constructor donde le mandes todo o atraves de un mapa de valores
	//TODO falta pasarle parametros por mapa
	public SftpConnectionInfoImpl() {}
		
	
	

	public SftpConnectionInfoImpl setOptions(Map<String, String> options) {
		this.options = options;
		return this;
	}




	public SftpConnectionInfoImpl setPassPhrase(String passPhrase) {
		this.passPhrase = passPhrase;
		return this;
	}


	public SftpConnectionInfoImpl setPort(int port) {
		this.port = port;
		return this;
	}



	public SftpConnectionInfoImpl setPassword(String password) {
		this.password = password;
		return this;
	}



	public SftpConnectionInfoImpl setHost(String host) {
		this.host = host;
		return this;
	}



	public SftpConnectionInfoImpl setUsername(String username) {
		this.username = username;
		return this;
	}


	
	public Map<String, String> getOptions() {
		return options;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public String getPassphrase() {
		return this.passPhrase;
	}

	@Override
	public String getPassword() {
		return this.password;
	}
	

	@Override
	public String getHost() {
		return this.host;
	}

	@Override
	public String getUsername() {
		return this.username;
	}
	
	
	//TODO estos de abajo que pedo?????
	
	@Override
	public boolean promptPassword(String message) {
		// TODO Que pedro!!!!
		return false;
	}

	@Override
	public boolean promptPassphrase(String message) {
		// TODO Que pedro!!!!
		return false;
	}

	@Override
	public boolean promptYesNo(String message) {
		// TODO Que pedro!!!!
		return false;
	}

	@Override
	public void showMessage(String message) {
		// TODO Que pedro!!!!
		
	}


	
}
