package com.example.demo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

//TODO pasarlo a java 6

@SpringBootApplication
public class LeerArchivoApplication {

    private final Logger logger = LoggerFactory.getLogger(LeerArchivoApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(LeerArchivoApplication.class, args);
	}
	
	@Bean
	public ApplicationRunner run() {
		return args -> {
			//TODO faltan las excepciones
			StopWatch watch = new StopWatch();
			Date inicio = new Date();
			watch.start();
	        logMemory();
	        
	        List<MiDto> lista = new ArrayList<>();
	        
		/*	procesaArchivoLocal(lista);
			procesaArchivoLocal(lista);
			procesaArchivoLocal(lista);
			procesaArchivoLocal(lista);
			procesaArchivoLocal(lista);
			procesaArchivoLocal(lista);
			procesaArchivoLocal(lista);
			procesaArchivoLocal(lista);
			procesaArchivoLocal(lista);
			procesaArchivoLocal(lista);
			*/
	
	        //TODO cambiarle el nombre 
	        String remoteFile="/home/bernux/cosa3.txt";
	        SftpConnectionInfoImpl sftpDatos = new SftpConnectionInfoImpl();	        
	        Map<String, String> options = new HashMap<>();
	        options.put("StrictHostKeyChecking", "no");
	        
	        sftpDatos.setHost("127.0.0.1")
	        		 .setPort(22)
	        		 .setPassword("aidees")
	        		 .setUsername("bernux")
	        		 .setOptions(options);
	        
	        
	        
	       /* String text = null;
    	    Scanner scanner = new Scanner(input, StandardCharsets.UTF_8.name());
    	    //TODO cerrar el scanner
    	    while(scanner.hasNext()) {
    	    	text = scanner.useDelimiter("\n").next();
    	    	//System.out.println(">>>"+text);
    	    	llenaLista(lista, text);
    	    }
    	    scanner.close();
    	    */
	        
	        SftpTemplate sftpTemplate = new SftpTemplate(sftpDatos);
	        
	        try {
		        lista = sftpTemplate.getRemoteFileAsObjectList(remoteFile, 
		        		new RowFileMapper<MiDto>() {
	
							@Override
							public MiDto mapRow(String row, int rowNum) {
								
								//TODO suponiendo que aqui hay una excepcion???
								String[] cols = row.split("\\|");							
								MiDto localRow = new MiDto();
						        
								localRow.setProp1(Integer.valueOf(cols[0]));
								localRow.setProp2(Integer.valueOf(cols[1]));
								localRow.setProp3(Integer.valueOf(cols[2]));
						        							
								return localRow;
							}
						}
		        );
	        }catch(Exception e) {
	        	e.printStackTrace();
	        }
	        
	        
	        
	       // procesaSftp(lista);
	        
	        
	        /*procesaSftp(lista);
	        procesaSftp(lista);
	        procesaSftp(lista);
	        procesaSftp(lista);
	        procesaSftp(lista);
	        procesaSftp(lista);
	        procesaSftp(lista);
	        procesaSftp(lista);
	        procesaSftp(lista);*/
	        
	        logger.info("Size list: "+lista.size());
			logger.info("========================= TERMINO!!!!! ======================================");
			
			 logMemory();
		        watch.stop();
		        
		        Date fin = new Date();
		        System.out.println(inicio);
		        System.out.println(fin);
		        System.out.println("Time Elapsed: " + watch.getTime(TimeUnit.SECONDS)); 
			
		};
	}
	
	
	private void procesaSftp(List<MiDto> lista) {
		
        
        JSch jsch=new JSch();
        Session session=null;
        ChannelSftp channelSftp=null;
        InputStream input = null;
        Channel channel=null;
        
		try {
						
			session = jsch.getSession("bernux", "127.0.0.1",22);
			
			session.setUserInfo(/*new SftpUserInfo() TODO la cagaste cambiando esrta clase*/null);
			session.setConfig("StrictHostKeyChecking", "no");
			
	        session.connect();

	        channel=session.openChannel("sftp");
	        channel.connect();
	        channelSftp=(ChannelSftp)channel;	        
	        
		} catch (JSchException e) {			
			// TODO Que hacemos con esto?
			e.printStackTrace();
		}
        
		
        try {
        	input = channelSftp.get("/home/bernux/cosa3.txt");        	
			//String text = IOUtils.toString(input, StandardCharsets.UTF_8.name());
        	  String text = null;
        	    Scanner scanner = new Scanner(input, StandardCharsets.UTF_8.name());
        	    //TODO cerrar el scanner
        	    while(scanner.hasNext()) {
        	    	text = scanner.useDelimiter("\n").next();
        	    	//System.out.println(">>>"+text);
        	    	llenaLista(lista, text);
        	    }
        	    scanner.close();
			
			
			
			//System.out.println(text);
			channelSftp.exit();
			channel.disconnect();
			session.disconnect();
		} catch (SftpException e) {
			channelSftp.exit();
			channel.disconnect();
			session.disconnect();
			System.out.println(">>>>"+e.getMessage());
			// TODO Marcar que hubo un error guardando este rollo
			e.printStackTrace();
		} /*catch (IOException e) {
			
			try {
				input.close();
			} catch (IOException e1) {
				// TODO Esto si es grave
				e1.printStackTrace();
			}
			// TODO Y si falla transformando a string? deberia cerrarlo no?
			e.printStackTrace();
		}*/
        
        //TODO falta cachar cualquier cosa y cerrar
        	        
		if(!channelSftp.isClosed()) {
			System.out.println("Aun no esta cerrado!!!!!!!!!!!!!!!");
			channelSftp.exit();
			channel.disconnect();
			session.disconnect();
		}else{
			System.out.println("YAAAA esta cerrado!!!!!!!!!!!!!!!");
		}
		
		logger.info("Size list: "+lista.size());
		logger.info("========================= TERMINO!!!!! ======================================");
		
	}
	
	
	
