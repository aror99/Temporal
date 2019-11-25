package com.example.demo;

import com.jcraft.jsch.UserInfo;

public interface SftpConnectionInfo extends UserInfo{

	String getHost();
	String getUsername();
	int getPort();
	
}
