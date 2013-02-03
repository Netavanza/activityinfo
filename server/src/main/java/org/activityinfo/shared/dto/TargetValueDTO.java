package org.activityinfo.shared.dto;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class TargetValueDTO extends BaseModelData implements EntityDTO{

	public static final String entityName = "TargetValue";
	
	public void setTargetId(int targetId){
		set("targetId", targetId);
	}
	
	public int getTargetId(){
		return (Integer) get("targetId");
	}
	
	public void setIndicatorId(int indicatorId){
		set("indicatorId", indicatorId);
	}
	
	public int getIndicatorId(){
		return (Integer) get("indicatorId");
	}
	
	public Double getValue() {
		return (Double) get("value");
	}

	public void setValue(Double value) {
		set("value", (Double) value);
	}
	
	public void setName(String name){
		set("name", name);
	}
	
	@Override
	public String getName(){
		return (String) get("name");
	}
		
	/* 
	 * no unique autogenerated ID for the table. use targetID + indicatorID instead.
	 * */
	@Override
	public int getId() {
		return 0;
	}

	@Override
	public String getEntityName() {
		return entityName;
	}
}
