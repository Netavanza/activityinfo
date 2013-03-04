package org.activityinfo.geoadmin;

import java.awt.Color;

import org.activityinfo.geoadmin.model.AdminUnit;
import org.apache.commons.lang3.StringUtils;

import com.vividsolutions.jts.geom.Envelope;

public class ImportQualityEvaluator {
	private ImportTableModel tableModel;
	private ImportSource importSource;
	private ImportForm importForm;
	
	public static final Color FOREST_GREEN = Color.decode("#4AA02C");
	public static final Color FIREBRICK3 = Color.decode("#C11B17");
	public static final Color PINK = Color.decode("#F660AB");
	

	public ImportQualityEvaluator(ImportTableModel tableModel,
			ImportSource importSource, ImportForm importForm) {
		super();
		this.tableModel = tableModel;
		this.importSource = importSource;
		this.importForm = importForm;
	}
	
	public Color evaluate(int rowIndex) {
		AdminUnit parent = (AdminUnit) tableModel.getValueAt(rowIndex, ImportTableModel.PARENT_COLUMN);
		if(parent == null) {
			return Color.WHITE;
		}

		// if it doesn't intersect AT ALL with the parent 
		// this is a big problem
		Envelope parentBounds = GeoUtils.toEnvelope(parent.getBounds());
		if(!parentBounds.intersects(importSource.getEnvelope(rowIndex))) {
			return FIREBRICK3;
		}
		
		// if there are big differences between the names,
		// this is a big problem
		String parentName  = importSource.getAttributeStringValue(rowIndex, importForm.getParentAttribute());
		if(MatchUtils.distance(parent.getName(), parentName) > 2) {
			return FIREBRICK3;			
		}
		
		if(MatchUtils.distance(parent.getName(), parentName) > 0) {
			return PINK;			
		}
		
		
		if(!parentBounds.contains(importSource.getEnvelope(rowIndex))) {
			return PINK;
		}
		
		return FOREST_GREEN;
	}
}