	private void llenaLista(List<MiDto> lista , String text) {
		
		//System.out.println(">>>>><"+text);
		
		//if(text.contains("\n"))
			//System.out.println("Tiene salto de linea");
		
		
		//System.out.println(text.split("\n").length);
		
		
		 MiDto row = new MiDto();
	        String[] rowSplitted = text.split("\\|");
	        
	        
	        	row.setProp1(Integer.valueOf(rowSplitted[0]));
	        	row.setProp2(Integer.valueOf(rowSplitted[1]));
	        	row.setProp3(Integer.valueOf(rowSplitted[2]));
	        	row.setProp4(Integer.valueOf(rowSplitted[3]));
	        	row.setProp5(Integer.valueOf(rowSplitted[4]));
	        	row.setProp6(Integer.valueOf(rowSplitted[5]));
	        	row.setProp7(Integer.valueOf(rowSplitted[6]));
	        	row.setProp8(Integer.valueOf(rowSplitted[7]));
	        	row.setProp9(Integer.valueOf(rowSplitted[8]));
	        	row.setProp10(Integer.valueOf(rowSplitted[9]));
	        	row.setProp11(Integer.valueOf(rowSplitted[10]));
	        	row.setProp12(Integer.valueOf(rowSplitted[11]));
	        	row.setProp13(Integer.valueOf(rowSplitted[12]));
	        	row.setProp14(Integer.valueOf(rowSplitted[13]));
	        	row.setProp15(Integer.valueOf(rowSplitted[14]));
	        	row.setProp16(Integer.valueOf(rowSplitted[15]));
	        	row.setProp17(Integer.valueOf(rowSplitted[16]));
	        	row.setProp18(Integer.valueOf(rowSplitted[17]));
	        	row.setProp19(Integer.valueOf(rowSplitted[18]));
	        	row.setProp20(Integer.valueOf(rowSplitted[19]));
	        	row.setProp21(Integer.valueOf(rowSplitted[20]));
	        	row.setProp22(Integer.valueOf(rowSplitted[21]));
	        	row.setProp23(Integer.valueOf(rowSplitted[22]));
	        	row.setProp24(Integer.valueOf(rowSplitted[23]));
	        	row.setProp25(Integer.valueOf(rowSplitted[24]));
	        	row.setProp26(Integer.valueOf(rowSplitted[25]));
	        	row.setProp27(Integer.valueOf(rowSplitted[26]));
	        	row.setProp28(Integer.valueOf(rowSplitted[27]));
	        	row.setProp29(Integer.valueOf(rowSplitted[28]));
	        	row.setProp30(Integer.valueOf(rowSplitted[29]));
	        	row.setProp31(Integer.valueOf(rowSplitted[30]));
	        	row.setProp32(Integer.valueOf(rowSplitted[31]));
	        	row.setProp33(Integer.valueOf(rowSplitted[32]));
	        	row.setProp34(Integer.valueOf(rowSplitted[33]));
	        	row.setProp35(Integer.valueOf(rowSplitted[34]));
	        	row.setProp36(Integer.valueOf(rowSplitted[35]));
	        	row.setProp37(Integer.valueOf(rowSplitted[36]));
	        	row.setProp38(Integer.valueOf(rowSplitted[37]));
	        	row.setProp39(Integer.valueOf(rowSplitted[38]));
	        	row.setProp40(Integer.valueOf(rowSplitted[39]));		      		       
	        
	        lista.add(row);
	}
	
