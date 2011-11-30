package org.sigmah.shared.report.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class ReportSubscriber extends BaseModelData{

	public ReportSubscriber(){
		
	}
	
	public ReportSubscriber(String email) {
		setEmail(email);
	}

	public void setEmail(String email) {
		set("email", email);
	}

	public String getEmail() {
		return (String) get("email");
	}
	
}
