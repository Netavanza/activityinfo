package org.activityinfo.geoadmin.model;

public class AdminUnit {
	private int id;
	private String name;
	private String code;
	private Bounds bounds;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Bounds getBounds() {
		return bounds;
	}
	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}
	
	@Override
	public String toString() {
		if(code == null) {
			return name;
		} else {
			return name + " (" + code + ")";
		}
	}
}
