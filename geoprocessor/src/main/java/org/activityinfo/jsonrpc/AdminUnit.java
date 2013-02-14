package org.activityinfo.jsonrpc;

public class AdminUnit {
	private String id;
	private int levelId;
	private String name;
	
	public String getId() {
		return id;
	}
	
	public int getMachineId() {
		return Integer.parseInt(id.substring(id.lastIndexOf("/")+1));
	}
	
	public void setId(String id) {
		this.id = id;
	}
	public int getLevelId() {
		return levelId;
	}
	public void setLevelId(int levelId) {
		this.levelId = levelId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
