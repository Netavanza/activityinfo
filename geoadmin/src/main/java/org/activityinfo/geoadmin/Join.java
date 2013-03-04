package org.activityinfo.geoadmin;

import org.activityinfo.geoadmin.model.AdminUnit;

public class Join {
	private AdminUnit unit;
	private int featureIndex;
	
	public Join(AdminUnit adminUnit, int bestFeature) {
		this.unit = adminUnit;
		this.featureIndex = bestFeature;
	}
	
	public AdminUnit getUnit() {
		return unit;
	}

	public int getFeatureIndex() {
		return featureIndex;
	}
}