	private void procesaArchivoLocal(List<MiDto> lista ) throws IOException {
                 
        
		File entrada = new File("/home/bernux/cosa2.txt");
		LineIterator it = FileUtils.lineIterator(entrada, "UTF-8");
		try {
			
		    while (it.hasNext()) {
		        String line = it.nextLine();
		        //System.out.println(line);
		        
		        MiDto row = new MiDto();
		        String[] rowSplitted = line.split("\\|");
		        
		        
		        	row.setProp1(Integer.valueOf(rowSplitted[0]));
		        	row.setProp2(Integer.valueOf(rowSplitted[1]));
		        	row.setProp3(Integer.valueOf(rowSplitted[2]));
		        	row.setProp4(Integer.valueOf(rowSplitted[3]));
		        	row.setProp5(Integer.valueOf(rowSplitted[4]));
		        	row.setProp6(Integer.valueOf(rowSplitted[5]));
		        	row.setProp7(Integer.valueOf(rowSplitted[6]));
		        	row.setProp8(Integer.valueOf(rowSplitted[7]));
		        	row.setProp9(Integer.valueOf(rowSplitted[8]));
		        	row.setProp10(Integer.valueOf(rowSplitted[9]));
		        	row.setProp11(Integer.valueOf(rowSplitted[10]));
		        	row.setProp12(Integer.valueOf(rowSplitted[11]));
		        	row.setProp13(Integer.valueOf(rowSplitted[12]));
		        	row.setProp14(Integer.valueOf(rowSplitted[13]));
		        	row.setProp15(Integer.valueOf(rowSplitted[14]));
		        	row.setProp16(Integer.valueOf(rowSplitted[15]));
		        	row.setProp17(Integer.valueOf(rowSplitted[16]));
		        	row.setProp18(Integer.valueOf(rowSplitted[17]));
		        	row.setProp19(Integer.valueOf(rowSplitted[18]));
		        	row.setProp20(Integer.valueOf(rowSplitted[19]));
		        	row.setProp21(Integer.valueOf(rowSplitted[20]));
		        	row.setProp22(Integer.valueOf(rowSplitted[21]));
		        	row.setProp23(Integer.valueOf(rowSplitted[22]));
		        	row.setProp24(Integer.valueOf(rowSplitted[23]));
		        	row.setProp25(Integer.valueOf(rowSplitted[24]));
		        	row.setProp26(Integer.valueOf(rowSplitted[25]));
		        	row.setProp27(Integer.valueOf(rowSplitted[26]));
		        	row.setProp28(Integer.valueOf(rowSplitted[27]));
		        	row.setProp29(Integer.valueOf(rowSplitted[28]));
		        	row.setProp30(Integer.valueOf(rowSplitted[29]));
		        	row.setProp31(Integer.valueOf(rowSplitted[30]));
		        	row.setProp32(Integer.valueOf(rowSplitted[31]));
		        	row.setProp33(Integer.valueOf(rowSplitted[32]));
		        	row.setProp34(Integer.valueOf(rowSplitted[33]));
		        	row.setProp35(Integer.valueOf(rowSplitted[34]));
		        	row.setProp36(Integer.valueOf(rowSplitted[35]));
		        	row.setProp37(Integer.valueOf(rowSplitted[36]));
		        	row.setProp38(Integer.valueOf(rowSplitted[37]));
		        	row.setProp39(Integer.valueOf(rowSplitted[38]));
		        	row.setProp40(Integer.valueOf(rowSplitted[39]));		      		       
		        
		        lista.add(row);
		    
		    }
		} finally {
		    //LineIterator.closeQuietly(it);
		    it.close();
		}
		
		logger.info("Size list: "+lista.size());
		logger.info("========================= TERMINO!!!!! ======================================");
       
	}
	
	
    private final void logMemory() {
    	Long max = Runtime.getRuntime().maxMemory() / 1048576;
    	Long total = Runtime.getRuntime().totalMemory() / 1048576;
    	Long free = Runtime.getRuntime().freeMemory() / 1048576;
    	Long used = total-free;
        logger.info("Max Memory: {} Mb", max);
        logger.info("Total Memory: {} Mb", total);
        logger.info("Free Memory: {} Mb", free);
        logger.info("Used Memory: {} Mb", used);
    }

}
