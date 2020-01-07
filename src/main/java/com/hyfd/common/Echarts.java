package com.hyfd.common;

import java.util.List;

public class Echarts {

	private String name;
	private String type;
	private String stack;
	private List<String> data;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStack() {
		return stack;
	}
	public void setStack(String stack) {
		this.stack = stack;
	}
	public List<String> getData() {
		return data;
	}
	public void setData(List<String> data) {
		this.data = data;
	}
	public Echarts(String name, String type, String stack, List<String> data) {
		super();
		this.name = name;
		this.type = type;
		this.stack = stack;
		this.data = data;
	}

	
	
}
