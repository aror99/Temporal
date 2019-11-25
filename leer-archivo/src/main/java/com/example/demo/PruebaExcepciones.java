package com.example.demo;

public class PruebaExcepciones {

	private static int id = (int)(Math.random()*((100-1)+1))+1;
	
	public static void main(String[] args) throws Exception {
		
		
		Exception e = null;
		try {
			metodo1();
		}catch(Exception e1) {
			//close(e);
			e = e1;
			throw new RuntimeException("["+id+"] Main",e);

		}finally {
			close(e);
		}
	}
	
	public static void metodo1() throws Exception {
		Exception e = null;
		try {
			metodo2();
		}catch(Exception e1) {
			//close(e);
			e = e1;
			throw new RuntimeException("["+id+"] Meotod 1 >> ["+e.getMessage()+"] ",e);

		}
	}
	
	public static void metodo2() {
		try {
			metodo3();
		}catch(Exception e) {
			throw new RuntimeException("["+id+"] Meotod 2 >> ["+e.getMessage()+"] ",e);
		}
	}
	
	public static void metodo3() {
		try {
			metodo4();
		}catch(Exception e) {
			throw new RuntimeException("["+id+"] Meotod 3 >> ["+e.getMessage()+"] ",e);
		}
	}
	
	public static void metodo4() {
		String x=null;
		x.toCharArray();
		
	}
	
	public static void close(Exception e) throws Exception {
				
		try {
			System.out.println("Cerrando...");
		
			String x=null;
			//x.toCharArray();
			System.out.println("Cerrado!!...");
		}catch(Exception e2 ) {
			if(e==null) {
				throw new RuntimeException("["+id+"] Error Cerrando MUY GRAVE!!!",e2);	
			}else {
				throw new RuntimeException("["+id+"] Error Cerrando MUY GRAVE!!! ["+e2.getMessage()+"]",e);
			}
		}
		//Suponemos que hubo un erro cerrando
		
	}
}
