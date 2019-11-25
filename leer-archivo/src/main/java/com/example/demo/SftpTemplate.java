package com.example.demo;


import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.management.RuntimeErrorException;
import javax.swing.tree.RowMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

	//System.out.println(text);
/*    channel.exit();
channel.disconnect();
session.disconnect();
} catch (SftpException e) {
channel.exit();
channel.disconnect();
session.disconnect();
System.out.println(">>>>"+e.getMessage());
// TODO Marcar que hubo un error guardando este rollo
e.printStackTrace();
} 
//TODO falta cachar cualquier cosa y cerrar
        
if(!channel.isClosed()) {
System.out.println("Aun no esta cerrado!!!!!!!!!!!!!!!");
channel.exit();
channel.disconnect();
session.disconnect();
}else{
System.out.println("YAAAA esta cerrado!!!!!!!!!!!!!!!");
}*/

public class SftpTemplate {
	
	//TODO que pasa si es null el datasource
	private Logger log = LoggerFactory.getLogger(getClass());
	
	
	
	private final static String SFTP = "sftp";
	
	
	ChannelSftp channelSftp;
	String remoteFile;
	
	private SftpConnectionInfo sftpConnectionInfo;
	
	public SftpTemplate() {}
	
	public SftpTemplate(SftpConnectionInfo sftpConnectionInfo) {
		this.sftpConnectionInfo = sftpConnectionInfo;
	}
	
//TODO Valida lo que si es necesario al inicio (params)	
	public <T> List<T> getRemoteFileAsObjectList(String remoteFile, RowFileMapper<T> rowFileMapper) throws RuntimeException{
			
		List<T> resultList = new ArrayList<>();
		this.remoteFile= remoteFile;		
		
		
		try {
			procesa(resultList,rowFileMapper);		
		}catch(Exception e ) {

			closeAndSendException(e);
									
		} finally {
						
			closeAndSendException(null);
		}
		
		
		return resultList;
	}
	
	private void closeAndSendException(Throwable e) throws RuntimeException{
		
		try {
			closeChannel();
		}catch(Exception e1 ) {
			throw new RuntimeException("Error cerrando recursos.",e1);
		}
		
		if(e != null)
			throw new RuntimeException(e);		
	}
	
	private <T> void procesa(List<T> resultList, RowFileMapper<T> rowFileMapper) {
		
		try {
			channelSftp = getNewChannel();
		} catch (JSchException e) {
			//closeChannel(channelSftp,e);
			throw new RemoteAccesException("Problema en la creacion del canal y la sesion, se intentaran cerrar recursos ["+e.getMessage()+"]",e); //TODO probar todas la excepciones
		} catch(Exception e) {
			//closeChannel(channelSftp,e);
			throw new RemoteAccesException("Se genero una excepcion no prevista, se intentara cerrar el canal", e); //TODO Por ejemplo, si fuese null alguno de los parametros
			 //TODO meter datos a la excepcion
		}
		finally {
			System.out.println("CAYENDO AQUI!!!!!!!!!!!!!!!!!");
			//closeChannel(channelSftp);
		}
		
		
		
		if(channelSftp == null) {
			//closeChannel(channelSftp);
			throw new NullPointerException("El canal no puede ser null"); //TODO seria bueno cambiarla por una personalizada
		}
		
						
		List<String> dataFromConnection = new ArrayList<>();
		try {
			dataFromConnection = processConnection(remoteFile, channelSftp);
		} catch (SftpException e) {
			// TODO Que hacemos con esto???
			e.printStackTrace();
		}
			
		//TODO poner aqui una lista de excepciones y marcar cuales son las rows que estan fallando
		
		//TODO y si hay error aqui?????
		int rowNum=0;
		//TODO y si el rowmapper regresa una excepcion????
		//Si regresa null el rowmapper o encuentro una excepcion no se incluye
		for(String rowAsString : dataFromConnection) {
			T row = null;
			
			try {
				row = rowFileMapper.mapRow(rowAsString,rowNum);
				if(row != null) {
					resultList.add(row);
					rowNum++;
				}
			}catch(Exception e) {
				//TODO esto sera mas elaborado, ya que quiero continuar si hay excepciones
				throw new RemoteAccesException("Error iterando rowMapper, se detiene el procesamiento",e);								
			}
			
		}
		
	}

	public SftpConnectionInfo getSftpConnectionInfo() {
		return sftpConnectionInfo;
	}

	public void setSftpConnectionInfo(SftpConnectionInfo sftpConnectionInfo) {
		this.sftpConnectionInfo = sftpConnectionInfo;
	}
	
	private ChannelSftp getNewChannel() throws JSchException {
		
        	String host = this.sftpConnectionInfo.getHost();
        	String username = this.sftpConnectionInfo.getUsername();
        	Integer port = this.sftpConnectionInfo.getPort();
        
        
		 	JSch jsch=new JSch();
	        Session session=null;
	        ChannelSftp channelSftp=null;
	        Channel channel=null;
	        										
			session = jsch.getSession(username, host,port);				
			session.setUserInfo(this.sftpConnectionInfo);								
		    session.connect();

		    channel=session.openChannel(SftpTemplate.SFTP);
		    channel.connect();
		    channelSftp=(ChannelSftp)channel;
		        		
		return channelSftp;
	}
	
	
	//TODO por lo pronto es void
	private <T> List<String> processConnection(String remoteFile, ChannelSftp channel) throws SftpException {
		
		List<String> streamAsString = new ArrayList<>();
		
		        	
        String text = "";
        Scanner scanner = null;  
        
        //TODO cerrar el scanner
        try {
        	
        	InputStream inputStream = channel.get(remoteFile);
        	scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name()); //TODO la codificacion debera ser configurable
            while(scanner.hasNext()) { //TODO que pasa cuando el archivo en casos especiales, es decir, viene vacio, no trae el formato adecuado, etc.
            	text = scanner.useDelimiter("\n").next(); //TODO delimiter debe ser configurable
            	streamAsString.add(text);
            }
            
       }catch(Exception e) {
    	   //TODO es cierto que finally pasa aun cuando cae en excepcion??
           throw new RemoteAccesException("Error extrayendo informacion de la conexion", e);
       }finally {
    	   log.info("Cerrando recursos"); //TODO quitar este
    	   if(scanner != null) { 
    		   scanner.close(); 
    	   }    	   
    	   closeChannel();
       }
        	    
	   return streamAsString;
	
	}
	
	
	private void closeChannel(/*Channel channel/*, Throwable throwable*/)  {
		
		//if(this.channelSftp == null) return;
		
		try {
			Session session = this.channelSftp.getSession();
			channelSftp.disconnect();
			session.disconnect();
			log.info("CLOSE_CHANNEL: Canal y sesion cerradas");
		} catch (JSchException e) {
			throw new RemoteAccesException("Error cerrando canal y sesion", e);			
		} catch(Exception e ){
			throw new RemoteAccesException("Error desconocido cerrando canal y sesion", e);
		} finally {
			try {
				if(channelSftp != null && !channelSftp.isClosed()){
					
					Session session = channelSftp.getSession();
					channelSftp.disconnect();
					session.disconnect();
									
				}else {
					log.info("CLOSE_CHANNEL: Ya estaba cerrado el canal!!!!"); //TODO quitar
				}
			} catch (JSchException e) {
				throw new RemoteAccesException("El canal y/o sesion quedaron abiertas, es necesario matar esas conexiones desde el servidor", e);		
			}
		}
		
		
		
	}
	

	
	
	

	//TODO como mandar varios archivos y que te regrese varios archivos con la misma conexion
}
