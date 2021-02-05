package com.hyfd.test;

public class test1 {

	public static int i = 0;
	
	
	public static void main(String [] args) {
		a();
		System.out.println(i);
		
		
	}
	
	public static int  a() {
		i +=1;
		if(i == 1) {
			a();
		}
		return i;
	}
	
}
